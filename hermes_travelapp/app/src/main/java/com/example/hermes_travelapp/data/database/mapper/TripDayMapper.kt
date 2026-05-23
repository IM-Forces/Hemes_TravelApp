package com.example.hermes_travelapp.data.database.mapper

import com.example.hermes_travelapp.data.database.entities.TripDayEntity
import com.example.hermes_travelapp.domain.model.TripDay

fun TripDayEntity.toDomain(): TripDay {
    return TripDay(
        id = id,
        tripId = tripId,
        dayNumber = dayNumber,
        date = date,
        photos = photos
    )
}

fun TripDay.toEntity(): TripDayEntity {
    return TripDayEntity(
        id = id,
        tripId = tripId,
        dayNumber = dayNumber,
        date = date,
        photos = photos
    )
}
