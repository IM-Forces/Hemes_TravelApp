package com.example.hermes_travelapp.domain

import android.util.Log
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * Utility function to generate all days for a given trip based on its start and end dates.
 * It clears existing days for the trip first to handle updates correctly.
 *
 * @param trip The trip to generate days for.
 * @param repository The repository to manage trip day data.
 */
fun generateDaysForTrip(trip: Trip, repository: TripDayRepository) {
    // Clear existing days to support editing (re-generating) the trip's timeline
    repository.clearDaysForTrip(trip.id)

    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    try {
        // Parse dates from trip domain model
        val start = LocalDate.parse(trip.startDate, formatter)
        val end = LocalDate.parse(trip.endDate, formatter)

        // Basic validation: ensure start date is not after end date
        if (start.isAfter(end)) {
            Log.w("TripUtils", "generateDaysForTrip: Start date is after end date for trip ${trip.id}")
            return
        }

        var currentDate = start
        var dayNumber = 1

        // Loop from start to end date inclusive
        while (!currentDate.isAfter(end)) {
            val tripDay = TripDay(
                id = UUID.randomUUID().toString(),
                tripId = trip.id,
                dayNumber = dayNumber,
                date = currentDate,
                subtitle = "" // Default empty subtitle
            )

            repository.addDay(tripDay)

            currentDate = currentDate.plusDays(1)
            dayNumber++
        }

        Log.d("TripUtils", "generateDaysForTrip: Successfully generated ${dayNumber - 1} days for trip ${trip.id}")

    } catch (e: Exception) {
        // Catch parsing or other errors to avoid crashing as per requirements
        Log.e("TripUtils", "generateDaysForTrip: Error generating days for trip ${trip.id}: ${e.message}")
    }
}
