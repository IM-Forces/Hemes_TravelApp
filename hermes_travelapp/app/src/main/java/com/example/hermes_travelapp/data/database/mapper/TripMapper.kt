package com.example.hermes_travelapp.data.database.mapper

import com.example.hermes_travelapp.data.database.entities.TripEntity
import com.example.hermes_travelapp.domain.model.Trip

fun TripEntity.toDomain(): Trip {
    return Trip(
        id = id,
        title = title,
        startDate = startDate,
        endDate = endDate,
        description = description,
        destinationCity = destinationCity,
        emoji = emoji,
        budget = budget,
        spent = spent,
        progress = progress,
        daysRemaining = daysRemaining,
        coverPhotoUrl = coverPhotoUrl
    )
}

fun Trip.toEntity(userId: String): TripEntity {
    return TripEntity(
        id = id,
        title = title,
        startDate = startDate,
        endDate = endDate,
        description = description,
        destinationCity = destinationCity,
        emoji = emoji,
        budget = budget,
        spent = spent,
        progress = progress,
        daysRemaining = daysRemaining,
        userId = userId,
        coverPhotoUrl = coverPhotoUrl
    )
}
