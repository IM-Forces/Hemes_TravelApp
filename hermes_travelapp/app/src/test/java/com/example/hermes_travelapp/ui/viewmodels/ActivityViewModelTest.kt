package com.example.hermes_travelapp.ui.viewmodels

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.hermes_travelapp.domain.repository.ActivityRepository
import com.example.hermes_travelapp.domain.model.ItineraryItem
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ActivityViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: ActivityRepository
    private lateinit var viewModel: ActivityViewModel

    @Before
    fun setUp() {
        // SOLUCIÓN: Mockeamos la clase Log de Android para que no falle en la JVM
        mockkStatic(Log::class)
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.i(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0

        repository = mockk(relaxed = true)
        
        viewModel = ActivityViewModel()
        
        val field = ActivityViewModel::class.java.getDeclaredField("repository")
        field.isAccessible = true
        field.set(viewModel, repository)
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun `test loadActivitiesForDay updates activities state`() {
        val tripId = "trip1"
        val dayId = "day1"
        val mockActivities = listOf(
            ItineraryItem("1", tripId, dayId, "Visit Museum", "Desc", LocalDate.now(), LocalTime.now())
        )
        every { repository.getActivitiesForDay(tripId, dayId) } returns mockActivities

        viewModel.loadActivitiesForDay(tripId, dayId)

        assertEquals(mockActivities, viewModel.activities.value)
        assertEquals(1, viewModel.dayCounts.value[dayId])
    }

    @Test
    fun `test addActivity calls repository and reloads`() {
        val tripId = "trip1"
        val dayId = "day1"
        val activity = ItineraryItem("1", tripId, dayId, "Visit Museum", "Desc", LocalDate.now(), LocalTime.now())
        
        viewModel.addActivity(activity)

        verify { repository.addActivity(activity) }
        verify { repository.getActivitiesForDay(tripId, dayId) }
    }

    @Test
    fun `test deleteActivity calls repository and reloads`() {
        val tripId = "trip1"
        val dayId = "day1"
        val activityId = "1"

        viewModel.deleteActivity(activityId, tripId, dayId)

        verify { repository.deleteActivity(activityId) }
        verify { repository.getActivitiesForDay(tripId, dayId) }
    }

    @Test
    fun `test validation rejects invalid activity`() {
        val invalidActivity = ItineraryItem("1", "trip1", "day1", "", "Desc", LocalDate.now(), LocalTime.now())
        
        viewModel.addActivity(invalidActivity)

        verify(exactly = 0) { repository.addActivity(any()) }
    }
}
