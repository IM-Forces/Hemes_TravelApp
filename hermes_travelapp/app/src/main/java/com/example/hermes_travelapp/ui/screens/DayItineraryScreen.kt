package com.example.hermes_travelapp.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hermes_travelapp.domain.ItineraryItem
import com.example.hermes_travelapp.ui.theme.*
import com.example.hermes_travelapp.ui.viewmodels.ActivityViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

// --- Data Models ---

data class TripDayInfo(
    val id: String,
    val dayNumber: Int,
    val date: String,
    val dayOfWeek: String,
    val subtitle: String,
    val activitiesCount: Int,
    val budget: String = "€0"
)

// --- Mock Data ---

val mockDays = listOf(
    TripDayInfo("day1", 1, "15 Jun", "Miér", "Llegada a Atenas", 0, "€45"),
    TripDayInfo("day2", 2, "16 Jun", "Juev", "Acrópolis y Plaka", 5, "€120"),
    TripDayInfo("day3", 3, "17 Jun", "Vier", "Museos de Atenas", 0, "€30"),
    TripDayInfo("day4", 4, "18 Jun", "Sáb", "Viaje a Mykonos", 2, "€200")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayItineraryScreen(
    tripId: String = "grecia_trip",
    dayId: String = "day1",
    activityViewModel: ActivityViewModel = viewModel(),
    onBack: () -> Unit = {},
    onNavigateToEditActivity: (activityId: String) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // State for activities from ViewModel
    val activities by activityViewModel.activities.collectAsState()
    val dayCounts by activityViewModel.dayCounts.collectAsState()

    // State for day navigation
    val initialPageIndex = mockDays.indexOfFirst { it.id == dayId }.coerceAtLeast(0)
    val pagerState = rememberPagerState(initialPage = initialPageIndex) { mockDays.size }
    
    // Deletion State
    var showDeleteDialog by remember { mutableStateOf(false) }
    var activityToDelete by remember { mutableStateOf<ItineraryItem?>(null) }

    // Bottom Sheet State
    var showAddSheet by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf(false) }
    var activityToEdit by remember { mutableStateOf<ItineraryItem?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val editSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Initial load of all counts
    LaunchedEffect(Unit) {
        activityViewModel.loadAllDayCounts(tripId, mockDays.map { it.id })
    }

    // Sync ViewModel with current page
    LaunchedEffect(pagerState.currentPage) {
        val currentDayId = mockDays[pagerState.currentPage].id
        activityViewModel.loadActivitiesForDay(tripId, currentDayId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Grecia Clásica", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSheet = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir actividad", modifier = Modifier.size(32.dp))
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. TOP SECTION - HORIZONTAL DAY CAROUSEL
            DayCarousel(
                days = mockDays,
                dayCounts = dayCounts,
                selectedPageIndex = pagerState.currentPage,
                onDayClick = { index ->
                    scope.launch { pagerState.animateScrollToPage(index) }
                }
            )

            // 2. MAIN CONTENT - SWIPEABLE DAYS
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.Top
            ) { pageIndex ->
                val day = mockDays[pageIndex]
                
                DayContent(
                    day = day,
                    activities = activities,
                    onEdit = { id ->
                        activityToEdit = activities.find { it.id == id }
                        showEditSheet = true
                    },
                    onDelete = { activity ->
                        activityToDelete = activity
                        showDeleteDialog = true
                    },
                    onAddFirst = { showAddSheet = true }
                )
            }
        }

        // Add Activity Bottom Sheet
        if (showAddSheet) {
            AddActivityBottomSheet(
                onDismiss = { showAddSheet = false },
                onAdd = { newItem ->
                    val currentDay = mockDays[pagerState.currentPage]
                    val activityWithContext = newItem.copy(
                        tripId = tripId,
                        dayId = currentDay.id,
                        date = getLocalDateForDay(currentDay.id)
                    )
                    activityViewModel.addActivity(activityWithContext)
                    showAddSheet = false
                    scope.launch {
                        snackbarHostState.showSnackbar("Actividad añadida: ${newItem.title}")
                    }
                },
                sheetState = sheetState
            )
        }

        // Edit Activity Bottom Sheet
        if (showEditSheet && activityToEdit != null) {
            EditActivityBottomSheet(
                activity = activityToEdit!!,
                onDismiss = { 
                    showEditSheet = false
                    activityToEdit = null
                },
                onSave = { updatedItem ->
                    activityViewModel.updateActivity(updatedItem)
                    showEditSheet = false
                    activityToEdit = null
                    scope.launch {
                        snackbarHostState.showSnackbar("Actividad actualizada: ${updatedItem.title}")
                    }
                },
                sheetState = editSheetState
            )
        }

        // Delete Confirmation Dialog
        if (showDeleteDialog && activityToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("¿Eliminar actividad?") },
                text = { Text("Se eliminará \"${activityToDelete?.title}\" permanentemente.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val activity = activityToDelete
                            if (activity != null) {
                                activityViewModel.deleteActivity(activity.id, tripId, activity.dayId)
                                showDeleteDialog = false
                                scope.launch {
                                    snackbarHostState.showSnackbar("Actividad eliminada")
                                }
                            }
                        }
                    ) {
                        Text("ELIMINAR", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("CANCELAR")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityBottomSheet(
    onDismiss: () -> Unit,
    onAdd: (ItineraryItem) -> Unit,
    sheetState: SheetState
) {
    var title by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf(false) }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var descriptionError by remember { mutableStateOf(false) }
    var cost by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf(LocalTime.now()) }
    var showTimePicker by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Nueva Actividad",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Title Field
            OutlinedTextField(
                value = title,
                onValueChange = { 
                    title = it
                    if (it.isNotBlank()) titleError = false
                },
                label = { Text("Título de la actividad *") },
                modifier = Modifier.fillMaxWidth(),
                isError = titleError,
                supportingText = { if (titleError) Text("El título es obligatorio") },
                shape = RoundedCornerShape(12.dp)
            )

            // Time Picker Trigger
            OutlinedTextField(
                value = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                onValueChange = {},
                label = { Text("Hora *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTimePicker = true },
                enabled = false,
                readOnly = true,
                leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null) },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Location Field
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Ubicación (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                shape = RoundedCornerShape(12.dp)
            )

            // Description Field
            OutlinedTextField(
                value = description,
                onValueChange = { 
                    description = it 
                    if (it.isNotBlank()) descriptionError = false
                },
                label = { Text("Descripción *") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                isError = descriptionError,
                supportingText = { if (descriptionError) Text("La descripción es obligatoria") },
                shape = RoundedCornerShape(12.dp)
            )

            // Cost Field
            OutlinedTextField(
                value = cost,
                onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) cost = it },
                label = { Text("Coste estimado (€)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                leadingIcon = { Icon(Icons.Default.Payments, contentDescription = null) },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancelar")
                }
                Button(
                    onClick = {
                        var hasError = false
                        if (title.isBlank()) {
                            titleError = true
                            hasError = true
                        }
                        if (description.isBlank()) {
                            descriptionError = true
                            hasError = true
                        }

                        if (!hasError) {
                            val newItem = ItineraryItem(
                                id = UUID.randomUUID().toString(),
                                tripId = "", // Set by parent
                                dayId = "",  // Set by parent
                                title = title,
                                description = description,
                                date = LocalDate.now(), // Set by parent
                                time = selectedTime,
                                location = location.ifBlank { null },
                                cost = cost.toDoubleOrNull()
                            )
                            onAdd(newItem)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Añadir")
                }
            }
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedTime.hour,
            initialMinute = selectedTime.minute,
            is24Hour = true
        )
        
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditActivityBottomSheet(
    activity: ItineraryItem,
    onDismiss: () -> Unit,
    onSave: (ItineraryItem) -> Unit,
    sheetState: SheetState
) {
    var title by remember { mutableStateOf(activity.title) }
    var titleError by remember { mutableStateOf(false) }
    var location by remember { mutableStateOf(activity.location ?: "") }
    var description by remember { mutableStateOf(activity.description) }
    var descriptionError by remember { mutableStateOf(false) }
    var cost by remember { mutableStateOf(activity.cost?.toString() ?: "") }
    var selectedTime by remember { mutableStateOf(activity.time) }
    var showTimePicker by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Editar Actividad",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Title Field
            OutlinedTextField(
                value = title,
                onValueChange = { 
                    title = it
                    if (it.isNotBlank()) titleError = false
                },
                label = { Text("Título de la actividad *") },
                modifier = Modifier.fillMaxWidth(),
                isError = titleError,
                supportingText = { if (titleError) Text("El título es obligatorio") },
                shape = RoundedCornerShape(12.dp)
            )

            // Time Picker Trigger
            OutlinedTextField(
                value = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                onValueChange = {},
                label = { Text("Hora *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTimePicker = true },
                enabled = false,
                readOnly = true,
                leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null) },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Location Field
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Ubicación (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                shape = RoundedCornerShape(12.dp)
            )

            // Description Field
            OutlinedTextField(
                value = description,
                onValueChange = { 
                    description = it 
                    if (it.isNotBlank()) descriptionError = false
                },
                label = { Text("Descripción *") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                isError = descriptionError,
                supportingText = { if (descriptionError) Text("La descripción es obligatoria") },
                shape = RoundedCornerShape(12.dp)
            )

            // Cost Field
            OutlinedTextField(
                value = cost,
                onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) cost = it },
                label = { Text("Coste estimado (€)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                leadingIcon = { Icon(Icons.Default.Payments, contentDescription = null) },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancelar")
                }
                Button(
                    onClick = {
                        var hasError = false
                        if (title.isBlank()) {
                            titleError = true
                            hasError = true
                        }
                        if (description.isBlank()) {
                            descriptionError = true
                            hasError = true
                        }

                        if (!hasError) {
                            val updatedItem = activity.copy(
                                title = title,
                                description = description,
                                time = selectedTime,
                                location = location.ifBlank { null },
                                cost = cost.toDoubleOrNull()
                            )
                            onSave(updatedItem)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar cambios")
                }
            }
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedTime.hour,
            initialMinute = selectedTime.minute,
            is24Hour = true
        )
        
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}

// Helper to map mock days to dates
fun getLocalDateForDay(dayId: String): LocalDate {
    return when(dayId) {
        "day1" -> LocalDate.of(2025, 6, 15)
        "day2" -> LocalDate.of(2025, 6, 16)
        "day3" -> LocalDate.of(2025, 6, 17)
        "day4" -> LocalDate.of(2025, 6, 18)
        else -> LocalDate.now()
    }
}

@Composable
fun DayCarousel(
    days: List<TripDayInfo>,
    dayCounts: Map<String, Int>,
    selectedPageIndex: Int,
    onDayClick: (Int) -> Unit
) {
    val listState = rememberLazyListState()
    
    LaunchedEffect(selectedPageIndex) {
        listState.animateScrollToItem(selectedPageIndex)
    }

    LazyRow(
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f))
    ) {
        items(days.size) { index ->
            val day = days[index]
            val isSelected = index == selectedPageIndex
            val count = dayCounts[day.id] ?: day.activitiesCount
            
            DayChip(
                day = day,
                count = count,
                isSelected = isSelected,
                onClick = { onDayClick(index) }
            )
        }
    }
}

@Composable
fun DayChip(
    day: TripDayInfo,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(85.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
        tonalElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Día ${day.dayNumber}",
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = day.date,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$count act",
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(4.dp)
                        .background(MaterialTheme.colorScheme.onPrimary, CircleShape)
                )
            }
        }
    }
}

@Composable
fun DayContent(
    day: TripDayInfo,
    activities: List<ItineraryItem>,
    onEdit: (String) -> Unit,
    onDelete: (ItineraryItem) -> Unit,
    onAddFirst: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(
                    text = "Día ${day.dayNumber} • ${day.dayOfWeek}, ${day.date}",
                    style = MaterialTheme.typography.labelLarge,
                    color = DoradoAtenea,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = day.subtitle,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoLabel(Icons.Default.Schedule, "09:00 - 22:00")
                    InfoLabel(Icons.AutoMirrored.Filled.List, "${activities.size} actividades")
                    InfoLabel(Icons.Default.Payments, day.budget)
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
        }

        if (activities.isEmpty()) {
            item {
                EmptyActivitiesState(onAddFirst)
            }
        } else {
            items(activities.size) { index ->
                ActivityTimelineItem(
                    activity = activities[index],
                    isLast = index == activities.size - 1,
                    onEdit = { onEdit(activities[index].id) },
                    onDelete = { onDelete(activities[index]) }
                )
            }
        }
        
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun InfoLabel(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
    }
}

@Composable
fun ActivityTimelineItem(
    activity: ItineraryItem,
    isLast: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(48.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(DoradoAtenea, CircleShape)
                    .border(3.dp, DoradoAtenea.copy(alpha = 0.2f), CircleShape)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(130.dp)
                        .background(DoradoAtenea.copy(alpha = 0.3f))
                )
            }
        }

        Card(
            modifier = Modifier.weight(1f).padding(bottom = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = activity.time.format(DateTimeFormatter.ofPattern("HH:mm")),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold
                    )
                    
                    var showMenu by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Opciones", tint = Color.Gray)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        "Editar", 
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    ) 
                                },
                                onClick = { showMenu = false; onEdit() },
                                leadingIcon = { 
                                    Icon(
                                        Icons.Default.Edit, 
                                        contentDescription = null, 
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    ) 
                                }
                            )
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        "Eliminar", 
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    ) 
                                },
                                onClick = { showMenu = false; onDelete() },
                                leadingIcon = { 
                                    Icon(
                                        Icons.Default.Delete, 
                                        contentDescription = null, 
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.error
                                    ) 
                                },
                                colors = MenuDefaults.itemColors(
                                    textColor = MaterialTheme.colorScheme.error,
                                )
                            )
                        }
                    }
                }
                
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                
                if (!activity.description.isNullOrEmpty()) {
                    Text(
                        text = activity.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = TerracotaSuave)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(activity.location ?: "Sin ubicación", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    }
                    if (activity.cost != null) {
                        Text(
                            text = "€${activity.cost}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = DoradoAtenea
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyActivitiesState(onAddFirst: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.EventBusy,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No hay actividades planeadas",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAddFirst,
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Añadir primera actividad")
        }
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
fun DayItineraryScreenPreview() {
    Hermes_travelappTheme {
        DayItineraryScreen()
    }
}
