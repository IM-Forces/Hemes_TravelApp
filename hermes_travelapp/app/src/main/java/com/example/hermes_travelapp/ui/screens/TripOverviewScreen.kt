package com.example.hermes_travelapp.ui.screens

import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.hermes_travelapp.R
import com.example.hermes_travelapp.domain.model.Trip
import com.example.hermes_travelapp.ui.theme.*
import com.example.hermes_travelapp.ui.viewmodels.TripDayViewModel
import com.example.hermes_travelapp.ui.viewmodels.TripViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import java.util.UUID

data class TripDayUI(
    val id: String,
    val date: String,
    val dayOfWeek: String,
    val dayNumber: Int,
    val activitiesCount: Int,
    val photos: List<String> = emptyList()
)

@Composable
fun TripOverviewScreen(
    tripId: String,
    tripViewModel: TripViewModel,
    tripDayViewModel: TripDayViewModel,
    onDayClick: (dayId: String) -> Unit = {},
    onBack: () -> Unit = {}
) {
    Log.d("Navigation", "TripDetailScreen composed, tripId: $tripId")
    
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
    val context = LocalContext.current

    var targetDayIdForPhotos by remember { mutableStateOf<String?>(null) }
    var showPhotoOptions by remember { mutableStateOf(false) }
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }
    
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        targetDayIdForPhotos?.let { dayId ->
            if (uris.isNotEmpty()) {
                tripDayViewModel.addPhotosToDay(dayId, uris)
            }
        }
        targetDayIdForPhotos = null
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            targetDayIdForPhotos?.let { dayId ->
                tempPhotoUri?.let { uri ->
                    tripDayViewModel.addPhotosToDay(dayId, listOf(uri))
                }
            }
        }
        targetDayIdForPhotos = null
        tempPhotoUri = null
    }

    fun createTempPictureUri(): Uri {
        val tempFile = File(context.cacheDir, "temp_photo_${UUID.randomUUID()}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        )
    }

    val uiDays = realDays.map { domainDay ->
        TripDayUI(
            id = domainDay.id,
            date = domainDay.date.format(dateFormatter),
            dayOfWeek = domainDay.date.format(dayOfWeekFormatter).replaceFirstChar { it.uppercase() },
            dayNumber = domainDay.dayNumber,
            activitiesCount = 0,
            photos = domainDay.photos
        )
    }

    TripOverviewContent(
        trip = trip,
        uiDays = uiDays,
        onAddDay = {
            tripDayViewModel.addDay(trip.id) { newEndDate ->
                tripViewModel.updateTripEndDate(trip.id, newEndDate)
            }
        },
        onDeleteDay = { dayId ->
            tripDayViewModel.deleteDay(dayId, trip.id, trip.startDate) { newEndDate ->
                tripViewModel.updateTripEndDate(trip.id, newEndDate)
            }
        },
        onAddPhotoToDay = { dayId ->
            targetDayIdForPhotos = dayId
            showPhotoOptions = true
        },
        onDeletePhoto = { dayId, photoUrl ->
            tripDayViewModel.deletePhotoFromDay(dayId, photoUrl)
        },
        onSetAsCover = { photoUrl ->
            tripViewModel.setTripCoverPhoto(trip.id, photoUrl)
        },
        onDayClick = onDayClick,
        onBack = onBack
    )

    if (showPhotoOptions) {
        AlertDialog(
            onDismissRequest = { showPhotoOptions = false },
            title = { Text(stringResource(R.string.photo_option_title)) },
            text = { Text(stringResource(R.string.photo_option_desc)) },
            confirmButton = {
                TextButton(onClick = {
                    showPhotoOptions = false
                    val uri = createTempPictureUri()
                    tempPhotoUri = uri
                    cameraLauncher.launch(uri)
                }) {
                    Text(stringResource(R.string.photo_option_camera))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPhotoOptions = false
                    photoPickerLauncher.launch("image/*")
                }) {
                    Text(stringResource(R.string.photo_option_gallery))
                }
            }
        )
    }
}

@Composable
fun TripOverviewContent(
    trip: Trip,
    uiDays: List<TripDayUI>,
    onAddDay: () -> Unit = {},
    onDeleteDay: (dayId: String) -> Unit = {},
    onAddPhotoToDay: (dayId: String) -> Unit = {},
    onDeletePhoto: (String, String) -> Unit = { _, _ -> },
    onDayClick: (dayId: String) -> Unit = {},
    onSetAsCover: (String) -> Unit = {},
    onBack: () -> Unit = {}
){
    var selectedPhotoInfo by remember { mutableStateOf<Pair<String, Int>?>(null) } // DayId to PhotoIndex

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
                    coverPhotoUrl = trip.coverPhotoUrl,
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
                        tripCoverPhotoUrl = trip.coverPhotoUrl,
                        onClick = { onDayClick(day.id) },
                        onDelete = { onDeleteDay(day.id) },
                        onAddPhoto = { onAddPhotoToDay(day.id) },
                        onPhotoClick = { photoUrl -> 
                            val photoIndex = day.photos.indexOf(photoUrl)
                            selectedPhotoInfo = day.id to photoIndex
                        }
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
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.itinerary_add_day),
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    selectedPhotoInfo?.let { (dayId, initialIndex) ->
        val day = uiDays.find { it.id == dayId }
        if (day != null) {
            PhotoLightbox(
                photos = day.photos,
                initialIndex = initialIndex,
                currentCoverUrl = trip.coverPhotoUrl,
                onDismiss = { selectedPhotoInfo = null },
                onSetAsCover = { onSetAsCover(it) },
                onDeletePhoto = { photoUrl ->
                    onDeletePhoto(dayId, photoUrl)
                }
            )
        }
    }
}

@Composable
fun TripOverviewHeader(
    tripName: String,
    dates: String,
    daysRemaining: Int,
    coverPhotoUrl: String? = null,
    onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth().height(280.dp).background(MaterialTheme.colorScheme.surfaceVariant)) {
        if (coverPhotoUrl != null) {
            AsyncImage(
                model = coverPhotoUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
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
fun TimelineDayItem(
    day: TripDayUI,
    isFirst: Boolean,
    isLast: Boolean,
    tripCoverPhotoUrl: String? = null,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onAddPhoto: () -> Unit = {},
    onPhotoClick: (String) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(IntrinsicSize.Min), // Ahora funciona porque no hay LazyRow dentro
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(40.dp)
                .fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(24.dp)
                    .background(if (isFirst) Color.Transparent else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            )
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .border(4.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape)
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .weight(1f)
                    .background(if (isLast) Color.Transparent else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp)
                .clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onAddPhoto,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = stringResource(R.string.cd_add_photo),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
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

                if (day.photos.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        day.photos.forEach { photoUrl ->
                            val isCover = photoUrl == tripCoverPhotoUrl
                            AsyncImage(
                                model = photoUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .then(
                                        if (isCover) Modifier.border(2.dp, DoradoAtenea, RoundedCornerShape(12.dp))
                                        else Modifier
                                    )
                                    .clickable { onPhotoClick(photoUrl) },
                                contentScale = ContentScale.Crop,
                                placeholder = ColorPainter(Color.LightGray.copy(alpha = 0.3f))
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoLightbox(
    photos: List<String>,
    initialIndex: Int,
    currentCoverUrl: String? = null,
    onDismiss: () -> Unit,
    onSetAsCover: (String) -> Unit,
    onDeletePhoto: (String) -> Unit
) {
    val pagerState = rememberPagerState(initialPage = initialIndex) { photos.size }
    // No longer using scope explicitly for now, will remove

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                pageSpacing = 16.dp
            ) { page ->
                AsyncImage(
                    model = photos[page],
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                    placeholder = ColorPainter(Color.DarkGray)
                )
            }

            // Toolbar superior
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.cd_close),
                        tint = Color.White
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = {
                            if (photos.isNotEmpty()) {
                                val currentPhoto = photos[pagerState.currentPage]
                                onDeletePhoto(currentPhoto)
                                if (photos.size <= 1) {
                                    onDismiss()
                                }
                            }
                        },
                        modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.cd_delete_photo),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }

                    IconButton(
                        onClick = { 
                            if (photos.isNotEmpty()) {
                                onSetAsCover(photos[pagerState.currentPage])
                            }
                        },
                        modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        val isCurrentCover = photos.getOrNull(pagerState.currentPage) == currentCoverUrl
                        Icon(
                            imageVector = if (isCurrentCover) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = stringResource(R.string.cd_set_as_cover),
                            tint = if (isCurrentCover) DoradoAtenea else Color.White
                        )
                    }
                }
            }

            // Indicador de página
            if (photos.size > 1) {
                Text(
                    text = "${pagerState.currentPage + 1} / ${photos.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = 24.dp)
                        .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
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
        TripDayUI("1", "15 Jul", "Lun", 1, 2, listOf("https://picsum.photos/200", "https://picsum.photos/201")),
        TripDayUI("2", "16 Jul", "Mar", 2, 4, listOf("https://picsum.photos/202", "https://picsum.photos/203", "https://picsum.photos/204")),
        TripDayUI("3", "17 Jul", "Mie", 3, 3, emptyList())
    )

    Hermes_travelappTheme {
        TripOverviewContent(
            trip = sampleTrip,
            uiDays = sampleDays
        )
    }
}
