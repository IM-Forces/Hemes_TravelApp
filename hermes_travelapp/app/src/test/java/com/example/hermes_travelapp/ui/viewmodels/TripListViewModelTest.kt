package com.example.hermes_travelapp.ui.viewmodels

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.hermes_travelapp.R
import com.example.hermes_travelapp.domain.model.Trip
import com.example.hermes_travelapp.domain.repository.ReservationRepository
import com.example.hermes_travelapp.domain.repository.TripRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TripListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: TripViewModel
    private lateinit var repository: TripRepository
    private lateinit var reservationRepository: ReservationRepository
    private val testDispatcher = UnconfinedTestDispatcher()
    private val tripsFlow = MutableStateFlow<List<Trip>>(emptyList())

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        // Mock Log
        mockkStatic(Log::class)
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.i(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0

        repository = mockk()
        reservationRepository = mockk(relaxed = true)
        every { repository.getTrips() } returns tripsFlow
        
        viewModel = TripViewModel(repository, reservationRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    @Test
    fun `test addTrip with valid data adds trip via repository`() = runTest {
        val trip = Trip(
            title = "Atenas",
            destinationCity = "Atenas",
            startDate = "01/05/2024",
            endDate = "10/05/2024",
            description = "Viaje cultural"
        )
        coEvery { repository.addTrip(any()) } just Runs
        
        val result = viewModel.addTrip(trip)
        
        assertTrue(result)
        coVerify { repository.addTrip(trip) }
    }

    @Test
    fun `test addTrip with empty title triggers error`() = runTest {
        val trip = Trip(
            title = "",
            destinationCity = "Atenas",
            startDate = "01/05/2024",
            endDate = "10/05/2024",
            description = "Desc"
        )
        
        val result = viewModel.addTrip(trip)
        
        assertFalse(result)
        assertEquals(R.string.error_field_required, viewModel.errorMessageRes.value)
    }

    @Test
    fun `test addTrip with empty city triggers error`() = runTest {
        val trip = Trip(
            title = "Atenas",
            destinationCity = "",
            startDate = "01/05/2024",
            endDate = "10/05/2024",
            description = "Desc"
        )
        
        val result = viewModel.addTrip(trip)
        
        assertFalse(result)
        assertEquals(R.string.error_field_required, viewModel.errorMessageRes.value)
    }

    @Test
    fun `test addTrip with invalid date range triggers error`() = runTest {
        val trip = Trip(
            title = "Error Trip",
            destinationCity = "London",
            startDate = "20/05/2024",
            endDate = "10/05/2024",
            description = "D"
        )
        
        val result = viewModel.addTrip(trip)
        
        assertFalse(result)
        assertEquals(R.string.error_invalid_range, viewModel.errorMessageRes.value)
    }

    @Test
    fun `test deleteTrip calls repository`() {
        val tripId = "1"
        coEvery { repository.deleteTrip(tripId) } just Runs
        
        viewModel.deleteTrip(tripId)
        
        coVerify { repository.deleteTrip(tripId) }
    }

    @Test
    fun `test editTrip calls repository`() = runTest {
        val trip = Trip(id = "1", title = "Original", destinationCity = "London", startDate = "01/01/2024", endDate = "02/01/2024", description = "D")
        coEvery { repository.editTrip(any()) } just Runs
        
        val updatedTrip = trip.copy(title = "Actualizado")
        val result = viewModel.editTrip(updatedTrip)
        
        assertTrue(result)
        coVerify { repository.editTrip(updatedTrip) }
    }
}
