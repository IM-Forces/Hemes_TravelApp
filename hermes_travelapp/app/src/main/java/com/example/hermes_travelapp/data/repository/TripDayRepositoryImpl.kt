package com.example.hermes_travelapp.data.repository

import android.util.Log
import com.example.hermes_travelapp.data.database.dao.TripDayDao
import com.example.hermes_travelapp.data.database.mapper.toDomain
import com.example.hermes_travelapp.data.database.mapper.toEntity
import com.example.hermes_travelapp.domain.model.TripDay
import com.example.hermes_travelapp.domain.repository.TripDayRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of the [TripDayRepository] interface using Room database.
 */
class TripDayRepositoryImpl @Inject constructor(
    private val tripDayDao: TripDayDao
) : TripDayRepository {

    private companion object {
        const val TAG = "TripDayRepositoryImpl"
    }

    override fun getDaysForTrip(tripId: String): Flow<List<TripDay>> {
        Log.d(TAG, "getDaysForTrip: tripId=$tripId")
        return tripDayDao.getDaysForTrip(tripId).map { entities ->
            entities.map { it.toDomain() }
        }.also {
            Log.i(TAG, "getDaysForTrip: Flow created successfully for tripId=$tripId")
        }
    }

    override suspend fun addDay(day: TripDay) {
        try {
            Log.d(TAG, "addDay: dayId=${day.id}, tripId=${day.tripId}")
            tripDayDao.insertTripDay(day.toEntity())
            Log.i(TAG, "addDay successful: dayId=${day.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Error adding day: ${e.message}", e)
            throw e
        }
    }

    override suspend fun clearDaysForTrip(tripId: String) {
        try {
            Log.d(TAG, "clearDaysForTrip: tripId=$tripId")
            tripDayDao.deleteDaysByTripId(tripId)
            Log.i(TAG, "clearDaysForTrip successful: tripId=$tripId")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing days: ${e.message}", e)
            throw e
        }
    }

    override suspend fun getLastDayForTrip(tripId: String): TripDay? {
        return try {
            Log.d(TAG, "getLastDayForTrip: tripId=$tripId")
            val result = tripDayDao.getLastDayForTrip(tripId)?.toDomain()
            Log.i(TAG, "getLastDayForTrip successful: tripId=$tripId")
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error getting last day: ${e.message}", e)
            throw e
        }
    }

    override suspend fun deleteDay(dayId: String) {
        try {
            Log.d(TAG, "deleteDay: dayId=$dayId")
            tripDayDao.deleteDayById(dayId)
            Log.i(TAG, "deleteDay successful: dayId=$dayId")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting day: ${e.message}", e)
            throw e
        }
    }

    override suspend fun updateDay(day: TripDay) {
        try {
            Log.d(TAG, "updateDay: dayId=${day.id}")
            tripDayDao.insertTripDay(day.toEntity())
            Log.i(TAG, "updateDay successful: dayId=${day.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating day: ${e.message}", e)
            throw e
        }
    }
}
