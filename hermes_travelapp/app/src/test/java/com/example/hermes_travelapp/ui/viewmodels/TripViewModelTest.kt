package com.example.hermes_travelapp.ui.viewmodels

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.hermes_travelapp.R
import com.example.hermes_travelapp.domain.model.Trip
import com.example.hermes_travelapp.domain.repository.TripRepository
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TripViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: TripRepository
    private lateinit var viewModel: TripViewModel

    @Before
    fun setUp() {
        // SOLUCIÓN: Mockeamos la clase Log de Android para que no falle en la JVM
        mockkStatic(Log::class)
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.i(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0

        repository = mockk(relaxed = true)
        every { repository.getTrips() } returns emptyList()
        viewModel = TripViewModel(repository)
    }

    @After
    fun tearDown() {
        // Limpiamos los mocks estáticos después de cada test
        unmockkStatic(Log::class)
    }

    @Test
    fun `test addTrip successfully`() {
        val trip = Trip(title = "Test Trip", startDate = "01/01/2024", endDate = "10/01/2024", description = "Desc")
        
        val result = viewModel.addTrip(trip)
        
        assertTrue(result)
        verify { repository.addTrip(trip) }
        assertNull(viewModel.errorMessageRes.value)
    }

    @Test
    fun `test editTrip successfully`() {
        val trip = Trip(id = "1", title = "Updated Trip", startDate = "01/01/2024", endDate = "10/01/2024", description = "Desc")
        
        val result = viewModel.editTrip(trip)
        
        assertTrue(result)
        verify { repository.editTrip(trip) }
        assertNull(viewModel.errorMessageRes.value)
    }

    @Test
    fun `test deleteTrip successfully`() {
        val tripId = "1"
        
        viewModel.deleteTrip(tripId)
        
        verify { repository.deleteTrip(tripId) }
    }

    @Test
    fun `test loadTrips retrieves all trips`() {
        val trips = listOf(
            Trip(title = "Trip 1", startDate = "01/01/2024", endDate = "05/01/2024", description = "D1"),
            Trip(title = "Trip 2", startDate = "10/01/2024", endDate = "15/01/2024", description = "D2")
        )
        every { repository.getTrips() } returns trips
        
        viewModel.loadTrips()
        
        assertEquals(2, viewModel.trips.value.size)
        assertEquals("Trip 1", viewModel.trips.value[0].title)
    }

    @Test
    fun `test validation rejects empty dates`() {
        val trip = Trip(title = "Error", startDate = "", endDate = "", description = "No dates")
        
        val result = viewModel.addTrip(trip)
        
        assertFalse(result)
        // Comprobamos contra el ID del recurso de error
        assertEquals(R.string.error_required_dates, viewModel.errorMessageRes.value)
    }

    @Test
    fun `test validation rejects invalid date range`() {
        val trip = Trip(title = "Range Error", startDate = "20/01/2024", endDate = "10/01/2024", description = "Desc")
        
        val result = viewModel.addTrip(trip)
        
        assertFalse(result)
        // Comprobamos contra el ID del recurso de error de rango
        assertEquals(R.string.error_invalid_range, viewModel.errorMessageRes.value)
    }
}
