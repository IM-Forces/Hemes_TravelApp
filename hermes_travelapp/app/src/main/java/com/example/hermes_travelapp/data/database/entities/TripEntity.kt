package com.example.hermes_travelapp.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "trips",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = CASCADE
        )
    ],
    indices = [Index(value = ["user_id"])]
)
data class TripEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val startDate: String,
    val endDate: String,
    val description: String,
    val destinationCity: String,
    val emoji: String,
    val budget: Int,
    val spent: Int,
    val progress: Float,
    val daysRemaining: Int,
    @ColumnInfo(name = "user_id")
    val userId: String,
    val coverPhotoUrl: String? = null
)
