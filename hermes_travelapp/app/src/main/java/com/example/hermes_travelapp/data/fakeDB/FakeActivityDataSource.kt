package com.example.hermes_travelapp.data.fakeDB

import android.util.Log
import com.example.hermes_travelapp.domain.ItineraryItem
import java.time.LocalDate
import java.time.LocalTime

/**
 * Singleton data source providing in-memory storage for Itinerary Activities.
 */
object FakeActivityDataSource {
    private const val TAG = "FakeActivityDataSource"
    
    private val activities = mutableListOf<ItineraryItem>()

    init {
        // Pre-load sample data
        val today = LocalDate.now()
        
        activities.addAll(listOf(
            ItineraryItem(
                id = "act_1",
                tripId = "grecia_trip",
                dayId = "day1",
                title = "Desayuno frente al Partenón",
                description = "Disfruta de un desayuno tradicional con vistas increíbles.",
                date = today,
                time = LocalTime.of(9, 0),
                location = "Restaurante Acrópolis",
                cost = 15.0
            ),
            ItineraryItem(
                id = "act_2",
                tripId = "grecia_trip",
                dayId = "day1",
                title = "Visita al Museo de la Acrópolis",
                description = "Explora los tesoros de la antigua Grecia.",
                date = today,
                time = LocalTime.of(11, 0),
                location = "Museo de la Acrópolis",
                cost = 25.0
            ),
            ItineraryItem(
                id = "act_3",
                tripId = "grecia_trip",
                dayId = "day2",
                title = "Almuerzo en Plaka",
                description = "Comida típica en el barrio más antiguo de Atenas.",
                date = today.plusDays(1),
                time = LocalTime.of(14, 0),
                location = "Plaka, Atenas",
                cost = 20.0
            ),
            ItineraryItem(
                id = "act_4",
                tripId = "kenia_trip",
                dayId = "day1",
                title = "Safari al amanecer",
                description = "Primer contacto con la fauna salvaje en Masai Mara.",
                date = today.plusMonths(1),
                time = LocalTime.of(6, 30),
                location = "Masai Mara",
                cost = 150.0
            )
        ))
        Log.d(TAG, "Initialized with ${activities.size} sample activities.")
    }

    /**
     * Returns activities filtered by trip and day, sorted by time.
     */
    fun getActivitiesForDay(tripId: String, dayId: String): List<ItineraryItem> {
        val filtered = activities
            .filter { it.tripId == tripId && it.dayId == dayId }
            .sortedBy { it.time }
        Log.d(TAG, "Fetched ${filtered.size} activities for trip $tripId, day $dayId.")
        return filtered
    }

    /**
     * Returns a single activity by its unique ID.
     */
    fun getActivityById(activityId: String): ItineraryItem? {
        val activity = activities.find { it.id == activityId }
        Log.d(TAG, "getActivityById: $activityId found=${activity != null}")
        return activity
    }

    /**
     * Adds a new activity to the in-memory list.
     */
    fun addActivity(activity: ItineraryItem) {
        activities.add(activity)
        Log.d(TAG, "Added activity: ${activity.title} (ID: ${activity.id})")
    }

    /**
     * Updates an existing activity by matching its ID.
     */
    fun updateActivity(updatedActivity: ItineraryItem) {
        val index = activities.indexOfFirst { it.id == updatedActivity.id }
        if (index != -1) {
            activities[index] = updatedActivity
            Log.d(TAG, "Updated activity: ${updatedActivity.title} (ID: ${updatedActivity.id})")
        } else {
            Log.w(TAG, "Update failed: Activity with ID ${updatedActivity.id} not found.")
        }
    }

    /**
     * Deletes an activity from the list by ID.
     */
    fun deleteActivity(activityId: String) {
        val removed = activities.removeIf { it.id == activityId }
        if (removed) {
            Log.d(TAG, "Deleted activity with ID: $activityId")
        } else {
            Log.w(TAG, "Delete failed: Activity with ID $activityId not found.")
        }
    }
}
