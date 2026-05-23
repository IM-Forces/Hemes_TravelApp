package com.example.hermes_travelapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.hermes_travelapp.data.database.dao.AccessLogDao
import com.example.hermes_travelapp.data.database.dao.ReservationDao
import com.example.hermes_travelapp.data.database.dao.TripDao
import com.example.hermes_travelapp.data.database.dao.TripDayDao
import com.example.hermes_travelapp.data.database.dao.ItineraryItemDao
import com.example.hermes_travelapp.data.database.dao.UserDao
import com.example.hermes_travelapp.data.database.entities.AccessLogEntity
import com.example.hermes_travelapp.data.database.entities.ItineraryItemEntity
import com.example.hermes_travelapp.data.database.entities.ReservationEntity
import com.example.hermes_travelapp.data.database.entities.TripDayEntity
import com.example.hermes_travelapp.data.database.entities.TripEntity
import com.example.hermes_travelapp.data.database.entities.UserEntity

@Database(
    entities = [
        TripEntity::class,
        TripDayEntity::class,
        UserEntity::class,
        ItineraryItemEntity::class,
        AccessLogEntity::class,
        ReservationEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(AppTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun tripDayDao(): TripDayDao
    abstract fun itineraryItemDao(): ItineraryItemDao
    abstract fun userDao(): UserDao
    abstract fun accessLogDao(): AccessLogDao
    abstract fun reservationDao(): ReservationDao
}
