package com.example.hermes_travelapp.data.fakeDB

import android.util.Log
import com.example.hermes_travelapp.domain.Trip

/**
 * Singleton data source providing in-memory storage for Trips.
 * Reactivity is now managed by the ViewModel's StateFlow.
 */
object FakeTripDataSource {
    private const val TAG = "FakeTripDataSource"
    
    private val _trips = mutableListOf<Trip>()

    init {
        // La lista comienza vacía.
        Log.d(TAG, "Initialized with 0 trips.")
    }

    /**
     * Returns all trips in the list.
     */
    fun getTrips(): List<Trip> {
        Log.d(TAG, "Fetching all trips. Total: ${_trips.size}")
        return _trips.toList()
    }

    /**
     * Adds a new trip to the in-memory list.
     */
    fun addTrip(trip: Trip) {
        _trips.add(trip)
        Log.d(TAG, "Added trip: ${trip.title} (ID: ${trip.id})")
    }

    /**
     * Updates an existing trip by matching its ID.
     */
    fun updateTrip(updatedTrip: Trip) {
        val index = _trips.indexOfFirst { it.id == updatedTrip.id }
        if (index != -1) {
            _trips[index] = updatedTrip
            Log.d(TAG, "Updated trip: ${updatedTrip.title} (ID: ${updatedTrip.id})")
        } else {
            Log.w(TAG, "Update failed: Trip with ID ${updatedTrip.id} not found.")
        }
    }

    /**
     * Deletes a trip from the list by ID.
     */
    fun deleteTrip(tripId: String) {
        _trips.removeIf { it.id == tripId }
        Log.d(TAG, "Deleted trip with ID: $tripId")
    }
}
