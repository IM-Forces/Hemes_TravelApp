package com.example.hermes_travelapp.domain

/**
 * Repository interface defining the contract for managing itinerary activities.
 * This interface follows the Repository pattern to abstract the data source from the UI layer.
 */
interface ActivityRepository {
    
    /**
     * Retrieves a list of activities for a specific day of a trip.
     * @param tripId The unique identifier of the trip.
     * @param dayId The identifier of the day within the trip.
     * @return A list of [ItineraryItem] objects, typically sorted by time.
     */
    fun getActivitiesForDay(tripId: String, dayId: String): List<ItineraryItem>

    /**
     * Retrieves a single activity by its unique identifier.
     * @param activityId The unique identifier of the activity.
     * @return The [ItineraryItem] if found, null otherwise.
     */
    fun getActivityById(activityId: String): ItineraryItem?

    /**
     * Adds a new activity to the itinerary.
     * @param activity The [ItineraryItem] to be added.
     */
    fun addActivity(activity: ItineraryItem)

    /**
     * Updates an existing activity with new information.
     * @param activity The [ItineraryItem] containing updated data.
     */
    fun updateActivity(activity: ItineraryItem)

    /**
     * Removes an activity from the itinerary.
     * @param activityId The unique identifier of the activity to delete.
     */
    fun deleteActivity(activityId: String)
}
