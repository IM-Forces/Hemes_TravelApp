package com.example.hermes_travelapp.di

import com.example.hermes_travelapp.data.repository.AccessLogRepositoryImpl
import com.example.hermes_travelapp.data.repository.ActivityRepositoryImpl
import com.example.hermes_travelapp.data.repository.AuthRepositoryImpl
import com.example.hermes_travelapp.data.repository.HotelRepositoryImpl
import com.example.hermes_travelapp.data.repository.TripDayRepositoryImpl
import com.example.hermes_travelapp.data.repository.TripRepositoryImpl
import com.example.hermes_travelapp.data.repository.UserRepositoryImpl
import com.example.hermes_travelapp.domain.repository.AccessLogRepository
import com.example.hermes_travelapp.domain.repository.ActivityRepository
import com.example.hermes_travelapp.domain.repository.AuthRepository
import com.example.hermes_travelapp.domain.repository.HotelRepository
import com.example.hermes_travelapp.domain.repository.TripDayRepository
import com.example.hermes_travelapp.domain.repository.TripRepository
import com.example.hermes_travelapp.domain.repository.UserRepository
import com.example.hermes_travelapp.data.repository.ReservationRepositoryImpl
import com.example.hermes_travelapp.domain.repository.ReservationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTripRepository(
        tripRepositoryImpl: TripRepositoryImpl
    ): TripRepository

    @Binds
    @Singleton
    abstract fun bindTripDayRepository(
        tripDayRepositoryImpl: TripDayRepositoryImpl
    ): TripDayRepository

    @Binds
    @Singleton
    abstract fun bindActivityRepository(
        activityRepositoryImpl: ActivityRepositoryImpl
    ): ActivityRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindAccessLogRepository(
        accessLogRepositoryImpl: AccessLogRepositoryImpl
    ): AccessLogRepository

    @Binds
    @Singleton
    abstract fun bindHotelRepository(
        hotelRepositoryImpl: HotelRepositoryImpl
    ): HotelRepository

    @Binds
    @Singleton
    abstract fun bindReservationRepository(
        reservationRepositoryImpl: ReservationRepositoryImpl
    ): ReservationRepository
}
