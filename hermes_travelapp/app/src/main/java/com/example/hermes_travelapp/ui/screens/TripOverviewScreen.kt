package com.example.hermes_travelapp.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hermes_travelapp.R
import com.example.hermes_travelapp.domain.Trip
import com.example.hermes_travelapp.ui.theme.*
import com.example.hermes_travelapp.ui.viewmodels.TripDayViewModel
import com.example.hermes_travelapp.ui.viewmodels.TripViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

data class TripDayUI(
    val id: String,
    val date: String,
    val dayOfWeek: String,
    val dayNumber: Int,
    val activitiesCount: Int
)

@Composable
fun TripOverviewScreen(
    tripId: String,
    tripViewModel: TripViewModel,
    tripDayViewModel: TripDayViewModel,
    onDayClick: (dayId: String) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val allTrips by tripViewModel.trips.collectAsState()
    val trip = allTrips.find { it.id == tripId }
    val realDays by tripDayViewModel.tripDays.collectAsState()
    
    LaunchedEffect(tripId) {
        tripDayViewModel.loadDaysForTrip(tripId)
    }

    if (trip == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.error_trip_not_found))
        }
        return
    }

    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale.getDefault())
    val dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEE", Locale.getDefault())

    val uiDays = realDays.map { domainDay ->
        TripDayUI(
            id = domainDay.id,
            date = domainDay.date.format(dateFormatter),
            dayOfWeek = domainDay.date.format(dayOfWeekFormatter).replaceFirstChar { it.uppercase() },
            dayNumber = domainDay.dayNumber,
            activitiesCount = 0
        )
    }

    // Calcular si se pueden añadir más días basándose en las fechas del viaje
    // Se recalcula automáticamente cada vez que cambia uiDays.size (ej. al borrar)
    val canAddDay = remember(trip.startDate, trip.endDate, uiDays.size) {
        try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val start = LocalDate.parse(trip.startDate, formatter)
            val end = LocalDate.parse(trip.endDate, formatter)
            val maxDaysPossible = ChronoUnit.DAYS.between(start, end).toInt() + 1
            uiDays.size < maxDaysPossible
        } catch (e: Exception) {
            true
        }
    }

    TripOverviewContent(
        trip = trip,
        uiDays = uiDays,
        canAddDay = canAddDay,
        onAddDay = {
            if (canAddDay) {
                tripDayViewModel.addDay(trip.id) { _ ->
                    // No actualizamos la fecha fin para respetar el límite definido originalmente
                }
            }
        },
        onDeleteDay = { dayId ->
            tripDayViewModel.deleteDay(dayId, trip.id, trip.startDate) { newEndDate ->
                // Al borrar un día, opcionalmente podrías actualizar la fecha fin del viaje, 
                // pero si quieres que el rango original mande, canAddDay se habilitará solo.
            }
        },
        onDayClick = onDayClick,
        onBack = onBack
    )
}

@Composable
fun TripOverviewContent(
    trip: Trip,
    uiDays: List<TripDayUI>,
    canAddDay: Boolean = true,
    onAddDay: () -> Unit = {},
    onDeleteDay: (dayId: String) -> Unit = {},
    onDayClick: (dayId: String) -> Unit = {},
    onBack: () -> Unit = {}
){
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            item {
                TripOverviewHeader(
                    tripName = "${trip.emoji} ${trip.title}",
                    dates = "${trip.startDate} - ${trip.endDate}",
                    daysRemaining = trip.daysRemaining,
                    onBack = onBack
                )
            }

            item {
                Box(modifier = Modifier.padding(16.dp)) {
                    BudgetOverviewCard(spent = trip.spent, total = trip.budget)
                }
            }

            item {
                Text(
                    text = "📅 " + stringResource(R.string.itinerary_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            if (uiDays.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.itinerary_no_days), color = Color.Gray)
                    }
                }
            } else {
                itemsIndexed(uiDays) { index, day ->
                    TimelineDayItem(
                        day = day,
                        isFirst = index == 0,
                        isLast = index == uiDays.size - 1,
                        onClick = { onDayClick(day.id) },
                        onDelete = { onDeleteDay(day.id) }
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedButton(
                        onClick = { onAddDay() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = canAddDay,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (canAddDay) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = if (canAddDay) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.itinerary_add_day),
                            fontWeight = FontWeight.SemiBold,
                            color = if (canAddDay) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun TripOverviewHeader(tripName: String, dates: String, daysRemaining: Int, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(280.dp).background(MaterialTheme.colorScheme.surfaceVariant)) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)))))
        IconButton(onClick = onBack, modifier = Modifier.statusBarsPadding().padding(8.dp).align(Alignment.TopStart).background(Color.Black.copy(alpha = 0.3f), CircleShape)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back), tint = Color.White)
        }
        Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = tripName, style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
                Surface(color = DoradoAtenea, shape = RoundedCornerShape(8.dp)) {
                    Text(text = stringResource(R.string.itinerary_days_remaining, daysRemaining), style = MaterialTheme.typography.labelMedium, color = Color.Black, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
            Text(text = "📅 $dates", style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun BudgetOverviewCard(spent: Int, total: Int) {
    val progress = if (total > 0) spent.toFloat() / total.toFloat() else 0f
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(R.string.detail_budget), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = stringResource(R.string.itinerary_budget_total, spent, total), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape), color = MaterialTheme.colorScheme.primary, trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = stringResource(R.string.detail_budget_total_pct, (progress * 100).toInt()), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
}

@Composable
fun TimelineDayItem(day: TripDayUI, isFirst: Boolean, isLast: Boolean, onClick: () -> Unit, onDelete: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.Top) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(40.dp)) {
            Box(modifier = Modifier.width(2.dp).height(24.dp).background(if (isFirst) Color.Transparent else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)))
            Box(modifier = Modifier.size(16.dp).background(MaterialTheme.colorScheme.primary, CircleShape).border(4.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape))
            Box(modifier = Modifier.width(2.dp).weight(1f, fill = false).height(80.dp).background(if (isLast) Color.Transparent else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Card(modifier = Modifier.weight(1f).padding(vertical = 8.dp).clickable { onClick() }, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)), border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))) {
            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.itinerary_day, day.dayNumber),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${day.dayOfWeek}, ${day.date}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                Row {
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TripOverviewPreview() {
    val sampleTrip = Trip(
        title = "Atenas y Santorini",
        startDate = "15/07/2025",
        endDate = "22/07/2025",
        description = "Un viaje por la cuna de la civilización.",
        emoji = "🏛️",
        budget = 2500,
        spent = 1200,
        daysRemaining = 12
    )
    
    val sampleDays = listOf(
        TripDayUI("1", "15 Jul", "Lun", 1, 2),
        TripDayUI("2", "16 Jul", "Mar", 2, 4),
        TripDayUI("3", "17 Jul", "Mie", 3, 3)
    )

    Hermes_travelappTheme {
        TripOverviewContent(
            trip = sampleTrip,
            uiDays = sampleDays
        )
    }
}
