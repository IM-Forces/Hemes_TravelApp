package com.example.hermes_travelapp.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reservations",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["trip_id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ],
    indices = [Index(value = ["trip_id"])]
)
data class ReservationEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "trip_id")
    val tripId: String? = null,
    val roomId: String = "",
    val hotelName: String,
    val hotelAddress: String = "",
    val checkInDate: String,
    val checkOutDate: String,
    val hotelImageUrl: String = "",
    val roomImageUrl: String = ""
)
