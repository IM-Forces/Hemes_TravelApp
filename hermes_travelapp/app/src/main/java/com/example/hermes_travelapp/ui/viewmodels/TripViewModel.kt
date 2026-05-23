package com.example.hermes_travelapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hermes_travelapp.domain.model.Trip
import com.example.hermes_travelapp.domain.repository.TripRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import com.example.hermes_travelapp.R
import kotlinx.coroutines.Job

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(private val repository: TripRepository) : ViewModel() {
    
    private companion object {
        const val TAG = "TripViewModel"
        val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    }

    private val _errorMessageRes = MutableLiveData<Int?>(null)
    val errorMessageRes: LiveData<Int?> = _errorMessageRes

    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    /**
     * Observable stream of all trips.
     */
    val trips: StateFlow<List<Trip>> = _trips.asStateFlow()

    private var loadTripsJob: Job? = null

    init {
        loadTrips()
    }

    /**
     * Loads trips from the repository and updates the [trips] StateFlow.
     */
    fun loadTrips() {
        Log.d(TAG, "loadTrips: initiating trip loading from repository")
        loadTripsJob?.cancel()
        loadTripsJob = viewModelScope.launch {
            repository.getTrips().collect { result ->
                _trips.value = result
                Log.d(TAG, "Trip list updated, total: ${result.size}")
                Log.i(TAG, "loadTrips: successfully loaded ${result.size} trips")
            }
        }
    }

    /**
     * Validates trip dates and adds the trip if valid.
     */
    suspend fun addTrip(trip: Trip): Boolean {
        Log.d(TAG, "addTrip: attempting to add trip '${trip.title}'")
        if (validateTrip(trip)) {
            try {
                repository.addTrip(trip)
                _errorMessageRes.value = null
                Log.i(TAG, "Trip created successfully: ${trip.title} (id=${trip.id})")
                return true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add trip: ${e.message}", e)
                // You might want to map specific exceptions to string resources here
                return false
            }
        }
        return false
    }

    /**
     * Validates trip dates and updates the trip if valid.
     */
    suspend fun editTrip(updatedTrip: Trip): Boolean {
        Log.d(TAG, "editTrip: attempting to edit trip id=${updatedTrip.id}")
        if (validateTrip(updatedTrip)) {
            try {
                repository.editTrip(updatedTrip)
                _errorMessageRes.value = null
                Log.i(TAG, "Trip updated successfully: ${updatedTrip.title} (id=${updatedTrip.id})")
                return true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to edit trip: ${e.message}", e)
                return false
            }
        }
        return false
    }

    fun deleteTrip(tripId: String) {
        Log.d(TAG, "deleteTrip: attempting to delete trip id=$tripId")
        viewModelScope.launch {
            repository.deleteTrip(tripId)
            Log.i(TAG, "Trip deleted successfully: id=$tripId")
        }
    }

    fun updateTripEndDate(tripId: String, newEndDate: String) {
        Log.d(TAG, "updateTripEndDate: updating end date for trip id=$tripId to $newEndDate")
        val trip = _trips.value.find { it.id == tripId } ?: return
        val updatedTrip = trip.copy(endDate = newEndDate)
        viewModelScope.launch {
            repository.editTrip(updatedTrip)
            Log.i(TAG, "Trip end date updated successfully for trip id=$tripId")
        }
    }

    fun setTripCoverPhoto(tripId: String, photoUrl: String) {
        Log.d(TAG, "setTripCoverPhoto: updating cover photo for trip id=$tripId to $photoUrl")
        val trip = _trips.value.find { it.id == tripId } ?: return
        val updatedTrip = trip.copy(coverPhotoUrl = photoUrl)
        viewModelScope.launch {
            repository.editTrip(updatedTrip)
            Log.i(TAG, "Trip cover photo updated successfully for trip id=$tripId")
        }
    }

    /**
     * Clears any active error messages.
     */
    fun clearError() {
        _errorMessageRes.value = null
    }

    /**
     * Core validation logic for trips.
     * Checks for mandatory dates and ensures start date is before or equal to end date.
     */
    private fun validateTrip(trip: Trip): Boolean {
        if (trip.title.isBlank()) {
            _errorMessageRes.value = R.string.error_field_required
            Log.e(TAG, "Validation failed: title is blank")
            return false
        }

        if (trip.startDate.isBlank() || trip.endDate.isBlank()) {
            Log.e(TAG, "error_required_dates")
            _errorMessageRes.value = R.string.error_required_dates
            return false
        }

        try {
            val start = LocalDate.parse(trip.startDate, DATE_FORMATTER)
            val end = LocalDate.parse(trip.endDate, DATE_FORMATTER)

            if (end.isBefore(start)) {
                Log.e(TAG, "error_invalid_range")
                _errorMessageRes.value = R.string.error_invalid_range
                return false
            }
        } catch (e: DateTimeParseException) {
            Log.e(TAG, "error_invalid_format: ${e.message}")
            _errorMessageRes.value = R.string.error_invalid_format
            return false
        }

        return true
    }
}
