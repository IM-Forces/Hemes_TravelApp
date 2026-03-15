package com.example.hermes_travelapp.domain

import java.util.UUID

/**
 * Represents a travel trip.
 * Root entity for itinerary and budget management.
 */
data class Trip(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val startDate: String, // format: dd/MM/YYYY
    val endDate: String,   // format: dd/MM/YYYY
    val description: String,
    val emoji: String = "🌍",
    val budget: Int = 0,
    val spent: Int = 0,
    val progress: Float = 0.0f,
    val daysRemaining: Int = 0
) {

    /**
     * Calculates the actual days remaining until the trip starts.
     */
    fun calculateDaysRemaining(): Int {
        // @TODO Implement date calculation logic
        return daysRemaining
    }

    /**
     * Returns true if the current expenses (spent) exceed the allocated budget.
     */
    fun isOverBudget(): Boolean {
        return spent > budget
    }

    /**
     * Future feature: Generates a string summary of the trip for sharing.
     */
    fun getSummary(): String {
        return "$title: $description"
    }
}
