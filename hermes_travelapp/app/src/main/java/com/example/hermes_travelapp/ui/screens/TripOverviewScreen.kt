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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hermes_travelapp.ui.theme.*

/**
 * Data class representing a day within a trip itinerary.
 */
data class TripDay(
    val id: String,
    val date: String,
    val dayOfWeek: String,
    val dayNumber: Int,
    val location: String,
    val activitiesCount: Int
)

/**
 * Mock data for TripOverviewScreen preview and development.
 */
val mockTripDays = listOf(
    TripDay("1", "15 Jun", "Miér", 1, "Llegada a Atenas", 3),
    TripDay("2", "16 Jun", "Juev", 2, "Acrópolis y Plaka", 4),
    TripDay("3", "17 Jun", "Vier", 3, "Museos de Atenas", 2),
    TripDay("4", "18 Jun", "Sáb", 4, "Viaje a Mykonos", 3),
    TripDay("5", "19 Jun", "Dom", 5, "Playas de Mykonos", 2),
    TripDay("6", "20 Jun", "Lun", 6, "Puesta de sol en Little Venice", 3),
    TripDay("7", "21 Jun", "Mar", 7, "Regreso a Atenas", 2),
    TripDay("8", "22 Jun", "Miér", 8, "Vuelo de vuelta", 1),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripOverviewScreen(
    tripId: String = "1",
    tripName: String = "Grecia Clásica",
    emoji: String = "🏛️",
    dates: String = "15 Jun - 22 Jun",
    duration: String = "8 días",
    daysRemaining: Int = 12,
    onDayClick: (dayId: String) -> Unit = {},
    onBack: () -> Unit = {}
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            // 1. Header Section with Hero Image Placeholder
            item {
                TripOverviewHeader(
                    tripName = "$emoji $tripName",
                    dates = dates,
                    duration = duration,
                    daysRemaining = daysRemaining,
                    onBack = onBack
                )
            }

            // 2. Budget Overview Card
            item {
                Box(modifier = Modifier.padding(16.dp)) {
                    BudgetOverviewCard(spent = 450, total = 1200)
                }
            }

            // 3. Timeline Section Title
            item {
                Text(
                    text = "📅 Itinerario del viaje",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // 4. Vertical Timeline with Days
            itemsIndexed(mockTripDays) { index, day ->
                TimelineDayItem(
                    day = day,
                    isFirst = index == 0,
                    isLast = index == mockTripDays.size - 1,
                    onClick = { onDayClick(day.id) }
                )
            }

            // 5. Add Day Button
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedButton(
                        onClick = { /* Add day logic */ },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Añadir día", fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun TripOverviewHeader(
    tripName: String,
    dates: String,
    duration: String,
    daysRemaining: Int,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        // Hero Image Placeholder Icon
        Icon(
            imageVector = Icons.Default.Image,
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.Center),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
        )

        // Dark gradient for text legibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                    )
                )
        )

        // Back Button with semi-transparent background
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .statusBarsPadding()
                .padding(8.dp)
                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = BlancoMarmol)
        }

        // Trip Details at the bottom of the banner
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = tripName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = BlancoMarmol,
                    fontWeight = FontWeight.Bold
                )
                
                // Days Remaining Badge using custom DoradoAtenea color
                Surface(
                    color = DoradoAtenea,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Faltan $daysRemaining días",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "📅 $dates • $duration",
                style = MaterialTheme.typography.bodyLarge,
                color = BlancoMarmol.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun BudgetOverviewCard(spent: Int, total: Int) {
    val progress = spent.toFloat() / total.toFloat()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Presupuesto",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "€$spent / €$total",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${(progress * 100).toInt()}% del presupuesto total",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun TimelineDayItem(
    day: TripDay,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Vertical Timeline Connector
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            // Top line (hidden for first item)
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(24.dp)
                    .background(if (isFirst) Color.Transparent else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            )
            // Timeline Node (Dot)
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .border(4.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape)
            )
            // Bottom line (hidden for last item)
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .weight(1f, fill = false)
                    .height(80.dp)
                    .background(if (isLast) Color.Transparent else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Clickable Day Card
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp)
                .clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Día ${day.dayNumber}",
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
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = day.location,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EventNote,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${day.activitiesCount} actividades",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Ver detalle",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Modo Claro")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Modo Oscuro"
)
@Composable
fun TripOverviewScreenPreview() {
    Hermes_travelappTheme {
        TripOverviewScreen()
    }
}
