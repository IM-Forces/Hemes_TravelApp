package com.example.hermes_travelapp.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hermes_travelapp.R
import com.example.hermes_travelapp.domain.Trip
import com.example.hermes_travelapp.ui.theme.Hermes_travelappTheme
import com.example.hermes_travelapp.ui.viewmodels.TripViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTripScreen(
    tripToEdit: Trip? = null,
    tripViewModel: TripViewModel? = null,
    onBack: () -> Unit = {},
    onSaveTrip: (Trip) -> Unit = {}
) {
    var title by remember { mutableStateOf(tripToEdit?.title ?: "") }
    var startDate by remember { mutableStateOf(tripToEdit?.startDate ?: "") }
    var endDate by remember { mutableStateOf(tripToEdit?.endDate ?: "") }
    var budget by remember { mutableStateOf(tripToEdit?.budget?.toString() ?: "") }
    var description by remember { mutableStateOf(tripToEdit?.description ?: "") }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val errorMessageState = tripViewModel?.errorMessage?.observeAsState()
    val errorMessage = errorMessageState?.value

    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { tripViewModel?.clearError() },
            confirmButton = {
                TextButton(onClick = { tripViewModel?.clearError() }) {
                    Text(stringResource(R.string.back))
                }
            },
            title = { Text("Error", fontWeight = FontWeight.Bold) },
            text = { Text(errorMessage) }
        )
    }

    if (showStartDatePicker) {
        DatePickerDialogWrapper(
            onDateSelected = { startDate = it },
            onDismiss = { showStartDatePicker = false }
        )
    }

    if (showEndDatePicker) {
        DatePickerDialogWrapper(
            onDateSelected = { endDate = it },
            onDismiss = { showEndDatePicker = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(if (tripToEdit == null) R.string.create_trip_title else R.string.edit_trip_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.trip_field_title) + " *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = startDate,
                onValueChange = { },
                label = { Text(stringResource(R.string.trip_field_start) + " *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showStartDatePicker = true },
                enabled = false,
                readOnly = true,
                leadingIcon = { Icon(Icons.Default.CalendarMonth, null) },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )

            OutlinedTextField(
                value = endDate,
                onValueChange = { },
                label = { Text(stringResource(R.string.trip_field_end) + " *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showEndDatePicker = true },
                enabled = false,
                readOnly = true,
                leadingIcon = { Icon(Icons.Default.CalendarMonth, null) },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )

            OutlinedTextField(
                value = budget,
                onValueChange = { budget = it },
                label = { Text(stringResource(R.string.prefs_settings) + " (€)") }, // Usando un string existente de presupuesto si hubiera uno mejor
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.trip_field_desc)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val trip = tripToEdit?.copy(
                            title = title,
                            startDate = startDate,
                            endDate = endDate,
                            budget = budget.toIntOrNull() ?: 0,
                            description = description
                        ) ?: Trip(
                            title = title,
                            startDate = startDate,
                            endDate = endDate,
                            budget = budget.toIntOrNull() ?: 0,
                            description = description
                        )
                        onSaveTrip(trip)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.medium,
                enabled = title.isNotBlank()
            ) {
                Text(stringResource(if (tripToEdit == null) R.string.trip_save else R.string.prefs_save))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialogWrapper(onDateSelected: (String) -> Unit, onDismiss: () -> Unit) {
    val datePickerState = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let {
                    val date = Date(it)
                    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    onDateSelected(formatter.format(date))
                }
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
fun CreateTripScreenPreview() {
    Hermes_travelappTheme {
        CreateTripScreen()
    }
}
