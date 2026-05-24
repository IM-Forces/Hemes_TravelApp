package com.example.hermes_travelapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hermes_travelapp.R
import com.example.hermes_travelapp.ui.viewmodels.HotelViewModel
import kotlin.math.roundToInt

import androidx.compose.ui.tooling.preview.Preview
import com.example.hermes_travelapp.ui.theme.Hermes_travelappTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelSearchScreen(
    viewModel: HotelViewModel,
    onBack: () -> Unit = {},
    onNavigateToResults: () -> Unit,
    onProfileClick: () -> Unit = {},
    showBack: Boolean = true,
    username: String = "Usuario"
) {
    val city by viewModel.city.collectAsState()
    val startDate by viewModel.startDate.collectAsState()
    val endDate by viewModel.endDate.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()
    
    val cityError by viewModel.cityError.collectAsState()
    val startDateError by viewModel.startDateError.collectAsState()
    val endDateError by viewModel.endDateError.collectAsState()
    
    val maxPrice by viewModel.maxPrice.collectAsState()
    val stars by viewModel.stars.collectAsState()
    val trips by viewModel.trips.collectAsState()
    val selectedTrip by viewModel.selectedTrip.collectAsState()

    HotelSearchContent(
        city = city,
        startDate = startDate,
        endDate = endDate,
        isLoading = isLoading,
        error = error,
        cityError = cityError,
        startDateError = startDateError,
        endDateError = endDateError,
        maxPrice = maxPrice,
        stars = stars,
        trips = trips,
        selectedTrip = selectedTrip,
        onBack = onBack,
        onProfileClick = onProfileClick,
        showBack = showBack,
        username = username,
        onCitySelected = viewModel::onCitySelected,
        onStartDateSelected = viewModel::onStartDateSelected,
        onEndDateSelected = viewModel::onEndDateSelected,
        onMaxPriceChanged = viewModel::onMaxPriceChanged,
        onStarsChanged = viewModel::onStarsChanged,
        onTripSelected = viewModel::onTripSelected,
        onSearchClick = { viewModel.searchHotels(onSuccess = onNavigateToResults) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelSearchContent(
    city: String,
    startDate: String,
    endDate: String,
    isLoading: Boolean,
    error: String?,
    cityError: String?,
    startDateError: String?,
    endDateError: String?,
    maxPrice: Float,
    stars: Int,
    trips: List<com.example.hermes_travelapp.domain.model.Trip> = emptyList(),
    selectedTrip: com.example.hermes_travelapp.domain.model.Trip? = null,
    onBack: () -> Unit,
    onProfileClick: () -> Unit = {},
    onCitySelected: (String) -> Unit,
    onStartDateSelected: (String) -> Unit,
    onEndDateSelected: (String) -> Unit,
    onMaxPriceChanged: (Float) -> Unit,
    onStarsChanged: (Int) -> Unit,
    onTripSelected: (com.example.hermes_travelapp.domain.model.Trip?) -> Unit = {},
    onSearchClick: () -> Unit,
    showBack: Boolean = true,
    username: String = "Usuario"
) {
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    
    val cities = listOf("London", "Paris", "Barcelona")
    var cityExpanded by remember { mutableStateOf(false) }
    var tripExpanded by remember { mutableStateOf(false) }

    val initials = remember(username) {
        if (username.isBlank()) "U"
        else if (username.length >= 2) username.take(2).uppercase()
        else username.take(1).uppercase()
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Pill header style
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.secondary
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (showBack) {
                                IconButton(
                                    onClick = onBack,
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(R.string.back),
                                        tint = MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                            }
                            Text(
                                text = stringResource(R.string.hotel_search_title),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                        
                        Surface(
                            onClick = onProfileClick,
                            modifier = Modifier.size(44.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = initials,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Trip Selector (T2.3)
                if (trips.isNotEmpty()) {
                    Column {
                        ExposedDropdownMenuBox(
                            expanded = tripExpanded,
                            onExpandedChange = { tripExpanded = !tripExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedTrip?.title ?: "Seleccionar viaje (opcional)",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Vincular a un viaje") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tripExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = tripExpanded,
                                onDismissRequest = { tripExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Ninguno (Crear nuevo)") },
                                    onClick = {
                                        onTripSelected(null)
                                        tripExpanded = false
                                    }
                                )
                                trips.forEach { trip ->
                                    DropdownMenuItem(
                                        text = { Text(trip.title) },
                                        onClick = {
                                            onTripSelected(trip)
                                            tripExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                        if (selectedTrip != null) {
                            Text(
                                text = "Fechas del viaje: ${selectedTrip.startDate} - ${selectedTrip.endDate}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                            )
                        }
                    }
                }

                // City Selector
                Column {
                    ExposedDropdownMenuBox(
                        expanded = cityExpanded,
                        onExpandedChange = { cityExpanded = !cityExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = city,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.hotel_search_city)) },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            isError = cityError != null
                        )
                        ExposedDropdownMenu(
                            expanded = cityExpanded,
                            onDismissRequest = { cityExpanded = false }
                        ) {
                            cities.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        onCitySelected(selectionOption)
                                        cityExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    if (cityError != null) {
                        Text(
                            text = cityError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                        )
                    }
                }

                // Start Date
            Column {
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.hotel_search_start_date)) },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        isError = startDateError != null
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showStartPicker = true }
                    )
                }
                if (startDateError != null) {
                    Text(
                        text = startDateError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                }
            }

            // End Date
            Column {
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.hotel_search_end_date)) },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        isError = endDateError != null
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showEndPicker = true }
                    )
                }
                if (endDateError != null) {
                    Text(
                        text = endDateError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                }
            }

                // Price Filter
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.hotel_search_max_price, maxPrice.roundToInt()),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Slider(
                        value = maxPrice,
                        onValueChange = onMaxPriceChanged,
                        valueRange = 0f..1000f,
                        steps = 19,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.24f)
                        )
                    )
                }

                // Stars Filter
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.hotel_search_stars),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        repeat(5) { index ->
                            val starIndex = index + 1
                            Icon(
                                imageVector = if (stars >= starIndex) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = if (stars >= starIndex) Color(0xFFFFD700) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable { 
                                        if (stars == starIndex) onStarsChanged(0)
                                        else onStarsChanged(starIndex)
                                    }
                            )
                        }
                    }
                }

                if (error != null) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onSearchClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(stringResource(R.string.hotel_search_button), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showStartPicker) {
        DatePickerDialogWrapper(
            initialDate = startDate,
            onDateSelected = { 
                onStartDateSelected(it)
                showStartPicker = false
            },
            onDismiss = { showStartPicker = false }
        )
    }

    if (showEndPicker) {
        DatePickerDialogWrapper(
            initialDate = endDate,
            onDateSelected = { 
                onEndDateSelected(it)
                showEndPicker = false
            },
            onDismiss = { showEndPicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialogWrapper(
    initialDate: String = "",
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = if (initialDate.isNotEmpty()) {
            try {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(initialDate)?.time
            } catch (e: Exception) {
                System.currentTimeMillis()
            }
        } else {
            System.currentTimeMillis()
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let {
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    onDateSelected(sdf.format(Date(it)))
                }
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Preview(showBackground = true)
@Composable
fun HotelSearchScreenPreview() {
    Hermes_travelappTheme {
        HotelSearchContent(
            city = "Barcelona",
            startDate = "20/12/2024",
            endDate = "27/12/2024",
            isLoading = false,
            error = null,
            cityError = null,
            startDateError = null,
            endDateError = null,
            maxPrice = 500f,
            stars = 0,
            onBack = {},
            onCitySelected = {},
            onStartDateSelected = {},
            onEndDateSelected = {},
            onMaxPriceChanged = {},
            onStarsChanged = {},
            onSearchClick = {}
        )
    }
}
