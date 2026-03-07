package com.example.hermes_travelapp.domain

/**
 * Represents a registered user of the Hermes Travel App.
 * Based on the class diagram for Sprint 1.
 *
 * SPRINT 1: Domain skeleton with planned functions from the class diagram.
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val profileInitials: String,
    val activeTripCount: Int,
    val countriesVisited: Int
) {

    /**
     * Logic helper to compute initials from name if profileInitials is empty.
     */
    fun computeInitials(): String {
        // @TODO Implement logic to compute initials from name (e.g., "Alex Johnson" -> "AJ")
        return profileInitials.uppercase()
    }

    /**
     * Validates if the email follows a correct pattern for authentication.
     */
    fun isEmailValid(): Boolean {
        // @TODO Implement robust email validation using Patterns.EMAIL_ADDRESS
        return email.contains("@")
    }

    /**
     * Future feature: Authenticate the user with the provided credentials.
     */
    fun login() {
        // @TODO Implement Login
    }

    /**
     * Future feature: Registers a new user account in the system.
     */
    fun register() {
        // @TODO Implement Registration
    }

    /**
     * Future feature: Ends the current user session and clears local data.
     */
    fun signOut() {
        // @TODO Implement SignOut
    }

    /**
     * Future feature: Updates trip counters and statistics from the database.
     */
    fun refreshData() {
        // @TODO Refresh trip counts and statistics
    }
}
