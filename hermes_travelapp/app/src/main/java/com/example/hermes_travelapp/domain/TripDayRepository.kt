package com.example.hermes_travelapp.domain

/**
 * Repository interface defining the contract for managing trip days.
 * This interface follows the Repository pattern to abstract the data source from the UI layer.
 */
interface TripDayRepository {

    /**
     * Retrieves a list of days for a specific trip.
     * @param tripId The unique identifier of the trip.
     * @return A list of [TripDay] objects, typically sorted by day number.
     */
    fun getDaysForTrip(tripId: String): List<TripDay>

    /**
     * Adds a new day to the trip's itinerary.
     * @param day The [TripDay] to be added.
     */
    fun addDay(day: TripDay)

    /**
     * Removes all days associated with a specific trip.
     * @param tripId The unique identifier of the trip to clear.
     */
    fun clearDaysForTrip(tripId: String)
}
