package com.example.hermes_travelapp.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hermes_travelapp.ui.theme.*
import kotlinx.coroutines.launch

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

data class TripActivity(
    val id: String,
    val time: String,
    val title: String,
    val description: String = "",
    val location: String,
    val cost: String = ""
)

// --- Mock Data ---

val mockDays = listOf(
    TripDayInfo("1", 1, "15 Jun", "Miér", "Llegada a Atenas", 0, "€45"),
    TripDayInfo("2", 2, "16 Jun", "Juev", "Acrópolis y Plaka", 5, "€120"),
    TripDayInfo("3", 3, "17 Jun", "Vier", "Museos de Atenas", 0, "€30"),
    TripDayInfo("4", 4, "18 Jun", "Sáb", "Viaje a Mykonos", 2, "€200")
)

val mockActivitiesDay2 = listOf(
    TripActivity("1", "09:00", "Desayuno frente al Partenón", "Vistas increíbles al amanecer", "Restaurante Acrópolis", "€15"),
    TripActivity("2", "11:00", "Visita guiada al museo", "Entrada reservada con antelación", "Museo de la Acrópolis", "€25"),
    TripActivity("3", "14:00", "Almuerzo tradicional", "Moussaka y ensalada griega", "Barrio de Plaka", "€20"),
    TripActivity("4", "17:00", "Paseo por el Ágora Antigua", "Exploración de las ruinas", "Atenas Centro", "€10"),
    TripActivity("5", "20:00", "Cena con música en vivo", "Taberna tradicional en Plaka", "Plaka", "€35")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayItineraryScreen(
    tripId: String = "1",
    dayId: String = "2",
    onBack: () -> Unit = {},
    onNavigateToAddActivity: () -> Unit = {},
    onNavigateToEditActivity: (activityId: String) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Dynamic state for activities per day
    val activitiesByDay = remember {
        mutableStateMapOf<String, SnapshotStateList<TripActivity>>().apply {
            mockDays.forEach { day ->
                val initialList = if (day.id == "2") mockActivitiesDay2 else emptyList()
                put(day.id, initialList.toMutableStateList())
            }
        }
    }

    // State for day navigation
    val initialPageIndex = mockDays.indexOfFirst { it.id == dayId }.coerceAtLeast(0)
    val pagerState = rememberPagerState(initialPage = initialPageIndex) { mockDays.size }
    
    // Deletion State
    var showDeleteDialog by remember { mutableStateOf(false) }
    var activityToDelete by remember { mutableStateOf<TripActivity?>(null) }
    var lastDeletedIndex by remember { mutableIntStateOf(-1) }

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
                onClick = onNavigateToAddActivity,
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
                selectedPageIndex = pagerState.currentPage,
                onDayClick = { index ->
                    scope.launch { pagerState.animateScrollToPage(index) }
                },
                activitiesByDay = activitiesByDay
            )

            // 2. MAIN CONTENT - SWIPEABLE DAYS
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.Top
            ) { pageIndex ->
                val day = mockDays[pageIndex]
                val activities = activitiesByDay[day.id] ?: remember { mutableStateListOf() }
                
                DayContent(
                    day = day,
                    activities = activities,
                    onEdit = onNavigateToEditActivity,
                    onDelete = { activity ->
                        activityToDelete = activity
                        showDeleteDialog = true
                    },
                    onAddFirst = onNavigateToAddActivity
                )
            }
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
                            val currentDayId = mockDays[pagerState.currentPage].id
                            val currentList = activitiesByDay[currentDayId]
                            val activity = activityToDelete
                            
                            if (currentList != null && activity != null) {
                                lastDeletedIndex = currentList.indexOf(activity)
                                currentList.remove(activity)
                                
                                showDeleteDialog = false
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Actividad eliminada",
                                        actionLabel = "Deshacer",
                                        duration = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        // Undo logic
                                        currentList.add(lastDeletedIndex.coerceIn(0, currentList.size), activity)
                                    }
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

@Composable
fun DayCarousel(
    days: List<TripDayInfo>,
    selectedPageIndex: Int,
    onDayClick: (Int) -> Unit,
    activitiesByDay: Map<String, List<TripActivity>>
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
            val currentActivitiesCount = activitiesByDay[day.id]?.size ?: 0
            
            DayChip(
                day = day.copy(activitiesCount = currentActivitiesCount),
                isSelected = isSelected,
                onClick = { onDayClick(index) }
            )
        }
    }
}

@Composable
fun DayChip(
    day: TripDayInfo,
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (day.activitiesCount == 0) {
                    Icon(
                        Icons.Default.Warning, 
                        contentDescription = null, 
                        modifier = Modifier.size(12.dp),
                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else TerracotaSuave
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                }
                Text(
                    text = "${day.activitiesCount} act",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
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
    activities: List<TripActivity>,
    onEdit: (String) -> Unit,
    onDelete: (TripActivity) -> Unit,
    onAddFirst: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 2. DAY HEADER SECTION
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
                    InfoLabel(Icons.Default.List, "${activities.size} actividades")
                    InfoLabel(Icons.Default.Payments, day.budget)
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
        }

        // 3. ACTIVITIES LIST - VERTICAL TIMELINE
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
        
        item { Spacer(modifier = Modifier.height(80.dp)) } // Padding for FAB
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
    activity: TripActivity,
    isLast: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Timeline Column
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
                        .height(130.dp) // Adjusted to match card height roughly
                        .background(DoradoAtenea.copy(alpha = 0.3f))
                )
            }
        }

        // Activity Card
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
                        text = activity.time,
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
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Editar") },
                                onClick = { showMenu = false; onEdit() },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp)) }
                            )
                            DropdownMenuItem(
                                text = { Text("Eliminar", color = MaterialTheme.colorScheme.error) },
                                onClick = { showMenu = false; onDelete() },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error) }
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
                
                if (activity.description.isNotEmpty()) {
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
                        Text(activity.location, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    }
                    if (activity.cost.isNotEmpty()) {
                        Text(
                            text = activity.cost,
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

// --- Previews ---

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
