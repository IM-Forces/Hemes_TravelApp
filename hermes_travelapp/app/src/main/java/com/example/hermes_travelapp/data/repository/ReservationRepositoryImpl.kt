package com.example.hermes_travelapp.data.repository

import com.example.hermes_travelapp.data.database.dao.ReservationDao
import com.example.hermes_travelapp.data.database.mapper.toDomain
import com.example.hermes_travelapp.data.database.mapper.toEntity
import com.example.hermes_travelapp.domain.model.ReservationUI
import com.example.hermes_travelapp.domain.repository.ReservationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReservationRepositoryImpl @Inject constructor(
    private val reservationDao: ReservationDao
) : ReservationRepository {

    override fun getAllReservations(): Flow<List<ReservationUI>> {
        return reservationDao.getAllReservations().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getReservationByTripId(tripId: String): Flow<ReservationUI?> {
        return reservationDao.getReservationByTripId(tripId).map { it?.toDomain() }
    }

    override suspend fun addReservation(reservation: ReservationUI) {
        reservationDao.insertReservation(reservation.toEntity())
    }

    override suspend fun deleteReservation(id: String) {
        // TODO: Llamar a la API remota para cumplir con el requisito T4.2 en el futuro.
        reservationDao.deleteReservationById(id)
    }
}
