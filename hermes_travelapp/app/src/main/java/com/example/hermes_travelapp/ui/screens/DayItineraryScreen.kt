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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hermes_travelapp.R
import com.example.hermes_travelapp.domain.ItineraryItem
import com.example.hermes_travelapp.ui.theme.*
import com.example.hermes_travelapp.ui.viewmodels.ActivityViewModel
import com.example.hermes_travelapp.ui.viewmodels.TripDayViewModel
import com.example.hermes_travelapp.ui.viewmodels.TripViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

data class TripDayInfo(
    val id: String,
    val dayNumber: Int,
    val date: String,
    val fullDate: LocalDate,
    val dayOfWeek: String,
    val activitiesCount: Int,
    val budget: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayItineraryScreen(
    tripId: String = "grecia_trip",
    dayId: String = "day1",
    tripViewModel: TripViewModel,
    activityViewModel: ActivityViewModel = viewModel(),
    tripDayViewModel: TripDayViewModel,
    onBack: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    
    val allTrips by tripViewModel.trips.collectAsState()
    val trip = allTrips.find { it.id == tripId }
    
    val domainDays by tripDayViewModel.tripDays.collectAsState()
    val activities by activityViewModel.activities.collectAsState()
    val dayCounts by activityViewModel.dayCounts.collectAsState()

    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale.getDefault())
    val dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEE", Locale.getDefault())
    
    val uiDays = remember(domainDays) {
        domainDays.map { domainDay ->
            TripDayInfo(
                id = domainDay.id,
                dayNumber = domainDay.dayNumber,
                date = domainDay.date.format(dateFormatter),
                fullDate = domainDay.date,
                dayOfWeek = domainDay.date.format(dayOfWeekFormatter).replaceFirstChar { it.uppercase() },
                activitiesCount = 0,
                budget = "€0"
            )
        }
    }

    LaunchedEffect(tripId) {
        tripDayViewModel.loadDaysForTrip(tripId)
    }

    if (uiDays.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val initialPageIndex = remember(uiDays, dayId) {
        val index = uiDays.indexOfFirst { it.id == dayId }
        if (index >= 0) index else 0
    }
    
    val pagerState = rememberPagerState(initialPage = initialPageIndex) { uiDays.size }
    
    var showAddSheet by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var activityToEdit by remember { mutableStateOf<ItineraryItem?>(null) }
    var activityToDelete by remember { mutableStateOf<ItineraryItem?>(null) }
    
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val editSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(tripId, uiDays) {
        if (uiDays.isNotEmpty()) {
            activityViewModel.loadAllDayCounts(tripId, uiDays.map { it.id })
        }
    }

    LaunchedEffect(pagerState.currentPage, uiDays) {
        if (uiDays.isNotEmpty()) {
            val currentDayId = uiDays[pagerState.currentPage].id
            activityViewModel.loadActivitiesForDay(tripId, currentDayId)
        }
    }

    val currentDayBudget = remember(activities) {
        val total = activities.sumOf { it.cost ?: 0.0 }
        "€${total.toInt()}"
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(trip?.title ?: stringResource(R.string.itinerary_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
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
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.itinerary_add_activity_cd), modifier = Modifier.size(32.dp))
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            DayCarousel(
                days = uiDays,
                dayCounts = dayCounts,
                selectedPageIndex = pagerState.currentPage,
                onDayClick = { index ->
                    scope.launch { pagerState.animateScrollToPage(index) }
                }
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.Top
            ) { pageIndex ->
                val day = uiDays[pageIndex]
                
                DayContent(
                    day = day.copy(budget = if(pageIndex == pagerState.currentPage) currentDayBudget else day.budget),
                    activities = if(pageIndex == pagerState.currentPage) activities else emptyList(),
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

        if (showAddSheet) {
            AddActivityBottomSheet(
                onDismiss = { showAddSheet = false },
                onAdd = { newItem ->
                    val currentDay = uiDays[pagerState.currentPage]
                    val activityWithContext = newItem.copy(
                        tripId = tripId,
                        dayId = currentDay.id,
                        date = currentDay.fullDate
                    )
                    activityViewModel.addActivity(activityWithContext)
                    showAddSheet = false
                    val msg = context.getString(R.string.itinerary_activity_added, newItem.title)
                    scope.launch { snackbarHostState.showSnackbar(msg) }
                },
                sheetState = sheetState
            )
        }

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
                    val msg = context.getString(R.string.itinerary_activity_updated, updatedItem.title)
                    scope.launch { snackbarHostState.showSnackbar(msg) }
                },
                sheetState = editSheetState
            )
        }

        if (showDeleteDialog && activityToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text(stringResource(R.string.itinerary_delete_activity)) },
                text = { Text(stringResource(R.string.itinerary_delete_msg, activityToDelete?.title ?: "")) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val activity = activityToDelete
                            if (activity != null) {
                                activityViewModel.deleteActivity(activity.id, tripId, activity.dayId)
                                showDeleteDialog = false
                                scope.launch { snackbarHostState.showSnackbar(context.getString(R.string.itinerary_activity_deleted)) }
                            }
                        }
                    ) {
                        Text(stringResource(R.string.itinerary_delete_confirm), color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
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
        if (days.isNotEmpty()) {
            listState.animateScrollToItem(selectedPageIndex)
        }
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
            val count = dayCounts[day.id] ?: 0
            
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
                text = stringResource(R.string.itinerary_day, day.dayNumber),
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
                text = stringResource(R.string.itinerary_activities, count),
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
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
                    text = stringResource(R.string.itinerary_day, day.dayNumber) + " • ${day.dayOfWeek}, ${day.date}",
                    style = MaterialTheme.typography.labelLarge,
                    color = DoradoAtenea,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoLabel(Icons.Default.Schedule, "09:00 - 22:00")
                    InfoLabel(Icons.AutoMirrored.Filled.List, stringResource(R.string.itinerary_activities, activities.size))
                    InfoLabel(Icons.Default.Payments, day.budget)
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
        }

        if (activities.isEmpty()) {
            item { EmptyActivitiesState(onAddFirst) }
        } else {
            items(activities) { activity ->
                ActivityTimelineItem(
                    activity = activity,
                    isLast = activity == activities.last(),
                    onEdit = { onEdit(activity.id) },
                    onDelete = { onDelete(activity) }
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
            Box(modifier = Modifier.size(12.dp).background(DoradoAtenea, CircleShape).border(3.dp, DoradoAtenea.copy(alpha = 0.2f), CircleShape))
            if (!isLast) {
                Box(modifier = Modifier.width(2.dp).height(130.dp).background(DoradoAtenea.copy(alpha = 0.3f)))
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
                            Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.itinerary_options), tint = Color.Gray)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.edit)) },
                                onClick = { showMenu = false; onEdit() },
                                leadingIcon = { Icon(Icons.Default.Edit, null, tint = MaterialTheme.colorScheme.primary) }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.delete)) },
                                onClick = { showMenu = false; onDelete() },
                                leadingIcon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) },
                                colors = MenuDefaults.itemColors(textColor = MaterialTheme.colorScheme.error)
                            )
                        }
                    }
                }
                
                Text(text = activity.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 4.dp))
                
                if (activity.description.isNotBlank()) {
                    Text(text = activity.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), maxLines = 2)
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(14.dp), tint = TerracotaSuave)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(activity.location ?: stringResource(R.string.itinerary_no_location), style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    }
                    if (activity.cost != null) {
                        Text(text = "€${activity.cost.toInt()}", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = DoradoAtenea)
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyActivitiesState(onAddFirst: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.EventBusy, null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = stringResource(R.string.itinerary_no_activities), style = MaterialTheme.typography.titleMedium, color = Color.Gray, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onAddFirst, shape = RoundedCornerShape(12.dp)) {
            Icon(Icons.Default.Add, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.itinerary_add_first_activity))
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
            Text(stringResource(R.string.itinerary_new_activity), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(
                value = title,
                onValueChange = { title = it; if (it.isNotBlank()) titleError = false },
                label = { Text(stringResource(R.string.itinerary_activity_title)) },
                modifier = Modifier.fillMaxWidth(),
                isError = titleError,
                supportingText = { if (titleError) Text(stringResource(R.string.itinerary_activity_title_err)) },
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                onValueChange = {},
                label = { Text(stringResource(R.string.itinerary_activity_time)) },
                modifier = Modifier.fillMaxWidth().clickable { showTimePicker = true },
                enabled = false,
                readOnly = true,
                leadingIcon = { Icon(Icons.Default.AccessTime, null) },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text(stringResource(R.string.itinerary_activity_location)) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it; if (it.isNotBlank()) descriptionError = false },
                label = { Text(stringResource(R.string.itinerary_activity_desc)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                isError = descriptionError,
                supportingText = { if (descriptionError) Text(stringResource(R.string.itinerary_activity_desc_err)) },
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = cost,
                onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) cost = it },
                label = { Text(stringResource(R.string.itinerary_activity_cost)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                leadingIcon = { Icon(Icons.Default.Payments, null) },
                shape = RoundedCornerShape(12.dp)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                    Text(stringResource(R.string.cancel))
                }
                Button(
                    onClick = {
                        var hasError = false
                        if (title.isBlank()) { titleError = true; hasError = true }
                        if (description.isBlank()) { descriptionError = true; hasError = true }
                        if (!hasError) {
                            onAdd(ItineraryItem(UUID.randomUUID().toString(), "", "", title, description, LocalDate.now(), selectedTime, location.ifBlank { null }, cost.toDoubleOrNull()))
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.add))
                }
            }
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(initialHour = selectedTime.hour, initialMinute = selectedTime.minute, is24Hour = true)
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = { TextButton(onClick = { selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute); showTimePicker = false }) { Text(stringResource(R.string.ok)) } },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text(stringResource(R.string.cancel)) } },
            text = { TimePicker(state = timePickerState) }
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
            Text(stringResource(R.string.itinerary_edit_activity), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(
                value = title,
                onValueChange = { title = it; if (it.isNotBlank()) titleError = false },
                label = { Text(stringResource(R.string.itinerary_activity_title)) },
                modifier = Modifier.fillMaxWidth(),
                isError = titleError,
                supportingText = { if (titleError) Text(stringResource(R.string.itinerary_activity_title_err)) },
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                onValueChange = {},
                label = { Text(stringResource(R.string.itinerary_activity_time)) },
                modifier = Modifier.fillMaxWidth().clickable { showTimePicker = true },
                enabled = false,
                readOnly = true,
                leadingIcon = { Icon(Icons.Default.AccessTime, null) },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text(stringResource(R.string.itinerary_activity_location)) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it; if (it.isNotBlank()) descriptionError = false },
                label = { Text(stringResource(R.string.itinerary_activity_desc)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                isError = descriptionError,
                supportingText = { if (descriptionError) Text(stringResource(R.string.itinerary_activity_desc_err)) },
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = cost,
                onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) cost = it },
                label = { Text(stringResource(R.string.itinerary_activity_cost)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                leadingIcon = { Icon(Icons.Default.Payments, null) },
                shape = RoundedCornerShape(12.dp)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                    Text(stringResource(R.string.cancel))
                }
                Button(
                    onClick = {
                        var hasError = false
                        if (title.isBlank()) { titleError = true; hasError = true }
                        if (description.isBlank()) { descriptionError = true; hasError = true }
                        if (!hasError) {
                            onSave(activity.copy(title = title, description = description, time = selectedTime, location = location.ifBlank { null }, cost = cost.toDoubleOrNull()))
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Save, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.save))
                }
            }
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(initialHour = selectedTime.hour, initialMinute = selectedTime.minute, is24Hour = true)
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = { TextButton(onClick = { selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute); showTimePicker = false }) { Text(stringResource(R.string.ok)) } },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text(stringResource(R.string.cancel)) } },
            text = { TimePicker(state = timePickerState) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DayItineraryPreview() {
    val sampleDays = listOf(
        TripDayInfo("1", 1, "20 May", LocalDate.now(), "Lun", 2, "€35"),
        TripDayInfo("2", 2, "21 May", LocalDate.now().plusDays(1), "Mar", 0, "€0"),
        TripDayInfo("3", 3, "22 May", LocalDate.now().plusDays(2), "Mié", 0, "€0")
    )
    val sampleActivities = listOf(
        ItineraryItem(
            id = "1",
            tripId = "trip1",
            dayId = "1",
            title = "Acrópolis de Atenas",
            description = "Visita al Partenón y museos antiguos.",
            date = LocalDate.now(),
            time = LocalTime.of(9, 0),
            location = "Atenas, Grecia",
            cost = 20.0
        ),
        ItineraryItem(
            id = "2",
            tripId = "trip1",
            dayId = "1",
            title = "Almuerzo en Plaka",
            description = "Comida tradicional en el barrio histórico.",
            date = LocalDate.now(),
            time = LocalTime.of(13, 30),
            location = "Plaka",
            cost = 15.0
        )
    )

    Hermes_travelappTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Viaje a Grecia", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                DayCarousel(
                    days = sampleDays,
                    dayCounts = mapOf("1" to 2, "2" to 0, "3" to 0),
                    selectedPageIndex = 0,
                    onDayClick = {}
                )
                DayContent(
                    day = sampleDays[0],
                    activities = sampleActivities,
                    onEdit = {},
                    onDelete = {},
                    onAddFirst = {}
                )
            }
        }
    }
}
