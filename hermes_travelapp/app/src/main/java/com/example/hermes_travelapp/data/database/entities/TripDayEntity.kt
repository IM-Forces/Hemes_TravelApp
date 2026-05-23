package com.example.hermes_travelapp.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "trip_days",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["trip_id"],
            onDelete = CASCADE
        )
    ],
    indices = [Index(value = ["trip_id"])]
)
data class TripDayEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "trip_id")
    val tripId: String,
    val dayNumber: Int,
    val date: LocalDate,
    val photos: List<String> = emptyList()
)
