package com.example.hermes_travelapp.data.fakeDB

import android.util.Log
import com.example.hermes_travelapp.domain.model.Trip
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FakeTripDataSourceTest {

    @Before
    fun setUp() {
        // Mock de Log para evitar errores de RuntimeException: Stub!
        mockkStatic(Log::class)
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
        every { Log.i(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>()) } returns 0

        // Limpieza del Singleton antes de cada test
        FakeTripDataSource.getTrips().forEach {
            FakeTripDataSource.deleteTrip(it.id)
        }
    }

    @Test
    fun `test addTrip aumenta el tamaño y el viaje es recuperable`() {
        val trip = Trip(id = "1", title = "París", startDate = "01/01/2024", endDate = "05/01/2024", description = "Eurotrip")
        
        FakeTripDataSource.addTrip(trip)
        
        val trips = FakeTripDataSource.getTrips()
        assertEquals(1, trips.size)
        assertEquals("París", trips[0].title)
    }

    @Test
    fun `test updateTrip modifica los campos correctamente`() {
        val trip = Trip(id = "1", title = "Original", startDate = "01/01/2024", endDate = "05/01/2024", description = "Desc")
        FakeTripDataSource.addTrip(trip)
        
        val updated = trip.copy(title = "Actualizado", budget = 500)
        FakeTripDataSource.updateTrip(updated)
        
        val result = FakeTripDataSource.getTrips().find { it.id == "1" }
        assertEquals("Actualizado", result?.title)
        assertEquals(500, result?.budget)
    }

    @Test
    fun `test deleteTrip elimina el viaje de la lista`() {
        val trip = Trip(id = "1", title = "Borrar", startDate = "01/01/2024", endDate = "05/01/2024", description = "D")
        FakeTripDataSource.addTrip(trip)
        
        FakeTripDataSource.deleteTrip("1")
        
        assertTrue(FakeTripDataSource.getTrips().isEmpty())
    }

    @Test
    fun `test getTrips devuelve todos los viajes almacenados`() {
        FakeTripDataSource.addTrip(Trip(id = "1", title = "T1", startDate = "01/01/2024", endDate = "02/01/2024", description = "D"))
        FakeTripDataSource.addTrip(Trip(id = "2", title = "T2", startDate = "01/01/2024", endDate = "02/01/2024", description = "D"))
        
        assertEquals(2, FakeTripDataSource.getTrips().size)
    }

    @Test
    fun `test addTrip no permite duplicados por ID`() {
        val trip1 = Trip(id = "same_id", title = "Primero", startDate = "01/01/2024", endDate = "02/01/2024", description = "D")
        val trip2 = Trip(id = "same_id", title = "Segundo", startDate = "01/01/2024", endDate = "02/01/2024", description = "D")
        
        FakeTripDataSource.addTrip(trip1)
        FakeTripDataSource.addTrip(trip2)
        
        val trips = FakeTripDataSource.getTrips()
        assertEquals(1, trips.size)
        assertEquals("Primero", trips[0].title)
    }
}
