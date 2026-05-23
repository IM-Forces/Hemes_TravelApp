package com.example.hermes_travelapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hermes_travelapp.data.database.entities.ReservationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReservation(reservation: ReservationEntity)

    @Query("DELETE FROM reservations WHERE id = :id")
    suspend fun deleteReservationById(id: String)

    @Query("SELECT * FROM reservations WHERE id = :id")
    suspend fun getReservationById(id: String): ReservationEntity?

    @Query("SELECT * FROM reservations WHERE trip_id = :tripId")
    fun getReservationByTripId(tripId: String): Flow<ReservationEntity?>

    @Query("SELECT * FROM reservations")
    fun getAllReservations(): Flow<List<ReservationEntity>>
}
