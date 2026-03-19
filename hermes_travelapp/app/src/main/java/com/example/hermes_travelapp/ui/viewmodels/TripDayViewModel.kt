package com.example.hermes_travelapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.hermes_travelapp.domain.TripDay
import com.example.hermes_travelapp.domain.TripDayRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for managing the state and business logic of trip days.
 * Follows the same pattern as ActivityViewModel.
 */
class TripDayViewModel(private val repository: TripDayRepository) : ViewModel() {

    private companion object {
        const val TAG = "TripDayViewModel"
    }

    private val _tripDays = MutableStateFlow<List<TripDay>>(emptyList())
    /**
     * Observable stream of trip days for the currently selected trip.
     */
    val tripDays: StateFlow<List<TripDay>> = _tripDays.asStateFlow()

    /**
     * Loads days for a specific trip and updates the [tripDays] StateFlow.
     * @param tripId The unique identifier of the trip.
     */
    fun loadDaysForTrip(tripId: String) {
        val result = repository.getDaysForTrip(tripId)
        _tripDays.value = result
        Log.d(TAG, "loadDaysForTrip: loaded ${result.size} days for trip $tripId")
    }
}
