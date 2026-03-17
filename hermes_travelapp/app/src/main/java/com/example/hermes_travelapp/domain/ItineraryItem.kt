package com.example.hermes_travelapp.domain

import java.time.LocalDate
import java.time.LocalTime

/**
 * Represents a single scheduled activity within a Trip's itinerary.
 *
 * @property id Unique identifier for the activity.
 * @property tripId Identifier for the trip this activity belongs to.
 * @property dayId Identifier for the specific day within the trip.
 * @property title Name of the activity (required).
 * @property description Detailed explanation of the activity (required).
 * @property date The date the activity takes place (required).
 * @property time The time the activity starts (required).
 * @property location Physical location or venue (optional).
 * @property cost Estimated cost in euros (optional).
 */
data class ItineraryItem(
    val id: String,
    val tripId: String,
    val dayId: String,
    val title: String,
    val description: String,
    val date: LocalDate,
    val time: LocalTime,
    val location: String? = null,
    val cost: Double? = null
) {
    /**
     * Validates if all required fields for an activity are present and valid.
     * Checks that IDs, title, and description are not blank.
     *
     * @return True if the activity meets the minimum data requirements.
     */
    fun isValid(): Boolean {
        return id.isNotBlank() &&
                tripId.isNotBlank() &&
                dayId.isNotBlank() &&
                title.isNotBlank() &&
                description.isNotBlank()
    }
}
