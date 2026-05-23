package com.example.hermes_travelapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import android.net.Uri
import com.example.hermes_travelapp.domain.model.Trip
import com.example.hermes_travelapp.domain.generateDaysForTrip
import com.example.hermes_travelapp.domain.model.TripDay
import com.example.hermes_travelapp.domain.repository.TripDayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for managing the state and business logic of trip days.
 * Follows the same pattern as ActivityViewModel.
 */
@HiltViewModel
class TripDayViewModel @Inject constructor(
    private val repository: TripDayRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private companion object {
        const val TAG = "TripDayViewModel"
    }

    private val _tripDays = MutableStateFlow<List<TripDay>>(emptyList())
    /**
     * Observable stream of trip days for the currently selected trip.
     */
    val tripDays: StateFlow<List<TripDay>> = _tripDays.asStateFlow()

    private var loadJob: Job? = null

    /**
     * Loads days for a specific trip and updates the [tripDays] StateFlow.
     * @param tripId The unique identifier of the trip.
     */
    fun loadDaysForTrip(tripId: String) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            repository.getDaysForTrip(tripId).collect { days ->
                _tripDays.value = days
                Log.d(TAG, "loadDaysForTrip: collected ${days.size} days for trip $tripId")
            }
        }
    }

    fun addDay(tripId: String, onUpdateEndDate: (String) -> Unit) {
        viewModelScope.launch {
            val lastDay = repository.getLastDayForTrip(tripId)
            val newDayNumber = (lastDay?.dayNumber ?: 0) + 1
            val newDate = lastDay?.date?.plusDays(1) ?: java.time.LocalDate.now()
            val newDay = TripDay(
                id = UUID.randomUUID().toString(),
                tripId = tripId,
                dayNumber = newDayNumber,
                date = newDate
            )
            repository.addDay(newDay)
            
            val formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
            onUpdateEndDate(newDate.format(formatter))
            Log.d(TAG, "addDay: added day $newDayNumber for trip $tripId")
        }
    }

    /**
     * Generates all days for a trip based on its start and end dates.
     */
    suspend fun generateDays(trip: Trip) {
        generateDaysForTrip(trip, repository)
        Log.d(TAG, "generateDays: Days generated for trip ${trip.id}")
    }

    fun deleteDay(dayId: String, tripId: String, tripStartDate: String, onUpdateEndDate: (String) -> Unit) {
        viewModelScope.launch {
            repository.deleteDay(dayId)

            val formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val startDate = java.time.LocalDate.parse(tripStartDate, formatter)

            // Reordenar todos los días restantes
            // Usamos first() para obtener el valor actual del Flow una sola vez
            val remainingDays = repository.getDaysForTrip(tripId).first()
            repository.clearDaysForTrip(tripId)

            remainingDays.forEachIndexed { index, day ->
                repository.addDay(
                    day.copy(
                        dayNumber = index + 1,
                        date = startDate.plusDays(index.toLong())
                    )
                )
            }

            // Actualizar fecha fin con el último día
            val lastDay = repository.getLastDayForTrip(tripId)
            if (lastDay != null) {
                onUpdateEndDate(lastDay.date.format(formatter))
            }

            Log.d(TAG, "deleteDay: deleted day $dayId, reordered ${remainingDays.size} days for trip $tripId")
        }
    }

    fun addPhotosToDay(dayId: String, uris: List<Uri>) {
        viewModelScope.launch {
            val day = _tripDays.value.find { it.id == dayId } ?: return@launch
            val newPhotos = uris.mapNotNull { uri ->
                saveImageToInternalStorage(uri)
            }
            if (newPhotos.isNotEmpty()) {
                val updatedDay = day.copy(photos = day.photos + newPhotos)
                repository.updateDay(updatedDay)
                Log.d(TAG, "addPhotosToDay: added ${newPhotos.size} photos to day $dayId")
            }
        }
    }

    fun deletePhotoFromDay(dayId: String, photoUrl: String) {
        viewModelScope.launch {
            val day = _tripDays.value.find { it.id == dayId } ?: return@launch
            val updatedPhotos = day.photos.filter { it != photoUrl }
            if (updatedPhotos.size != day.photos.size) {
                val updatedDay = day.copy(photos = updatedPhotos)
                repository.updateDay(updatedDay)
                
                // Opcional: Eliminar el archivo físico si está en almacenamiento interno
                try {
                    val file = File(photoUrl)
                    if (file.exists() && photoUrl.startsWith(context.filesDir.absolutePath)) {
                        file.delete()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error deleting physical file: ${e.message}")
                }
                
                Log.d(TAG, "deletePhotoFromDay: removed photo from day $dayId")
            }
        }
    }

    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val fileName = "photo_${UUID.randomUUID()}.jpg"
            val file = File(context.filesDir, fileName)
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            file.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Error saving image: ${e.message}", e)
            null
        }
    }
}
