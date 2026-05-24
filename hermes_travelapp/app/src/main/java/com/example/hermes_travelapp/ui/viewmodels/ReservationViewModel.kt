package com.example.hermes_travelapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hermes_travelapp.domain.model.ReservationUI
import com.example.hermes_travelapp.domain.model.Trip
import com.example.hermes_travelapp.domain.repository.ReservationRepository
import com.example.hermes_travelapp.domain.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReservationViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository,
    private val tripRepository: TripRepository
) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    val reservations: StateFlow<List<ReservationUI>> = reservationRepository.getAllReservations()
        .catch { e -> _errorMessage.value = "Error al cargar reservas: ${e.message}" }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val trips: StateFlow<List<Trip>> = tripRepository.getTrips()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteReservation(reservation: ReservationUI) {
        viewModelScope.launch {
            try {
                reservationRepository.deleteReservation(reservation.id)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar la reserva: ${e.message}"
            }
        }
    }

    fun addReservation(reservation: ReservationUI) {
        viewModelScope.launch {
            try {
                reservationRepository.addReservation(reservation)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Error al añadir la reserva: ${e.message}"
            }
        }
    }

    fun linkReservationToTrip(reservationId: String, tripId: String) {
        viewModelScope.launch {
            try {
                val currentReservations = reservations.value
                val reservation = currentReservations.find { it.id == reservationId }
                if (reservation != null) {
                    val updated = reservation.copy(tripId = tripId)
                    reservationRepository.addReservation(updated)
                    _errorMessage.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al vincular la reserva: ${e.message}"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
