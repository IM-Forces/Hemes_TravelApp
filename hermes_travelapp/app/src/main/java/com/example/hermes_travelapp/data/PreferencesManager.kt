package com.example.hermes_travelapp.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Manager class to handle SharedPreferences for user settings.
 */
class PreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("hermes_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USERNAME = "username"
        private const val KEY_DATE_OF_BIRTH = "dob"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_LANGUAGE = "language"
    }

    var username: String
        get() = sharedPreferences.getString(KEY_USERNAME, "") ?: ""
        set(value) = sharedPreferences.edit { putString(KEY_USERNAME, value) }

    var dateOfBirth: String
        get() = sharedPreferences.getString(KEY_DATE_OF_BIRTH, "") ?: ""
        set(value) = sharedPreferences.edit { putString(KEY_DATE_OF_BIRTH, value) }

    var isDarkMode: Boolean
        get() = sharedPreferences.getBoolean(KEY_DARK_MODE, false)
        set(value) = sharedPreferences.edit { putBoolean(KEY_DARK_MODE, value) }

    var language: String
        get() = sharedPreferences.getString(KEY_LANGUAGE, "es") ?: "es"
        set(value) = sharedPreferences.edit { putString(KEY_LANGUAGE, value) }
}
