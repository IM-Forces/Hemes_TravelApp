package com.example.hermes_travelapp.data.database.mapper

import com.example.hermes_travelapp.data.database.entities.ReservationEntity
import com.example.hermes_travelapp.domain.model.ReservationUI

fun ReservationEntity.toDomain(): ReservationUI {
    return ReservationUI(
        id = id,
        tripId = tripId,
        hotelName = hotelName,
        hotelAddress = hotelAddress,
        checkInDate = checkInDate,
        checkOutDate = checkOutDate,
        hotelImageUrl = hotelImageUrl,
        roomImageUrl = roomImageUrl
    )
}

fun ReservationUI.toEntity(): ReservationEntity {
    return ReservationEntity(
        id = id,
        tripId = tripId,
        hotelName = hotelName,
        hotelAddress = hotelAddress,
        checkInDate = checkInDate,
        checkOutDate = checkOutDate,
        hotelImageUrl = hotelImageUrl,
        roomImageUrl = roomImageUrl
    )
}
