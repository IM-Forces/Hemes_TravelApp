package com.example.hermes_travelapp.ui.viewmodels

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.hermes_travelapp.R
import com.example.hermes_travelapp.domain.model.Trip
import com.example.hermes_travelapp.domain.repository.TripRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Fake implementation of TripRepository for unit testing.
 */
class FakeTripRepository : TripRepository {
    private val trips = mutableListOf<Trip>()

    override fun getTrips(): List<Trip> = trips.toList()

    override fun addTrip(trip: Trip) {
        trips.add(trip)
    }

    override fun editTrip(trip: Trip) {
        val index = trips.indexOfFirst { it.id == trip.id }
        if (index != -1) trips[index] = trip
    }

    override fun deleteTrip(tripId: String) {
        trips.removeIf { it.id == tripId }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class TripListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: TripViewModel
    private lateinit var fakeRepository: FakeTripRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        // SOLUCIÓN: Mockeamos la clase Log de Android para que no falle en la JVM
        mockkStatic(Log::class)
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.i(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0

        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeTripRepository()
        viewModel = TripViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    @Test
    fun `test addTrip with valid data adds trip to state`() {
        val trip = Trip(
            title = "Atenas",
            startDate = "01/05/2024",
            endDate = "10/05/2024",
            description = "Viaje cultural"
        )
        
        val result = viewModel.addTrip(trip)
        
        assertTrue(result)
        assertEquals(1, viewModel.trips.value.size)
        assertEquals("Atenas", viewModel.trips.value[0].title)
    }

    @Test
    fun `test addTrip with empty title (implicit validation via dates in this VM)`() {
        val trip = Trip(
            title = "Test",
            startDate = "",
            endDate = "",
            description = "Desc"
        )
        
        val result = viewModel.addTrip(trip)
        
        assertFalse(result)
        // Ahora comparamos contra el ID del recurso
        assertEquals(R.string.error_required_dates, viewModel.errorMessageRes.value)
    }

    @Test
    fun `test addTrip with startDate after endDate triggers error`() {
        val trip = Trip(
            title = "Error Trip",
            startDate = "20/05/2024",
            endDate = "10/05/2024",
            description = "D"
        )
        
        val result = viewModel.addTrip(trip)
        
        assertFalse(result)
        // Ahora comparamos contra el ID del recurso
        assertEquals(R.string.error_invalid_range, viewModel.errorMessageRes.value)
    }

    @Test
    fun `test deleteTrip removes trip from state`() {
        val trip = Trip(id = "1", title = "T1", startDate = "01/01/2024", endDate = "02/01/2024", description = "D")
        fakeRepository.addTrip(trip)
        viewModel.loadTrips()
        
        viewModel.deleteTrip("1")
        
        assertTrue(viewModel.trips.value.isEmpty())
    }

    @Test
    fun `test editTrip reflects changes in state`() {
        val trip = Trip(id = "1", title = "Original", startDate = "01/01/2024", endDate = "02/01/2024", description = "D")
        fakeRepository.addTrip(trip)
        viewModel.loadTrips()
        
        val updatedTrip = trip.copy(title = "Actualizado")
        val result = viewModel.editTrip(updatedTrip)
        
        assertTrue(result)
        assertEquals("Actualizado", viewModel.trips.value[0].title)
    }
}
