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
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TripViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: TripRepository
    private lateinit var reservationRepository: ReservationRepository
    private lateinit var viewModel: TripViewModel
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
    fun `test addTrip successfully`() = runTest {
        val trip = Trip(
            title = "Test Trip",
            destinationCity = "London",
            startDate = "01/01/2024",
            endDate = "10/01/2024",
            description = "Desc"
        )
        coEvery { repository.addTrip(any()) } just Runs
        
        val result = viewModel.addTrip(trip)
        
        assertTrue(result)
        coVerify { repository.addTrip(trip) }
        assertNull(viewModel.errorMessageRes.value)
    }

    @Test
    fun `test editTrip successfully`() = runTest {
        val trip = Trip(
            id = "1",
            title = "Updated Trip",
            destinationCity = "Paris",
            startDate = "01/01/2024",
            endDate = "10/01/2024",
            description = "Desc"
        )
        coEvery { repository.editTrip(any()) } just Runs
        
        val result = viewModel.editTrip(trip)
        
        assertTrue(result)
        coVerify { repository.editTrip(trip) }
        assertNull(viewModel.errorMessageRes.value)
    }

    @Test
    fun `test deleteTrip successfully`() {
        val tripId = "1"
        coEvery { repository.deleteTrip(tripId) } just Runs
        
        viewModel.deleteTrip(tripId)
        
        coVerify { repository.deleteTrip(tripId) }
    }

    @Test
    fun `test loadTrips retrieves all trips`() {
        val trips = listOf(
            Trip(title = "Trip 1", startDate = "01/01/2024", endDate = "05/01/2024", description = "D1"),
            Trip(title = "Trip 2", startDate = "10/01/2024", endDate = "15/01/2024", description = "D2")
        )
        
        // Emitting trips through the flow
        tripsFlow.value = trips
        
        assertEquals(2, viewModel.trips.value.size)
        assertEquals("Trip 1", viewModel.trips.value[0].title)
    }

    @Test
    fun `test validation rejects empty city`() = runTest {
        val trip = Trip(title = "No City", destinationCity = "", startDate = "01/01/2024", endDate = "10/01/2024", description = "Desc")
        
        val result = viewModel.addTrip(trip)
        
        assertFalse(result)
        assertEquals(R.string.error_field_required, viewModel.errorMessageRes.value)
    }

    @Test
    fun `test validation rejects empty dates`() = runTest {
        val trip = Trip(title = "Error", destinationCity = "London", startDate = "", endDate = "", description = "No dates")
        
        val result = viewModel.addTrip(trip)
        
        assertFalse(result)
        assertEquals(R.string.error_required_dates, viewModel.errorMessageRes.value)
    }

    @Test
    fun `test validation rejects invalid date range`() = runTest {
        val trip = Trip(title = "Range Error", destinationCity = "London", startDate = "20/01/2024", endDate = "10/01/2024", description = "Desc")
        
        val result = viewModel.addTrip(trip)
        
        assertFalse(result)
        assertEquals(R.string.error_invalid_range, viewModel.errorMessageRes.value)
    }
}
