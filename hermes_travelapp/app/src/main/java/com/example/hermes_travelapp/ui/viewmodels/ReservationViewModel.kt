package com.example.hermes_travelapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hermes_travelapp.domain.model.ReservationUI
import com.example.hermes_travelapp.domain.repository.ReservationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReservationViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository
) : ViewModel() {

    val reservations: StateFlow<List<ReservationUI>> = reservationRepository.getAllReservations()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteReservation(reservation: ReservationUI) {
        viewModelScope.launch {
            reservationRepository.deleteReservation(reservation.id)
        }
    }
    
    fun addReservation(reservation: ReservationUI) {
        viewModelScope.launch {
            reservationRepository.addReservation(reservation)
        }
    }
}
