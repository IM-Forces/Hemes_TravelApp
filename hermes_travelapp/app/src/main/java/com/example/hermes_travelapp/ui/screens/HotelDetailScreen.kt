package com.example.hermes_travelapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.hermes_travelapp.R
import com.example.hermes_travelapp.domain.model.Hotel
import com.example.hermes_travelapp.domain.model.HotelRoom
import com.example.hermes_travelapp.ui.theme.Hermes_travelappTheme
import com.example.hermes_travelapp.ui.viewmodels.HotelViewModel
import com.example.hermes_travelapp.ui.theme.DoradoAtenea

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailScreen(
    hotelId: String,
    tripId: String? = null,
    city: String,
    startDate: String,
    endDate: String,
    viewModel: HotelViewModel,
    onBack: () -> Unit
) {
    val searchResults by viewModel.availableHotels.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()
    
    val hotel = remember(searchResults, hotelId) {
        searchResults.find { it.id == hotelId }
    }

    val isReserving by viewModel.isReserving.collectAsState()
    val reservationSuccess by viewModel.reservationSuccess.collectAsState()
    val reservationError by viewModel.errorMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Handle Reservation Success
    if (reservationSuccess) {
        AlertDialog(
            onDismissRequest = { viewModel.resetReservationState() },
            title = { Text("Reserva confirmada", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("¡Tu reserva en ${hotel?.name} ha sido realizada con éxito!")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Fechas: $startDate - $endDate", style = MaterialTheme.typography.bodyMedium)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetReservationState()
                        onBack()
                    }
                ) {
                    Text("Aceptar")
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurface
        )
    }

    // Handle Reservation Error
    LaunchedEffect(reservationError) {
        reservationError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back),
                                tint = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = hotel?.name ?: stringResource(R.string.hotel_details_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondary,
                            maxLines = 1
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (hotel == null) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    Text(
                        text = error ?: "No se encontró el hotel",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                HotelDetailContent(
                    hotel = hotel,
                    isReserving = isReserving,
                    onReserveClick = { room ->
                        if (tripId != null) {
                            viewModel.createReservation(tripId, hotel, room.id)
                        } else {
                            viewModel.confirmReservation(
                                hotel = hotel,
                                roomId = room.id,
                                startDate = startDate,
                                endDate = endDate
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun HotelDetailContent(
    hotel: Hotel,
    isReserving: Boolean,
    onReserveClick: (HotelRoom) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Hero Image
        item {
            AsyncImage(
                model = hotel.imageUrl,
                contentDescription = hotel.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(24.dp)),
                contentScale = ContentScale.Crop
            )
        }

        // Info Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = hotel.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = hotel.rating.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = hotel.address,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Habitaciones disponibles",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Rooms List
        items(hotel.rooms) { room ->
            RoomItemCard(
                room = room,
                isReserving = isReserving,
                onReserveClick = { onReserveClick(room) }
            )
        }
    }
}

@Composable
fun RoomItemCard(
    room: HotelRoom,
    isReserving: Boolean,
    onReserveClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            if (room.images.isNotEmpty()) {
                AsyncImage(
                    model = room.images.first(),
                    contentDescription = room.roomType,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = ContentScale.Crop
                )
            }
            
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = room.roomType.uppercase(),
                            style = MaterialTheme.typography.labelLarge,
                            color = DoradoAtenea,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.hotel_price_per_night, room.price),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Button(
                        onClick = onReserveClick,
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isReserving
                    ) {
                        if (isReserving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Reservar")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HotelDetailScreenPreview() {
    val mockHotel = Hotel(
        id = "1",
        name = "Atenas Palace Luxury",
        address = "Plaza de la Constitución, 5, Atenas",
        rating = 5,
        imageUrl = "https://images.unsplash.com/photo-1566073771259-6a8506099945",
        rooms = listOf(
            HotelRoom("1", "Single", 80.0, listOf("https://images.unsplash.com/photo-1631049307264-da0ec9d70304")),
            HotelRoom("2", "Double", 120.0, listOf("https://images.unsplash.com/photo-1590490360182-c33d57733427")),
            HotelRoom("3", "Suite", 250.0, listOf("https://images.unsplash.com/photo-1582719478250-c89cae4dc85b"))
        )
    )
    
    Hermes_travelappTheme {
        HotelDetailContent(
            hotel = mockHotel,
            isReserving = false,
            onReserveClick = {}
        )
    }
}
