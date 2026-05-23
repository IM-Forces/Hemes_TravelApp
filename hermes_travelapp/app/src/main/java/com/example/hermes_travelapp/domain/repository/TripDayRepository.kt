package com.example.hermes_travelapp.domain.repository

import com.example.hermes_travelapp.domain.model.TripDay
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the contract for managing trip days.
 */
interface TripDayRepository {

    /**
     * Retrieves all days associated with a specific trip.
     *
     * @param tripId The unique identifier of the trip.
     * @return A flow emitting a list of [TripDay] objects, typically sorted by day number.
     */
    fun getDaysForTrip(tripId: String): Flow<List<TripDay>>

    /**
     * Adds a new day to the trip's timeline.
     */
    suspend fun addDay(day: TripDay)

    /**
     * Removes all days associated with a specific trip.
     */
    suspend fun clearDaysForTrip(tripId: String)

    /**
     * Retrieves the last scheduled day for a trip.
     */
    suspend fun getLastDayForTrip(tripId: String): TripDay?

    /**
     * Deletes a specific day by its ID.
     */
    suspend fun deleteDay(dayId: String)

    /**
     * Updates an existing trip day.
     */
    suspend fun updateDay(day: TripDay)
}
