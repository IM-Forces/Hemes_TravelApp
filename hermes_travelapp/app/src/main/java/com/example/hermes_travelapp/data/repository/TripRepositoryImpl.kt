package com.example.hermes_travelapp.data.repository

import android.util.Log
import com.example.hermes_travelapp.data.database.dao.TripDao
import com.example.hermes_travelapp.data.database.mapper.toDomain
import com.example.hermes_travelapp.data.database.mapper.toEntity
import com.example.hermes_travelapp.domain.model.Trip
import com.example.hermes_travelapp.domain.repository.AuthRepository
import com.example.hermes_travelapp.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of the [TripRepository] interface.
 * This class acts as a bridge between the domain layer and the data source,
 * delegating all operations to [TripDao].
 */
@Singleton
class TripRepositoryImpl @Inject constructor(
    private val tripDao: TripDao,
    private val authRepository: AuthRepository
) : TripRepository {

    private companion object {
        const val TAG = "TripRepository"
    }

    override fun getTrips(): Flow<List<Trip>> {
        val userId = authRepository.getCurrentUserId()
        if (userId == null) {
            Log.w(TAG, "getTrips: No user authenticated, returning empty flow")
            return kotlinx.coroutines.flow.flowOf(emptyList())
        }
        Log.d(TAG, "getTrips called: fetching trips for userId=$userId")
        return tripDao.getTripsByUser(userId)
            .map { list ->
                Log.d(TAG, "getTrips returned ${list.size} trips for userId=$userId")
                list.map { it.toDomain() }
            }
    }

    override suspend fun addTrip(trip: Trip) {
        val userId = authRepository.getCurrentUserId()
            ?: throw IllegalStateException("User must be authenticated to add a trip")
        Log.d(TAG, "addTrip: Validando datos para el viaje '${trip.title}' para userId=$userId")

        if (tripDao.existsByTitle(userId, trip.title)) {
            Log.e(TAG, "addTrip: Error - El nombre '${trip.title}' ya está en uso para este usuario.")
            throw IllegalStateException("Ya existe un viaje con este nombre.")
        }

        if (trip.startDate > trip.endDate) {
            Log.e(TAG, "addTrip: Error - La fecha de inicio (${trip.startDate}) es posterior a la de fin (${trip.endDate}).")
            throw IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.")
        }

        try {
            tripDao.insertTrip(trip.toEntity(userId))
            Log.i(TAG, "addTrip: Éxito - Viaje '${trip.title}' persistido en DB local.")
        } catch (e: Exception) {
            Log.e(TAG, "addTrip: Error crítico al insertar en la base de datos", e)
            throw e
        }
    }

    override suspend fun editTrip(trip: Trip) {
        val userId = authRepository.getCurrentUserId()
            ?: throw IllegalStateException("User must be authenticated to edit a trip")
        Log.d(TAG, "editTrip: Intentando actualizar viaje ID ${trip.id} para usuario $userId")

        if (trip.title.isBlank()) {
            Log.e(TAG, "editTrip: Error - El título no puede estar vacío.")
            throw IllegalArgumentException("El título no puede estar vacío.")
        }

        try {
            tripDao.updateTrip(trip.toEntity(userId))
            Log.i(TAG, "editTrip: Éxito - Viaje ID ${trip.id} actualizado.")
        } catch (e: Exception) {
            Log.e(TAG, "editTrip: Error al actualizar en la base de datos", e)
            throw e
        }
    }

    override suspend fun deleteTrip(tripId: String) {
        Log.d(TAG, "deleteTrip called for tripId=$tripId")
        tripDao.deleteTripById(tripId)
        Log.i(TAG, "deleteTrip successful: tripId=$tripId removed.")
    }
}
