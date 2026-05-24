package com.example.hermes_travelapp.data.repository

import android.util.Log
import com.example.hermes_travelapp.data.remote.api.HotelApiService
import com.example.hermes_travelapp.data.remote.dto.ReserveRequestDto
import com.example.hermes_travelapp.data.remote.mapper.toDomain
import com.example.hermes_travelapp.domain.model.Hotel
import com.example.hermes_travelapp.domain.model.HotelReservation
import com.example.hermes_travelapp.domain.repository.HotelRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HotelRepositoryImpl @Inject constructor(
    private val apiService: HotelApiService
) : HotelRepository {

    override suspend fun getHotels(groupId: String): Result<List<Hotel>> {
        return runCatching {
            apiService.getHotels(groupId).hotels?.map { it.toDomain() } ?: emptyList()
        }
    }

    override suspend fun checkAvailability(
        groupId: String,
        city: String?,
        hotelId: String?,
        startDate: String,
        endDate: String
    ): Result<List<Hotel>> {
        Log.d("HotelRepository", "checkAvailability params: groupId=$groupId, city=$city, hotelId=$hotelId, start=$startDate, end=$endDate")
        return runCatching {
            val response = apiService.checkAvailability(groupId, city, hotelId, startDate, endDate)
            Log.d("HotelRepository", "API Response: $response")
            response.availableHotels?.map { it.toDomain() } ?: emptyList()
        }.onFailure { e ->
            Log.e("HotelRepository", "Error in checkAvailability: ${e.message}", e)
        }
    }

    override suspend fun reserveRoom(
        groupId: String,
        hotelId: String,
        roomId: String,
        startDate: String,
        endDate: String,
        guestName: String,
        guestEmail: String
    ): Result<HotelReservation> {
        Log.d("HotelRepository", "reserveRoom params: groupId=$groupId, hotelId=$hotelId, roomId=$roomId")
        return runCatching {
            val request = ReserveRequestDto(hotelId, roomId, startDate, endDate, guestName, guestEmail)
            val reservation = apiService.reserveRoom(groupId, request).reservation.toDomain()
            Log.d("HotelRepository", "API Reserve Success: ${reservation.id}")
            reservation
        }.onFailure { e ->
            Log.e("HotelRepository", "Error in reserveRoom: ${e.message}", e)
        }
    }

    override suspend fun getGroupReservations(
        groupId: String,
        guestEmail: String?
    ): Result<List<HotelReservation>> {
        return runCatching {
            apiService.getGroupReservations(groupId, guestEmail).reservations.map { it.toDomain() }
        }
    }

    override suspend fun getReservationById(reservationId: String): Result<HotelReservation> {
        return runCatching {
            apiService.getReservationById(reservationId).toDomain()
        }
    }

    override suspend fun deleteReservation(reservationId: String): Result<Unit> {
        return runCatching {
            apiService.deleteReservation(reservationId)
            Unit
        }
    }
}
