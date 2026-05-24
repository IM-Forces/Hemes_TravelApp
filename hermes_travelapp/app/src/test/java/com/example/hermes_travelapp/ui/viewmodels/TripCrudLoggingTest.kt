package com.example.hermes_travelapp.ui.viewmodels

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.hermes_travelapp.domain.model.Trip
import com.example.hermes_travelapp.domain.repository.ReservationRepository
import com.example.hermes_travelapp.domain.repository.TripRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TripCrudLoggingTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: TripRepository
    private lateinit var reservationRepository: ReservationRepository
    private lateinit var viewModel: TripViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        // Mock de la clase Log de Android con tipos explícitos para evitar errores de inferencia
        mockkStatic(Log::class)
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.i(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
        every { Log.v(any<String>(), any<String>()) } returns 0

        repository = mockk(relaxed = true)
        reservationRepository = mockk(relaxed = true)
        viewModel = TripViewModel(repository, reservationRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    @Test
    fun `test addTrip logging and execution flow`() = runTest {
        val trip = Trip(
            title = "Tokio",
            destinationCity = "Tokio",
            startDate = "10/11/2025",
            endDate = "20/11/2025",
            description = "Viaje a Japón"
        )

        val success = viewModel.addTrip(trip)
        
        assertTrue(success)
        
        // Verificamos que se llamaron a los niveles de log esperados
        verify { Log.d(any<String>(), match<String> { it.contains("addTrip") || it.contains("attempting") }) }
        verify { Log.i(any<String>(), match<String> { it.contains("created successfully") }) }
    }

    @Test
    fun `test validation failure logging`() = runTest {
        val invalidTrip = Trip(
            title = "", // Título vacío para forzar error
            startDate = "10/11/2025",
            endDate = "20/11/2025",
            description = "Desc"
        )

        viewModel.addTrip(invalidTrip)

        // Verificamos que se registra el error en el log
        verify { Log.e(any<String>(), match<String> { it.contains("Validation failed") || it.contains("blank") }) }
    }

    @Test
    fun `test deleteTrip logging flow`() {
        val tripId = "test_id"
        
        viewModel.deleteTrip(tripId)

        verify { Log.d(any<String>(), match<String> { it.contains("deleteTrip") }) }
        verify { Log.i(any<String>(), match<String> { it.contains("deleted successfully") }) }
    }
}
