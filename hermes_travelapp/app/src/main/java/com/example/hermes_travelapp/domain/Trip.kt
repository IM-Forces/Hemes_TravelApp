package com.example.hermes_travelapp.domain

/**
 * Represents a travel trip.
 * Root entity for itinerary and budget management.
 */
data class Trip(
    val id: String,
    val name: String,
    val emoji: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val budget: Int,
    val nights: Int,
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
        // @TODO Implement budget limit validation
        return spent > budget
    }

    /**
     * Future feature: Adds a new activity or place to this trip's itinerary.
     */
    fun addActivity(activityId: String) {
        // @TODO Implement itinerary management and validation
    }

    /**
     * Future feature: Updates the total spent amount when a new expense is recorded.
     */
    fun updateSpending(amount: Int) {
        // @TODO Implement real-time budget tracking and progress update
    }

    /**
     * Future feature: Generates a string summary of the trip for sharing.
     */
    fun getSummary(): String {
        // @TODO Implement formatting for trip summary (e.g., "Paris - 4 nights")
        return "$name to $destination"
    }
}
