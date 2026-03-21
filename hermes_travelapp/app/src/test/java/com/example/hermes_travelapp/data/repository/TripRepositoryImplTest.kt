package com.example.hermes_travelapp.data.repository

import android.util.Log
import com.example.hermes_travelapp.data.fakeDB.FakeTripDataSource
import com.example.hermes_travelapp.domain.model.Trip
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TripRepositoryImplTest {

    private lateinit var repository: TripRepositoryImpl

    @Before
    fun setUp() {
        // Mock de Log para evitar errores de RuntimeException: Stub! en entorno JVM
        mockkStatic(Log::class)
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.i(any<String>(), any<String>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>()) } returns 0
        
        repository = TripRepositoryImpl()
        
        // Limpiamos el FakeTripDataSource antes de cada test para asegurar independencia
        val trips = FakeTripDataSource.getTrips()
        trips.forEach { FakeTripDataSource.deleteTrip(it.id) }
    }
    
    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun testAddTripAndGetTrips() {
        val trip = Trip(
            id = "1", 
            title = "Atenas", 
            startDate = "01/05/2024", 
            endDate = "10/05/2024", 
            description = "Visita cultural"
        )
        
        repository.addTrip(trip)
        val trips = repository.getTrips()
        
        assertEquals(1, trips.size)
        assertEquals("Atenas", trips[0].title)
    }

    @Test
    fun testEditTripUpdatesCorrectValue() {
        val trip = Trip(id = "1", title = "Roma", startDate = "01/01/2024", endDate = "10/01/2024", description = "Desc")
        repository.addTrip(trip)
        
        val updatedTrip = trip.copy(title = "Roma Actualizada", budget = 2000)
        repository.editTrip(updatedTrip)
        
        val trips = repository.getTrips()
        assertEquals(1, trips.size)
        assertEquals("Roma Actualizada", trips[0].title)
        assertEquals(2000, trips[0].budget)
    }

    @Test
    fun testDeleteTripRemovesFromSource() {
        val trip = Trip(id = "1", title = "Londres", startDate = "01/01/2024", endDate = "10/01/2024", description = "D")
        repository.addTrip(trip)
        
        repository.deleteTrip("1")
        
        val trips = repository.getTrips()
        assertTrue(trips.isEmpty())
    }

    @Test
    fun testGetTripsReturnsEmptyListInitially() {
        val trips = repository.getTrips()
        assertTrue(trips.isEmpty())
    }
}
