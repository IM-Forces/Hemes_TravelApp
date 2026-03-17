package com.example.hermes_travelapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.hermes_travelapp.data.repository.ActivityRepositoryImpl
import com.example.hermes_travelapp.domain.ActivityRepository
import com.example.hermes_travelapp.domain.ItineraryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for managing the state and business logic of activities within a trip.
 * This class follows the MVVM pattern by interacting with the Repository layer.
 */
class ActivityViewModel : ViewModel() {

    private companion object {
        const val TAG = "ActivityViewModel"
    }

    private val repository: ActivityRepository = ActivityRepositoryImpl()

    private val _activities = MutableStateFlow<List<ItineraryItem>>(emptyList())
    /**
     * Observable stream of activities for the currently selected day.
     */
    val activities: StateFlow<List<ItineraryItem>> = _activities.asStateFlow()

    private val _dayCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    /**
     * Observable stream of activity counts for each day ID.
     */
    val dayCounts: StateFlow<Map<String, Int>> = _dayCounts.asStateFlow()

    /**
     * Loads activities for a specific day and updates the [activities] StateFlow.
     * @param tripId The unique identifier of the trip.
     * @param dayId The identifier of the day within the trip.
     */
    fun loadActivitiesForDay(tripId: String, dayId: String) {
        val result = repository.getActivitiesForDay(tripId, dayId)
        _activities.value = result
        Log.d(TAG, "loadActivitiesForDay: loaded ${result.size} activities for trip=$tripId, day=$dayId")
        // Update individual day count as well
        val currentCounts = _dayCounts.value.toMutableMap()
        currentCounts[dayId] = result.size
        _dayCounts.value = currentCounts
    }

    /**
     * Loads counts for all days of a trip.
     * @param tripId The trip ID.
     * @param dayIds List of all day IDs to fetch counts for.
     */
    fun loadAllDayCounts(tripId: String, dayIds: List<String>) {
        val counts = dayIds.associateWith { dayId ->
            repository.getActivitiesForDay(tripId, dayId).size
        }
        _dayCounts.value = counts
        Log.d(TAG, "loadAllDayCounts: loaded counts for ${dayIds.size} days")
    }

    /**
     * Adds a new activity after validation and reloads the daily list.
     * @param activity The [ItineraryItem] to add.
     */
    fun addActivity(activity: ItineraryItem) {
        if (!activity.isValid()) {
            Log.e(TAG, "addActivity: validation failed for activity ${activity.id}")
            return
        }
        repository.addActivity(activity)
        Log.i(TAG, "addActivity: successfully added ${activity.id}")
        loadActivitiesForDay(activity.tripId, activity.dayId)
    }

    /**
     * Updates an existing activity after validation and reloads the daily list.
     * @param activity The [ItineraryItem] with updated information.
     */
    fun updateActivity(activity: ItineraryItem) {
        if (!activity.isValid()) {
            Log.e(TAG, "updateActivity: validation failed for activity ${activity.id}")
            return
        }
        repository.updateActivity(activity)
        Log.i(TAG, "updateActivity: successfully updated ${activity.id}")
        loadActivitiesForDay(activity.tripId, activity.dayId)
    }

    /**
     * Deletes an activity by ID and reloads the daily list.
     * @param activityId The unique identifier of the activity to delete.
     * @param tripId The trip ID to reload the correct day.
     * @param dayId The day ID to reload the correct day.
     */
    fun deleteActivity(activityId: String, tripId: String, dayId: String) {
        repository.deleteActivity(activityId)
        Log.i(TAG, "deleteActivity: successfully deleted $activityId")
        loadActivitiesForDay(tripId, dayId)
    }
}
