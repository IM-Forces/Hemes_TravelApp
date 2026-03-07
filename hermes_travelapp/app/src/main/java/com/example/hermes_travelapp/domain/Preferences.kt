package com.example.hermes_travelapp.domain

/**
 * Stores all user-configurable settings.
 */
data class Preferences(
    val language: String = "English",
    val currency: String = "EUR (€)",
    val dateFormat: String = "DD/MM/YYYY",
    val theme: String = "Dark",
    val notifications: Boolean = true,
    val emailUpdates: Boolean = false,
    val textSize: String = "Medium"
) {

    /**
     * Future feature: Saves current preferences state to local storage.
     */
    fun save() {
        // @TODO Implement DataStore persistence
    }

    /**
     * Future feature: Resets all application settings to default values.
     */
    fun resetToDefaults() {
        // @TODO Implement reset logic and clear DataStore
    }
}