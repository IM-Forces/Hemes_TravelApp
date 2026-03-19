package com.example.hermes_travelapp.data.fakeDB

import android.util.Log
import com.example.hermes_travelapp.domain.TripDay
import java.time.LocalDate

/**
 * Singleton data source providing in-memory storage for Trip Days.
 */
object FakeTripDayDataSource {
    private const val TAG = "FakeTripDayDataSource"
    
    private val days = mutableListOf<TripDay>()

    init {
        // Pre-load sample data for Grecia Trip
        val today = LocalDate.now()
        days.addAll(listOf(
            TripDay("day1", "grecia_trip", 1, today, "Llegada a Atenas"),
            TripDay("day2", "grecia_trip", 2, today.plusDays(1), "Acrópolis y Plaka"),
            TripDay("day3", "grecia_trip", 3, today.plusDays(2), "Museos de Atenas"),
            TripDay("day4", "grecia_trip", 4, today.plusDays(3), "Viaje a Mykonos")
        ))
        Log.d(TAG, "Initialized with ${days.size} sample days.")
    }

    /**
     * Returns days for a specific trip, sorted by day number.
     */
    fun getDaysForTrip(tripId: String): List<TripDay> {
        val filtered = days.filter { it.tripId == tripId }.sortedBy { it.dayNumber }
        Log.d(TAG, "Fetched ${filtered.size} days for trip $tripId.")
        return filtered
    }

    /**
     * Adds a new day to the in-memory list.
     */
    fun addDay(day: TripDay) {
        days.add(day)
        Log.d(TAG, "Added day ${day.dayNumber} for trip ${day.tripId}.")
    }

    /**
     * Clears all days for a trip.
     */
    fun clearDaysForTrip(tripId: String) {
        val removed = days.removeIf { it.tripId == tripId }
        if (removed) {
            Log.d(TAG, "Cleared all days for trip: $tripId")
        }
    }
}
