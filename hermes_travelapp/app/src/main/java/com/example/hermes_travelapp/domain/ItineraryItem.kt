package com.example.hermes_travelapp.domain

/** * Represents a single scheduled activity within a Trip's itinerary.
 */
data class ItineraryItem(
    val id: String,
    val time: String,
    val title: String,
    val location: String,
    val isCompleted: Boolean = false
) {

    /**
     * Validates if all required fields for an activity are non-blank.
     */
    fun isValid(): Boolean {
        // @TODO Implement strict validation for time formats and titles
        return id.isNotBlank() && time.isNotBlank() && title.isNotBlank()
    }

    /**
     * Future feature: Saves this item to the database under a specific trip.
     */
    fun save(tripId: String) {
        // @TODO Implement persistence via ItineraryRepository
    }

    /**
     * Future feature: Removes this activity from the trip's itinerary.
     */
    fun delete() {
        // @TODO Implement deletion logic in ItineraryRepository
    }

    /**
     * Future feature: Toggles the completion status of the activity.
     */
    fun toggleComplete() {
        // @TODO Update isCompleted state and persist
    }
}