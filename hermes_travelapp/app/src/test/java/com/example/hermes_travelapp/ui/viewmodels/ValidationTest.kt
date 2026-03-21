package com.example.hermes_travelapp.ui.viewmodels

import com.example.hermes_travelapp.domain.model.Trip
import com.example.hermes_travelapp.domain.ValidationUtils
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ValidationTest {

    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    @Test
    fun `test startDate must be before endDate`() {
        val futureDate = LocalDate.now().plusYears(1)
        val startDate = futureDate.plusDays(10).format(formatter)
        val endDate = futureDate.plusDays(5).format(formatter)
        
        val result = ValidationUtils.validateTripDates(startDate, endDate)
        
        assertEquals("La fecha de inicio debe ser anterior a la de fin", result)
    }

    @Test
    fun `test activity date must be within trip range`() {
        val futureDate = LocalDate.now().plusYears(1)
        val tripStart = futureDate.format(formatter)
        val tripEnd = futureDate.plusDays(10).format(formatter)
        
        val beforeDate = futureDate.minusDays(1)
        assertEquals(
            "La fecha de la actividad debe estar dentro del rango del viaje",
            ValidationUtils.validateActivityDate(beforeDate, tripStart, tripEnd)
        )
        
        val afterDate = futureDate.plusDays(11)
        assertEquals(
            "La fecha de la actividad debe estar dentro del rango del viaje",
            ValidationUtils.validateActivityDate(afterDate, tripStart, tripEnd)
        )
        
        val validDate = futureDate.plusDays(5)
        assertNull(ValidationUtils.validateActivityDate(validDate, tripStart, tripEnd))
    }

    @Test
    fun `test required fields must not be empty`() {
        val tripWithEmptyTitle = Trip(title = "", startDate = "10/10/2025", endDate = "20/10/2025", description = "Desc")
        assertEquals("Todos los campos obligatorios deben estar rellenos", ValidationUtils.validateTripFields(tripWithEmptyTitle))
        
        val tripWithEmptyDesc = Trip(title = "Trip", startDate = "10/10/2025", endDate = "20/10/2025", description = "")
        assertEquals("Todos los campos obligatorios deben estar rellenos", ValidationUtils.validateTripFields(tripWithEmptyDesc))
        
        val validTrip = Trip(title = "Trip", startDate = "10/10/2025", endDate = "20/10/2025", description = "Desc")
        assertNull(ValidationUtils.validateTripFields(validTrip))
    }

    @Test
    fun `test dates must be in the future`() {
        val pastDate = LocalDate.now().minusDays(1).format(formatter)
        val futureEndDate = LocalDate.now().plusDays(5).format(formatter)
        
        val result = ValidationUtils.validateTripDates(pastDate, futureEndDate)
        
        assertEquals("La fecha de inicio no puede estar en el pasado", result)
    }

    @Test
    fun `test valid trip data returns no error`() {
        val futureStart = LocalDate.now().plusDays(1).format(formatter)
        val futureEnd = LocalDate.now().plusDays(10).format(formatter)
        
        assertNull(ValidationUtils.validateTripDates(futureStart, futureEnd))
    }
}
