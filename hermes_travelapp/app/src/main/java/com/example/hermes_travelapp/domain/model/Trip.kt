package com.example.hermes_travelapp.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val startDate: String,
    val endDate: String,
    val description: String,
    val emoji: String = "🌍",
    val budget: Int = 0,
    val spent: Int = 0,
    val progress: Float = 0.0f,
    val daysRemaining: Int = 0,
    val coverPhotoUrl: String? = null
) {
    fun calculateDaysRemaining(): Int {
        return daysRemaining
    }

    fun isOverBudget(): Boolean {
        return spent > budget
    }

    fun getSummary(): String {
        return "$title: $description"
    }
}
