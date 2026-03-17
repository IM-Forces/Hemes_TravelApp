package com.example.hermes_travelapp.data.repository

import android.util.Log
import com.example.hermes_travelapp.data.fakeDB.FakeActivityDataSource
import com.example.hermes_travelapp.domain.ActivityRepository
import com.example.hermes_travelapp.domain.ItineraryItem

/**
 * Implementation of the [ActivityRepository] interface.
 * This class acts as a bridge between the domain layer and the data source,
 * delegating all operations to [FakeActivityDataSource].
 */
class ActivityRepositoryImpl : ActivityRepository {

    private companion object {
        const val TAG = "ActivityRepositoryImpl"
    }

    override fun getActivitiesForDay(tripId: String, dayId: String): List<ItineraryItem> {
        Log.d(TAG, "getActivitiesForDay: tripId=$tripId, dayId=$dayId")
        return FakeActivityDataSource.getActivitiesForDay(tripId, dayId)
    }

    override fun getActivityById(activityId: String): ItineraryItem? {
        Log.d(TAG, "getActivityById: activityId=$activityId")
        return FakeActivityDataSource.getActivityById(activityId)
    }

    override fun addActivity(activity: ItineraryItem) {
        Log.d(TAG, "addActivity: activityId=${activity.id}")
        FakeActivityDataSource.addActivity(activity)
    }

    override fun updateActivity(activity: ItineraryItem) {
        Log.d(TAG, "updateActivity: activityId=${activity.id}")
        FakeActivityDataSource.updateActivity(activity)
    }

    override fun deleteActivity(activityId: String) {
        Log.d(TAG, "deleteActivity: activityId=$activityId")
        FakeActivityDataSource.deleteActivity(activityId)
    }
}
