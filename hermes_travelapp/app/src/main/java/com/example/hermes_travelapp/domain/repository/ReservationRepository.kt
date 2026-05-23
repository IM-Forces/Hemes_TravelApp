package com.example.hermes_travelapp.domain.repository

import com.example.hermes_travelapp.domain.model.ReservationUI
import kotlinx.coroutines.flow.Flow

interface ReservationRepository {
    fun getAllReservations(): Flow<List<ReservationUI>>
    fun getReservationByTripId(tripId: String): Flow<ReservationUI?>
    suspend fun addReservation(reservation: ReservationUI)
    suspend fun deleteReservation(id: String)
}
