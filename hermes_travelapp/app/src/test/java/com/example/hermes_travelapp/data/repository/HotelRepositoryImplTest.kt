package com.example.hermes_travelapp.data.repository

import android.util.Log
import com.example.hermes_travelapp.data.remote.api.HotelApiService
import com.example.hermes_travelapp.data.remote.dto.*
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HotelRepositoryImplTest {

    private lateinit var repository: HotelRepositoryImpl
    private val apiService: HotelApiService = mockk()

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.i(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
        
        repository = HotelRepositoryImpl(apiService)
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun `getHotels returns success when API call is successful`() = runBlocking {
        val mockHotelsDto = listOf(
            HotelDto("H1", "Hotel Test", "Address", 5, "url", emptyList())
        )
        coEvery { apiService.getHotels("G03") } returns HotelListDto(mockHotelsDto)

        val result = repository.getHotels("G03")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Hotel Test", result.getOrNull()?.first()?.name)
    }

    @Test
    fun `checkAvailability returns hotels when successful`() = runBlocking {
        val mockHotelsDto = listOf(
            HotelDto("H1", "Hotel Available", "Address", 4, "url", emptyList())
        )
        coEvery {
            apiService.checkAvailability("G03", any(), any(), "2026-05-20", "2026-05-22")
        } returns AvailabilityResponseDto(mockHotelsDto)

        val result = repository.checkAvailability("G03", "BCN", "H1", "2026-05-20", "2026-05-22")

        assertTrue(result.isSuccess)
        assertEquals("Hotel Available", result.getOrNull()?.first()?.name)
    }

    @Test
    fun `reserveRoom returns success and maps correctly`() = runBlocking {
        val reservationDto = ReservationDto(
            id = "R1", hotelId = "H1", roomId = "RM1",
            startDate = "2026-05-20", endDate = "2026-05-22",
            guestName = "Ivan Gil", guestEmail = "ivan@example.com"
        )
        val mockResponse = ReserveResponseDto(
            message = "Reservation confirmed",
            nights = 2,
            reservation = reservationDto
        )

        coEvery { apiService.reserveRoom(any(), any()) } returns mockResponse

        val result = repository.reserveRoom(
            groupId = "G03", hotelId = "H1", roomId = "RM1",
            startDate = "2026-05-20", endDate = "2026-05-22",
            guestName = "Ivan Gil", guestEmail = "ivan@example.com"
        )

        assertTrue(result.isSuccess)
        assertEquals("R1", result.getOrNull()?.id)
        assertEquals("Ivan Gil", result.getOrNull()?.guestName)
    }

    @Test
    fun `reserveRoom returns failure when API throws exception`() = runBlocking {
        coEvery { apiService.reserveRoom(any(), any()) } throws Exception("Network Error")

        val result = repository.reserveRoom(
            groupId = "G03", hotelId = "H1", roomId = "RM1",
            startDate = "2026-05-20", endDate = "2026-05-22",
            guestName = "Ivan Gil", guestEmail = "ivan@example.com"
        )

        assertTrue(result.isFailure)
        assertEquals("Network Error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getGroupReservations returns reservations when successful`() = runBlocking {
        val reservations = listOf(
            ReservationDto("R1", "H1", "RM1", "2026-05-20", "2026-05-22", "Marco", "marco@test.com")
        )
        coEvery { apiService.getGroupReservations("G03", any()) } returns ReservationListDto(reservations)

        val result = repository.getGroupReservations("G03", "marco@test.com")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Marco", result.getOrNull()?.first()?.guestName)
    }

    @Test
    fun `deleteReservation returns success when item is removed`() = runBlocking {
        val reservationDto = ReservationDto("R1", "H1", "RM1", "2026-05-20", "2026-05-22", "Ivan", "ivan@test.com")
        coEvery { apiService.deleteReservation("R1") } returns reservationDto

        val result = repository.deleteReservation("R1")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `deleteReservation returns failure when API throws exception`() = runBlocking {
        coEvery { apiService.deleteReservation("R1") } throws Exception("Not Found")

        val result = repository.deleteReservation("R1")

        assertTrue(result.isFailure)
        assertEquals("Not Found", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getReservationById returns success and maps correctly`() = runBlocking {
        val reservationDto = ReservationDto(
            id = "AOBUJB", hotelId = "BCN01", roomId = "R1",
            startDate = "2026-05-20", endDate = "2026-05-22",
            guestName = "Ivan Gil", guestEmail = "ivan@example.com"
        )
        coEvery { apiService.getReservationById("AOBUJB") } returns reservationDto

        val result = repository.getReservationById("AOBUJB")

        assertTrue(result.isSuccess)
        assertEquals("AOBUJB", result.getOrNull()?.id)
        assertEquals("BCN01", result.getOrNull()?.hotelId)
    }

    @Test
    fun `getReservationById returns failure when API throws exception`() = runBlocking {
        coEvery { apiService.getReservationById("INVALID") } throws Exception("Reservation not found")

        val result = repository.getReservationById("INVALID")

        assertTrue(result.isFailure)
        assertEquals("Reservation not found", result.exceptionOrNull()?.message)
    }

    @Test
    fun `repository returns failure when API throws exception`() = runBlocking {
        coEvery { apiService.getHotels(any()) } throws Exception("API Error")

        val result = repository.getHotels("G03")

        assertTrue(result.isFailure)
        assertEquals("API Error", result.exceptionOrNull()?.message)
    }
}