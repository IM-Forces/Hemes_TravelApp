package com.example.hermes_travelapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.hermes_travelapp.data.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class AccountViewModel(private val preferencesManager: PreferencesManager) : ViewModel() {

    private val _username = MutableStateFlow(preferencesManager.username.ifBlank { "Vítor Da Silva" })
    val username: StateFlow<String> = _username.asStateFlow()

    private val _birthDate = MutableStateFlow(preferencesManager.dateOfBirth)
    val birthDate: StateFlow<String> = _birthDate.asStateFlow()

    private val _email = MutableStateFlow(preferencesManager.email)
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _dateError = MutableStateFlow<String?>(null)
    val dateError: StateFlow<String?> = _dateError.asStateFlow()

    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun updateUsername(newUsername: String) {
        _username.value = newUsername
    }

    fun updateBirthDate(newBirthDate: String) {
        _birthDate.value = newBirthDate
        validateDate(newBirthDate)
    }

    private fun validateDate(date: String): Boolean {
        if (date.isBlank()) {
            _dateError.value = null
            return true
        }
        return try {
            LocalDate.parse(date, formatter)
            _dateError.value = null
            true
        } catch (e: DateTimeParseException) {
            _dateError.value = "Invalid format. Use DD/MM/YYYY"
            false
        }
    }

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun saveAccount(): Boolean {
        if (_dateError.value != null) return false
        
        Log.d("AccountViewModel", "Saving account info:")
        
        preferencesManager.username = _username.value
        preferencesManager.dateOfBirth = _birthDate.value
        preferencesManager.email = _email.value
        
        Log.d("AccountViewModel", "Username: ${_username.value}")
        Log.d("AccountViewModel", "BirthDate: ${_birthDate.value}")
        Log.d("AccountViewModel", "Email: ${_email.value}")
        
        return true
    }
}
