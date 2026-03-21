package com.example.hermes_travelapp.ui.viewmodels

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.hermes_travelapp.data.fakeDB.FakeTripDataSource
import com.example.hermes_travelapp.data.repository.TripRepositoryImpl
import com.example.hermes_travelapp.domain.model.Trip
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertTrue

class TripCrudLoggingTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: TripRepositoryImpl
    private lateinit var viewModel: TripViewModel

    @Before
    fun setUp() {
        // Mock de la clase Log de Android indicando tipos explícitos para evitar ambigüedad
        mockkStatic(Log::class)
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.i(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
        every { Log.v(any<String>(), any<String>()) } returns 0

        repository = TripRepositoryImpl()
        viewModel = TripViewModel(repository)
        
        // Limpiar el data source antes de cada prueba
        FakeTripDataSource.getTrips().forEach { FakeTripDataSource.deleteTrip(it.id) }
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun `test addTrip logging and execution flow`() {
        val trip = Trip(
            title = "Tokio",
            startDate = "10/11/2025",
            endDate = "20/11/2025",
            description = "Viaje a Japón"
        )

        val success = viewModel.addTrip(trip)
        
        assertTrue(success)
        
        // Verificamos que se llamaron a los niveles de log esperados usando matchers
        verify { Log.d(any<String>(), match { it.contains("addTrip") || it.contains("attempting") }) }
        verify { Log.i(any<String>(), match { it.contains("added successfully") }) }
    }

    @Test
    fun `test validation failure logging`() {
        val invalidTrip = Trip(
            title = "", // Título vacío para forzar error
            startDate = "10/11/2025",
            endDate = "20/11/2025",
            description = "Desc"
        )

        viewModel.addTrip(invalidTrip)

        // Verificamos que se registra el error en el log
        verify { Log.e(any<String>(), match { it.contains("Validation failed") || it.contains("blank") }) }
    }

    @Test
    fun `test deleteTrip logging flow`() {
        val tripId = "test_id"
        
        viewModel.deleteTrip(tripId)

        verify { Log.d(any<String>(), match { it.contains("delete") }) }
        verify { Log.i(any<String>(), match { it.contains("deleted") }) }
    }
}
