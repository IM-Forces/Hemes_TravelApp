package com.example.hermes_travelapp.domain.model

data class ReservationUI(
    val id: String,
    val tripId: String?,
    val roomId: String = "",
    val hotelName: String,
    val hotelAddress: String,
    val checkInDate: String,
    val checkOutDate: String,
    val hotelImageUrl: String,
    val roomImageUrl: String
)
