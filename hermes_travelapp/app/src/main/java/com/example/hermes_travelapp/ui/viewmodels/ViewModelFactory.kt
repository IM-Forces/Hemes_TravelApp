package com.example.hermes_travelapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hermes_travelapp.domain.TripDayRepository
import com.example.hermes_travelapp.domain.TripRepository

class ViewModelFactory(
    private val tripRepository: TripRepository? = null,
    private val tripDayRepository: TripDayRepository? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(TripViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                TripViewModel(tripRepository ?: throw IllegalArgumentException("TripRepository is required for TripViewModel")) as T
            }
            modelClass.isAssignableFrom(TripDayViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                TripDayViewModel(tripDayRepository ?: throw IllegalArgumentException("TripDayRepository is required for TripDayViewModel")) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
