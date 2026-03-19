package com.example.hermes_travelapp.data.repository

import android.util.Log
import com.example.hermes_travelapp.data.fakeDB.FakeTripDayDataSource
import com.example.hermes_travelapp.domain.TripDay
import com.example.hermes_travelapp.domain.TripDayRepository

/**
 * Implementation of the [TripDayRepository] interface.
 * This class acts as a bridge between the domain layer and the data source,
 * delegating all operations to [FakeTripDayDataSource].
 */
class TripDayRepositoryImpl : TripDayRepository {

    private companion object {
        const val TAG = "TripDayRepositoryImpl"
    }

    override fun getDaysForTrip(tripId: String): List<TripDay> {
        Log.d(TAG, "getDaysForTrip: tripId=$tripId")
        return FakeTripDayDataSource.getDaysForTrip(tripId)
    }

    override fun addDay(day: TripDay) {
        Log.d(TAG, "addDay: dayId=${day.id}, tripId=${day.tripId}")
        FakeTripDayDataSource.addDay(day)
    }

    override fun clearDaysForTrip(tripId: String) {
        Log.d(TAG, "clearDaysForTrip: tripId=$tripId")
        FakeTripDayDataSource.clearDaysForTrip(tripId)
    }
}
