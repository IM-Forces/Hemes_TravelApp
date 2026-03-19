package com.example.hermes_travelapp.domain

import java.time.LocalDate

/**
 * Represents a single day within a specific trip's itinerary.
 *
 * @property id Unique identifier for the day.
 * @property tripId Identifier for the trip this day belongs to.
 * @property dayNumber The sequential number of the day in the trip (e.g., Day 1, Day 2).
 * @property date The specific calendar date for this day.
 * @property subtitle A short description or title for the day's main focus (optional).
 */
data class TripDay(
    val id: String,
    val tripId: String,
    val dayNumber: Int,
    val date: LocalDate,
    val subtitle: String = ""
)
