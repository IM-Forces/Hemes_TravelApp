package com.example.hermes_travelapp.data.fakeDB

import android.util.Log
import com.example.hermes_travelapp.domain.model.ItineraryItem
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class FakeActivityDataSourceTest {

    private val tripId = "test_trip"
    private val dayId = "test_day"

    @Before
    fun setUp() {
        // Mock de Log para evitar errores de ejecución en tests unitarios
        mockkStatic(Log::class)
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
        every { Log.i(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>()) } returns 0

        // Limpieza manual de los datos (al ser un Singleton persistente en tests)
        val allActivities = FakeActivityDataSource.getActivitiesForDay(tripId, dayId)
        allActivities.forEach { FakeActivityDataSource.deleteActivity(it.id) }
        
        // También limpiamos los datos precargados para los tests
        FakeActivityDataSource.deleteActivity("act_1")
        FakeActivityDataSource.deleteActivity("act_2")
        FakeActivityDataSource.deleteActivity("act_3")
        FakeActivityDataSource.deleteActivity("act_4")
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun `test addActivity increases the count and is retrievable`() {
        val activity = createSampleActivity("1", "Visita Museo")
        
        FakeActivityDataSource.addActivity(activity)
        
        val result = FakeActivityDataSource.getActivitiesForDay(tripId, dayId)
        assertTrue(result.any { it.id == "1" })
        assertEquals("Visita Museo", result.find { it.id == "1" }?.title)
    }

    @Test
    fun `test updateActivity modifies fields correctly`() {
        val activity = createSampleActivity("2", "Almuerzo")
        FakeActivityDataSource.addActivity(activity)
        
        val updatedActivity = activity.copy(title = "Cena", cost = 50.0)
        FakeActivityDataSource.updateActivity(updatedActivity)
        
        val result = FakeActivityDataSource.getActivityById("2")
        assertEquals("Cena", result?.title)
        assertEquals(50.0, result?.cost)
    }

    @Test
    fun `test deleteActivity removes the activity`() {
        val activity = createSampleActivity("3", "Borrar")
        FakeActivityDataSource.addActivity(activity)
        
        FakeActivityDataSource.deleteActivity("3")
        
        val result = FakeActivityDataSource.getActivityById("3")
        assertNull(result)
    }

    @Test
    fun `test getActivitiesForDay returns sorted activities by time`() {
        val act1 = createSampleActivity("A1", "Tarde", LocalTime.of(18, 0))
        val act2 = createSampleActivity("A2", "Mañana", LocalTime.of(9, 0))
        
        FakeActivityDataSource.addActivity(act1)
        FakeActivityDataSource.addActivity(act2)
        
        val result = FakeActivityDataSource.getActivitiesForDay(tripId, dayId)
        assertEquals("Mañana", result[0].title)
        assertEquals("Tarde", result[1].title)
    }

    @Test
    fun `test activity validation with empty title fails`() {
        val invalidActivity = createSampleActivity("4", "").copy(description = "Desc")
        // La validación ocurre en el modelo o ViewModel, aquí comprobamos el método isValid()
        assertFalse(invalidActivity.isValid())
    }

    @Test
    fun `test activity validation with empty description fails`() {
        val invalidActivity = createSampleActivity("5", "Title").copy(description = "")
        assertFalse(invalidActivity.isValid())
    }

    private fun createSampleActivity(
        id: String, 
        title: String, 
        time: LocalTime = LocalTime.now()
    ): ItineraryItem {
        return ItineraryItem(
            id = id,
            tripId = tripId,
            dayId = dayId,
            title = title,
            description = "Descripción de prueba",
            date = LocalDate.now(),
            time = time,
            location = "Lugar",
            cost = 10.0
        )
    }
}
