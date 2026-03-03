package com.example.hermes_travelapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hermes_travelapp.ui.theme.*

data class ItineraryEvent(
    val id: Int,
    val time: String,
    val title: String,
    val location: String,
    val description: String,
    val icon: ImageVector = Icons.Default.Event
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    tripName: String = "Grecia Clásica",
    onBack: () -> Unit = {}
) {
    val mockEvents = remember {
        mutableStateListOf(
            ItineraryEvent(1, "09:00 AM", "Visita al Partenón", "Acrópolis de Atenas", "Una visita guiada por el templo más importante de la antigua Grecia, dedicado a la diosa Atenea."),
            ItineraryEvent(2, "01:30 PM", "Almuerzo Tradicional", "Plaka District", "Disfruta de la moussaka y el souvlaki en uno de los barrios más pintorescos de Atenas."),
            ItineraryEvent(3, "04:00 PM", "Museo de la Acrópolis", "Dionysiou Areopagitou", "Explora las obras maestras de la civilización griega en este museo arqueológico de vanguardia.")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tripName, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulEgeo,
                    titleContentColor = BlancoMarmol
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Lógica para añadir evento */ },
                containerColor = DoradoAtenea,
                contentColor = NegroCeramica,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Event")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                TripSummaryHeader()
            }

            item {
                Text(
                    text = "Itinerary",
                    style = MaterialTheme.typography.titleMedium,
                    color = DoradoAtenea,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(mockEvents, key = { it.id }) { event ->
                ItineraryItem(
                    event = event,
                    onEdit = { /* Lógica de edición */ },
                    onDelete = { mockEvents.remove(event) }
                )
            }
        }
    }
}

@Composable
fun TripSummaryHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏛️", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Total Budget: €1,200",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "15 Jun - 22 Jun",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun ItineraryItem(
    event: ItineraryEvent,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Timeline vertical line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(DoradoAtenea, CircleShape)
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .weight(1f)
                    .background(DoradoAtenea.copy(alpha = 0.3f))
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Event Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .animateContentSize()
                .clickable { expanded = !expanded },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = event.time,
                            style = MaterialTheme.typography.labelSmall,
                            color = DoradoAtenea,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    if (expanded) {
                        Row {
                            IconButton(onClick = onEdit) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = DoradoAtenea, modifier = Modifier.size(20.dp))
                            }
                            IconButton(onClick = onDelete) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TerracotaSuave, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = AzulEgeo,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                AnimatedVisibility(visible = expanded) {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = event.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Trip Detail Light")
@Composable
fun TripDetailScreenPreviewLight() {
    Hermes_travelappTheme(darkTheme = false) {
        TripDetailScreen()
    }
}

@Preview(showBackground = true, name = "Trip Detail Dark")
@Composable
fun TripDetailScreenPreviewDark() {
    Hermes_travelappTheme(darkTheme = true) {
        TripDetailScreen()
    }
}
