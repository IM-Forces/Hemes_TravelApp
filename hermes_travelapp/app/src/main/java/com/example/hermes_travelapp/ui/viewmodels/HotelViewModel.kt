package com.example.hermes_travelapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hermes_travelapp.data.PreferencesManager
import com.example.hermes_travelapp.domain.generateDaysForTrip
import com.example.hermes_travelapp.domain.model.Hotel
import com.example.hermes_travelapp.domain.model.HotelRoom
import com.example.hermes_travelapp.domain.model.ReservationUI
import com.example.hermes_travelapp.domain.model.Trip
import com.example.hermes_travelapp.domain.repository.HotelRepository
import com.example.hermes_travelapp.domain.repository.ReservationRepository
import com.example.hermes_travelapp.domain.repository.TripDayRepository
import com.example.hermes_travelapp.domain.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HotelViewModel @Inject constructor(
    private val repository: HotelRepository,
    private val reservationRepository: ReservationRepository,
    private val tripRepository: TripRepository,
    private val tripDayRepository: TripDayRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private companion object {
        const val TAG = "HotelViewModel"
    }

    // Form State
    private val _city = MutableStateFlow("")
    val city: StateFlow<String> = _city.asStateFlow()

    private val _startDate = MutableStateFlow("")
    val startDate: StateFlow<String> = _startDate.asStateFlow()

    private val _endDate = MutableStateFlow("")
    val endDate: StateFlow<String> = _endDate.asStateFlow()

    private val _maxPrice = MutableStateFlow(500f)
    val maxPrice: StateFlow<Float> = _maxPrice.asStateFlow()

    private val _stars = MutableStateFlow(0)
    val stars: StateFlow<Int> = _stars.asStateFlow()

    // Validation State
    private val _cityError = MutableStateFlow<String?>(null)
    val cityError: StateFlow<String?> = _cityError.asStateFlow()

    private val _startDateError = MutableStateFlow<String?>(null)
    val startDateError: StateFlow<String?> = _startDateError.asStateFlow()

    private val _endDateError = MutableStateFlow<String?>(null)
    val endDateError: StateFlow<String?> = _endDateError.asStateFlow()

    // Search Results State
    private val _availableHotels = MutableStateFlow<List<Hotel>>(emptyList())
    val availableHotels: StateFlow<List<Hotel>> = _availableHotels.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Reservation State
    private val _isReserving = MutableStateFlow(false)
    val isReserving: StateFlow<Boolean> = _isReserving.asStateFlow()

    private val _reservationSuccess = MutableStateFlow(false)
    val reservationSuccess: StateFlow<Boolean> = _reservationSuccess.asStateFlow()

    // T2.3: Trips for selection
    val trips: StateFlow<List<Trip>> = tripRepository.getTrips()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedTrip = MutableStateFlow<Trip?>(null)
    val selectedTrip: StateFlow<Trip?> = _selectedTrip.asStateFlow()

    // Form Setters
    fun onTripSelected(trip: Trip?) {
        _selectedTrip.value = trip
        if (trip != null) {
            _city.value = trip.destinationCity
            _startDate.value = trip.startDate
            _endDate.value = trip.endDate
            _cityError.value = null
            _startDateError.value = null
            _endDateError.value = null
            // Reset search results when changing trip to ensure new search respects trip dates and city
            _availableHotels.value = emptyList()
            Log.d(TAG, "onTripSelected: Selected trip ${trip.title}, city ${trip.destinationCity}, dates set to ${trip.startDate} - ${trip.endDate}")
        }
    }

    fun onCitySelected(city: String) {
        _city.value = city
        _cityError.value = null
    }

    fun onStartDateSelected(date: String) {
        _startDate.value = date
        _startDateError.value = null
    }

    fun onEndDateSelected(date: String) {
        _endDate.value = date
        _endDateError.value = null
    }

    fun onMaxPriceChanged(price: Float) {
        _maxPrice.value = price
    }

    fun onStarsChanged(stars: Int) {
        _stars.value = stars
    }

    /**
     * Searches for available hotels based on city and dates.
     */
    fun searchHotels(onSuccess: () -> Unit = {}) {
        val currentCity = _city.value
        val start = _startDate.value
        val end = _endDate.value

        if (!validate(currentCity, start, end)) return

        Log.d(TAG, "searchHotels: Searching for hotels in $currentCity from $start to $end")
        _isLoading.value = true
        _errorMessage.value = null

        val apiStartDate = convertDateFormat(start)
        val apiEndDate = convertDateFormat(end)

        viewModelScope.launch {
            repository.checkAvailability(
                groupId = "G03",
                city = currentCity,
                startDate = apiStartDate,
                endDate = apiEndDate
            ).onSuccess { hotels ->
                Log.d(TAG, "searchHotels: Successfully found ${hotels.size} hotels")
                _availableHotels.value = hotels
                _isLoading.value = false
                onSuccess()
            }.onFailure { exception ->
                Log.e(TAG, "searchHotels: Error searching hotels: ${exception.message}")
                val displayError = if (exception is HttpException) {
                    try {
                        val errorBody = exception.response()?.errorBody()?.string()
                        if (errorBody?.contains("detail") == true) {
                            errorBody.substringAfter("\"detail\":\"").substringBefore("\"")
                        } else {
                            "Error del servidor: ${exception.code()}"
                        }
                    } catch (e: Exception) {
                        "Error al buscar hoteles"
                    }
                } else {
                    exception.message ?: "Error al buscar hoteles"
                }
                _errorMessage.value = displayError
                _isLoading.value = false
            }
        }
    }

    private fun validate(city: String, start: String, end: String): Boolean {
        var isValid = true
        _cityError.value = null
        _startDateError.value = null
        _endDateError.value = null

        if (city.isBlank()) {
            _cityError.value = "La ciudad es obligatoria"
            isValid = false
        }
        if (start.isBlank()) {
            _startDateError.value = "La fecha de entrada es obligatoria"
            isValid = false
        }
        if (end.isBlank()) {
            _endDateError.value = "La fecha de salida es obligatoria"
            isValid = false
        }

        if (start.isNotBlank() && end.isNotBlank()) {
            try {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val startObj = sdf.parse(start)
                val endObj = sdf.parse(end)
                if (startObj != null && endObj != null && !startObj.before(endObj)) {
                    _endDateError.value = "La entrada debe ser anterior a la salida"
                    isValid = false
                }

                // T2.3: Check if dates are within selected trip range
                _selectedTrip.value?.let { trip ->
                    val tripStart = sdf.parse(trip.startDate)
                    val tripEnd = sdf.parse(trip.endDate)
                    if (tripStart != null && startObj != null && startObj.before(tripStart)) {
                        _startDateError.value = "La fecha de entrada está fuera del rango del viaje (${trip.startDate})"
                        isValid = false
                    }
                    if (tripEnd != null && endObj != null && endObj.after(tripEnd)) {
                        _endDateError.value = "La fecha de salida está fuera del rango del viaje (${trip.endDate})"
                        isValid = false
                    }

                    // Check if city matches trip destination
                    if (city != trip.destinationCity) {
                        _cityError.value = "La ciudad debe coincidir con el destino del viaje: ${trip.destinationCity}"
                        isValid = false
                    }
                }
            } catch (e: Exception) {
                _endDateError.value = "Formato de fecha inválido"
                isValid = false
            }
        }
        return isValid
    }

    private fun convertDateFormat(date: String): String {
        return try {
            val inputSdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputSdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateObj = inputSdf.parse(date)
            if (dateObj != null) outputSdf.format(dateObj) else date
        } catch (e: Exception) {
            date
        }
    }

    fun clearError() {
        Log.d(TAG, "clearError: Clearing errors")
        _errorMessage.value = null
        _cityError.value = null
        _startDateError.value = null
        _endDateError.value = null
    }

    /**
     * Confirms a hotel reservation.
     */
    fun confirmReservation(
        hotel: Hotel,
        roomId: String,
        startDate: String,
        endDate: String,
        onSuccess: () -> Unit = {}
    ) {
        val guestName = preferencesManager.username
        val guestEmail = preferencesManager.email

        Log.d(TAG, "confirmReservation: hotelId=${hotel.id}, roomId=$roomId, guest=$guestName")
        
        if (guestName.isBlank() || guestEmail.isBlank()) {
            _errorMessage.value = "Datos de usuario incompletos en el perfil"
            return
        }

        // T2.3: Validate dates again (especially against trip range if selected)
        if (!validate(_city.value, startDate, endDate)) {
            return
        }

        _isReserving.value = true
        _errorMessage.value = null
        _reservationSuccess.value = false

        val apiStartDate = convertDateFormat(startDate)
        val apiEndDate = convertDateFormat(endDate)

        viewModelScope.launch {
            // Local validation: Check if a reservation already exists for the same room and dates
            try {
                val existingReservations = reservationRepository.getAllReservations().first()
                val duplicate = existingReservations.find { 
                    it.hotelName == hotel.name && it.roomId == roomId && 
                    it.checkInDate == startDate && it.checkOutDate == endDate 
                }
                
                if (duplicate != null) {
                    _errorMessage.value = "Ya tienes una reserva idéntica guardada localmente."
                    _isReserving.value = false
                    return@launch
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error checking local duplicates: ${e.message}")
            }

            repository.reserveRoom(
                groupId = "G03",
                hotelId = hotel.id,
                roomId = roomId,
                startDate = apiStartDate,
                endDate = apiEndDate,
                guestName = guestName,
                guestEmail = guestEmail
            ).onSuccess { apiReservation ->
                Log.d(TAG, "confirmReservation: API Success. Saving locally...")
                
                try {
                    val tripToLink = _selectedTrip.value
                    val currentTrips = tripRepository.getTrips().first()
                    
                    var finalTripId: String
                    var isNewTrip = false

                    // 1. First, ensure the Trip exists and is PERSISTED
                    val existingTrip = currentTrips.find { it.title == "Viaje a ${hotel.name}" }
                    
                    if (tripToLink != null) {
                        finalTripId = tripToLink.id
                    } else if (existingTrip != null) {
                        finalTripId = existingTrip.id
                    } else {
                        val newId = UUID.randomUUID().toString()
                        val newTrip = Trip(
                            id = newId,
                            title = "Viaje a ${hotel.name}",
                            startDate = startDate,
                            endDate = endDate,
                            destinationCity = hotel.address.split(",").last().trim(), // Use city from address
                            description = "Estancia en ${hotel.name} (${hotel.address})",
                            coverPhotoUrl = hotel.imageUrl
                        )
                        Log.d(TAG, "confirmReservation: Creating new trip: ${newTrip.title} in ${newTrip.destinationCity}")
                        tripRepository.addTrip(newTrip)
                        finalTripId = newId
                        isNewTrip = true
                    }

                    // 2. Generate days if it's a new trip
                    if (isNewTrip) {
                        val createdTrip = Trip(
                            id = finalTripId,
                            title = "Viaje a ${hotel.name}",
                            startDate = startDate,
                            endDate = endDate,
                            destinationCity = hotel.address.split(",").last().trim(),
                            description = "Estancia en ${hotel.name}"
                        )
                        generateDaysForTrip(createdTrip, tripDayRepository)
                    }

                    // 3. Finally, save the reservation. 
                    // Now we are sure the trip with finalTripId exists in the DB.
                    val reservationUI = ReservationUI(
                        id = apiReservation.id,
                        tripId = finalTripId,
                        roomId = roomId,
                        hotelName = hotel.name,
                        hotelAddress = hotel.address,
                        checkInDate = startDate,
                        checkOutDate = endDate,
                        hotelImageUrl = hotel.imageUrl,
                        roomImageUrl = hotel.rooms.find { it.id == roomId }?.images?.firstOrNull() ?: hotel.imageUrl
                    )
                    reservationRepository.addReservation(reservationUI)
                    
                    _isReserving.value = false
                    _reservationSuccess.value = true
                    onSuccess()
                } catch (e: Exception) {
                    Log.e(TAG, "confirmReservation: Error saving local reservation/trip", e)
                    _errorMessage.value = "Reserva realizada, pero hubo un error al vincularla al viaje: ${e.message}"
                    _isReserving.value = false
                }
            }.onFailure { exception ->
                Log.e(TAG, "confirmReservation: Error: ${exception.message}")
                
                // Handle 409 Conflict specifically
                val errorMsg = if (exception is retrofit2.HttpException && exception.code() == 409) {
                    "Esta habitación ya no está disponible para las fechas seleccionadas (Conflicto 409)."
                } else {
                    exception.message ?: "Error al realizar la reserva"
                }
                
                _errorMessage.value = errorMsg
                _isReserving.value = false
            }
        }
    }

    fun resetReservationState() {
        _reservationSuccess.value = false
        _errorMessage.value = null
    }

    /**
     * Creates a local reservation in the database.
     */
    fun createReservation(tripId: String, hotel: Hotel, roomId: String) {
        val start = _startDate.value
        val end = _endDate.value

        if (!validate(_city.value, start, end)) return

        _isReserving.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                // Ensure the trip exists and match dates if it's the selected one
                val trip = trips.value.find { it.id == tripId }

                // Local validation for linked trips too
                val existingReservations = reservationRepository.getAllReservations().first()
                val duplicate = existingReservations.find { 
                    it.hotelName == hotel.name && it.roomId == roomId && 
                    it.checkInDate == start && it.checkOutDate == end 
                }
                
                if (duplicate != null) {
                    _errorMessage.value = "Ya tienes una reserva para esta habitación en estas fechas."
                    _isReserving.value = false
                    return@launch
                }

                val reservation = ReservationUI(
                    id = UUID.randomUUID().toString(),
                    tripId = tripId,
                    roomId = roomId,
                    hotelName = hotel.name,
                    hotelAddress = hotel.address,
                    checkInDate = start,
                    checkOutDate = end,
                    hotelImageUrl = hotel.imageUrl,
                    roomImageUrl = hotel.rooms.find { it.id == roomId }?.images?.firstOrNull() ?: hotel.imageUrl
                )
                
                reservationRepository.addReservation(reservation)
                
                Log.d(TAG, "createReservation: Local reservation saved successfully")
                _reservationSuccess.value = true
                _isReserving.value = false
            } catch (e: Exception) {
                Log.e(TAG, "createReservation: Error saving local reservation", e)
                _errorMessage.value = "Error al guardar la reserva localmente"
                _isReserving.value = false
            }
        }
    }
}
