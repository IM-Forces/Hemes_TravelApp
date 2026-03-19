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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hermes_travelapp.R
import com.example.hermes_travelapp.domain.TripDay
import com.example.hermes_travelapp.ui.theme.*
import com.example.hermes_travelapp.ui.viewmodels.TripDayViewModel
import com.example.hermes_travelapp.ui.viewmodels.TripViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

data class TripDayUI(
    val id: String,
    val date: String,
    val dayOfWeek: String,
    val dayNumber: Int,
    val location: String,
    val activitiesCount: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripOverviewScreen(
    tripId: String,
    tripViewModel: TripViewModel = viewModel(),
    tripDayViewModel: TripDayViewModel = viewModel(),
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
            location = domainDay.subtitle.ifBlank { stringResource(R.string.itinerary_no_activities) },
            activitiesCount = 0
        )
    }

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
                    duration = "",
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
                        onClick = { onDayClick(day.id) }
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.itinerary_add_day), fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun TripOverviewHeader(tripName: String, dates: String, duration: String, daysRemaining: Int, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(280.dp).background(MaterialTheme.colorScheme.surfaceVariant)) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)))))
        IconButton(onClick = onBack, modifier = Modifier.statusBarsPadding().padding(8.dp).align(Alignment.TopStart).background(Color.Black.copy(alpha = 0.3f), CircleShape)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back), tint = BlancoMarmol)
        }
        Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = tripName, style = MaterialTheme.typography.headlineMedium, color = BlancoMarmol, fontWeight = FontWeight.Bold)
                Surface(color = DoradoAtenea, shape = RoundedCornerShape(8.dp)) {
                    Text(text = stringResource(R.string.itinerary_days_remaining, daysRemaining), style = MaterialTheme.typography.labelMedium, color = Color.Black, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
            Text(text = "📅 $dates" + (if (duration.isNotBlank()) " • $duration" else ""), style = MaterialTheme.typography.bodyLarge, color = BlancoMarmol.copy(alpha = 0.8f))
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
                Text(text = "€$spent / €$total", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape), color = MaterialTheme.colorScheme.primary, trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = stringResource(R.string.detail_budget_total_pct, (progress * 100).toInt()), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
}

@Composable
fun TimelineDayItem(day: TripDayUI, isFirst: Boolean, isLast: Boolean, onClick: () -> Unit) {
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
                        Text(text = stringResource(R.string.itinerary_day, day.dayNumber), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "${day.dayOfWeek}, ${day.date}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = day.location, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                }
                Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            }
        }
    }
}
