package com.example.hermes_travelapp.ui.viewmodels

import android.util.Log
import com.example.hermes_travelapp.data.PreferencesManager
import com.example.hermes_travelapp.domain.model.*
import com.example.hermes_travelapp.domain.repository.HotelRepository
import com.example.hermes_travelapp.domain.repository.ReservationRepository
import com.example.hermes_travelapp.domain.repository.TripDayRepository
import com.example.hermes_travelapp.domain.repository.TripRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReservationFlowTest {

    private lateinit var hotelViewModel: HotelViewModel
    private lateinit var reservationViewModel: ReservationViewModel
    private lateinit var tripViewModel: TripViewModel
    
    private val hotelRepository: HotelRepository = mockk()
    private val reservationRepository: ReservationRepository = mockk()
    private val tripRepository: TripRepository = mockk()
    private val tripDayRepository: TripDayRepository = mockk()
    private val preferencesManager: PreferencesManager = mockk()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        mockkStatic(Log::class)
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.i(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0

        every { preferencesManager.username } returns "Test User"
        every { preferencesManager.email } returns "test@example.com"
        
        // Default flow mocks
        coEvery { reservationRepository.getAllReservations() } returns flowOf(emptyList())
        coEvery { tripRepository.getTrips() } returns flowOf(emptyList())
        
        // Mock repository methods used in confirmReservation to prevent crashes
        coEvery { tripRepository.addTrip(any()) } returns Unit
        coEvery { tripDayRepository.clearDaysForTrip(any()) } returns Unit
        coEvery { tripDayRepository.addDay(any()) } returns Unit
        coEvery { reservationRepository.addReservation(any()) } returns Unit

        hotelViewModel = HotelViewModel(hotelRepository, reservationRepository, tripRepository, tripDayRepository, preferencesManager)
        reservationViewModel = ReservationViewModel(reservationRepository, tripRepository)
        tripViewModel = TripViewModel(tripRepository, reservationRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    // --- T4.1 – Listar reservas ---

    @Test
    fun `loadReservations success returns list`() = runTest {
        val mockReservations = listOf(
            ReservationUI("1", "trip1", "room1", "Hotel A", "Addr", "20/05/2026", "22/05/2026", "url1", "url2")
        )
        coEvery { reservationRepository.getAllReservations() } returns flowOf(mockReservations)
        
        val vm = ReservationViewModel(reservationRepository, tripRepository)
        assertEquals(mockReservations, vm.reservations.first { it.isNotEmpty() })
    }

    @Test
    fun `loadReservations empty list shows empty state`() = runTest {
        coEvery { reservationRepository.getAllReservations() } returns flowOf(emptyList())
        val vm = ReservationViewModel(reservationRepository, tripRepository)
        assertTrue(vm.reservations.value.isEmpty())
    }

    @Test
    fun `loadReservations failure shows error`() = runTest {
        coEvery { reservationRepository.getAllReservations() } returns flow {
            throw Exception("Database Error")
        }
        val vm = ReservationViewModel(reservationRepository, tripRepository)
        
        // Act - Trigger flow to catch the error
        try { vm.reservations.first() } catch (e: Exception) { /* expected */ }
        
        assertNotNull(vm.errorMessage.value)
        assertTrue(vm.errorMessage.value!!.contains("Database Error"))
    }

    // --- T4.2 – Cancelar reserva ---

    @Test
    fun `deleteReservation success removes from local DB`() = runTest {
        val reservation = ReservationUI("res123", "trip1", "room1", "Hotel", "Addr", "date", "date", "u", "u")
        coEvery { reservationRepository.deleteReservation("res123") } returns Unit
        
        reservationViewModel.deleteReservation(reservation)
        
        coVerify { reservationRepository.deleteReservation("res123") }
        assertNull(reservationViewModel.errorMessage.value)
    }

    @Test
    fun `deleteReservation failure shows error`() = runTest {
        val reservation = ReservationUI("res123", "trip1", "room1", "Hotel", "Addr", "date", "date", "u", "u")
        coEvery { reservationRepository.deleteReservation(any()) } throws Exception("API Deletion Failed")
        
        reservationViewModel.deleteReservation(reservation)
        
        assertNotNull(reservationViewModel.errorMessage.value)
        assertTrue(reservationViewModel.errorMessage.value!!.contains("API Deletion Failed"))
    }

    // --- T4.3 – Mostrar imágenes ---

    @Test
    fun `reservation list item contains image URLs`() {
        val reservation = ReservationUI(
            id = "1", tripId = "T1", roomId = "R1",
            hotelName = "Hotel Test", hotelAddress = "Address",
            checkInDate = "01/01/2026", checkOutDate = "02/01/2026",
            hotelImageUrl = "https://example.com/hotel.jpg",
            roomImageUrl = "https://example.com/room.jpg"
        )
        assertEquals("https://example.com/hotel.jpg", reservation.hotelImageUrl)
        assertEquals("https://example.com/room.jpg", reservation.roomImageUrl)
    }

    // --- T4.4 – My Trips integración ---

    @Test
    fun `MyTrips state shows reservation indicator when trip has hotel booking`() = runTest {
        val tripId = "trip_with_hotel"
        val mockReservations = listOf(
            ReservationUI("res1", tripId, "room1", "Hotel", "Addr", "01/01/2026", "02/01/2026", "u", "u")
        )
        coEvery { reservationRepository.getAllReservations() } returns flowOf(mockReservations)
        
        val tVm = TripViewModel(tripRepository, reservationRepository)
        assertTrue(tVm.tripsWithReservations.value.contains(tripId))
    }

    // --- T2.3: Booking and saving locally (New Trip + Reservation) ---

    @Test
    fun `confirmReservation success creates a new Trip when no trip is selected`() = runTest {
        // Arrange
        val room = HotelRoom("room1", "Deluxe", 100.0, listOf("room_url"))
        val hotel = Hotel("1", "Hotel Paris", "Address", 4, "url", listOf(room))
        val startDate = "20/05/2026"
        val endDate = "25/05/2026"
        val apiReservation = HotelReservation(
            id = "res_123",
            hotelId = "1",
            roomId = "room1",
            startDate = "2026-05-20",
            endDate = "2026-05-25",
            guestName = "Test User",
            guestEmail = "test@example.com"
        )

        hotelViewModel.onCitySelected("Paris")
        hotelViewModel.onStartDateSelected(startDate)
        hotelViewModel.onEndDateSelected(endDate)
        coEvery { 
            hotelRepository.reserveRoom("G03", hotel.id, "room1", "2026-05-20", "2026-05-25", "Test User", "test@example.com") 
        } returns Result.success(apiReservation)
        
        coEvery { reservationRepository.addReservation(any()) } returns Unit

        // Act
        hotelViewModel.confirmReservation(hotel, "room1", startDate, endDate)
        advanceUntilIdle()
        
        // Assert
        assertTrue(hotelViewModel.reservationSuccess.value)
        coVerify { reservationRepository.addReservation(match { it.tripId != null }) }
    }

    @Test
    fun `confirmReservation success links to existing Trip`() = runTest {
        // Arrange
        val existingTrip = Trip(id = "trip123", title = "My Trip", startDate = "10/05/2026", endDate = "30/05/2026", description = "")
        val room = HotelRoom("room1", "Deluxe", 100.0, emptyList())
        val hotel = Hotel("1", "Hotel Paris", "Address", 4, "url", listOf(room))
        val startDate = "20/05/2026"
        val endDate = "25/05/2026"
        val apiReservation = HotelReservation("res_123", "1", "room1", "2026-05-20", "2026-05-25", "User", "test@example.com")

        hotelViewModel.onTripSelected(existingTrip)
        hotelViewModel.onCitySelected("Paris")

        coEvery { 
            hotelRepository.reserveRoom(any(), any(), any(), any(), any(), any(), any()) 
        } returns Result.success(apiReservation)
        coEvery { reservationRepository.addReservation(any()) } returns Unit

        // Act
        hotelViewModel.confirmReservation(hotel, "room1", startDate, endDate)
        advanceUntilIdle()

        // Assert
        assertTrue(hotelViewModel.reservationSuccess.value)
        coVerify(exactly = 0) { tripRepository.addTrip(any()) }
        coVerify { reservationRepository.addReservation(match { it.tripId == "trip123" }) }
    }

    @Test
    fun `confirmReservation fails when dates are outside existing Trip range`() = runTest {
        // Arrange
        val existingTrip = Trip(id = "trip123", title = "My Trip", startDate = "20/05/2026", endDate = "25/05/2026", description = "")
        val room = HotelRoom("room1", "Deluxe", 100.0, emptyList())
        val hotel = Hotel("1", "Hotel Paris", "Address", 4, "url", listOf(room))
        
        // Dates outside (starts before trip)
        val startDate = "15/05/2026" 
        val endDate = "22/05/2026"

        hotelViewModel.onTripSelected(existingTrip)
        hotelViewModel.onCitySelected("Paris")

        // Act
        hotelViewModel.confirmReservation(hotel, "room1", startDate, endDate)

        // Assert
        assertFalse(hotelViewModel.reservationSuccess.value)
        assertNotNull(hotelViewModel.startDateError.value)
        coVerify(exactly = 0) { hotelRepository.reserveRoom(any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `onTripSelected sets filter dates automatically`() {
        val trip = Trip(id = "t1", title = "Paris", startDate = "10/06/2026", endDate = "15/06/2026", description = "")
        
        hotelViewModel.onTripSelected(trip)
        
        assertEquals("10/06/2026", hotelViewModel.startDate.value)
        assertEquals("15/06/2026", hotelViewModel.endDate.value)
        assertEquals(trip, hotelViewModel.selectedTrip.value)
    }
}
