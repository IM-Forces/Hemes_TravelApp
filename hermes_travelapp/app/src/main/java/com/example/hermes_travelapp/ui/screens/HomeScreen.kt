package com.example.hermes_travelapp.ui.screens

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hermes_travelapp.ui.theme.*
import org.json.JSONArray
import java.io.InputStream

data class RecommendationItem(
    val lugar: String,
    val tipo: String,
    val pais: String,
    val ciudadRegion: String,
    val precio: Int,
    val descripcion: String
)

fun loadRecommendationsFromAssets(context: Context): List<RecommendationItem> {
    val items = mutableListOf<RecommendationItem>()
    try {
        val inputStream: InputStream = context.assets.open("exemplos.json")
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        val jsonString = String(buffer, Charsets.UTF_8)
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            items.add(
                RecommendationItem(
                    lugar = obj.getString("lugar"),
                    tipo = obj.getString("tipo"),
                    pais = obj.getString("pais"),
                    ciudadRegion = obj.getString("ciudad_region"),
                    precio = obj.optInt("precio_estimado_usd", 0),
                    descripcion = obj.getString("descripcion")
                )
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return items
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    items: List<RecommendationItem>? = null,
    favorites: List<RecommendationItem> = emptyList(),
    onToggleFavorite: (RecommendationItem) -> Unit = {}
) {
    val context = LocalContext.current
    val recommendations = remember(items) { items ?: loadRecommendationsFromAssets(context) }

    Scaffold(
        topBar = { HomeTopBar() },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    text = "Explora el mundo",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            if (recommendations.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillParentMaxHeight(0.7f), contentAlignment = Alignment.Center) {
                        Text("No se pudieron cargar las recomendaciones.")
                    }
                }
            } else {
                items(recommendations) { item ->
                    val isFavorite = favorites.any { it.lugar == item.lugar }
                    RecommendationCard(
                        item = item,
                        isFavorite = isFavorite,
                        onToggleFavorite = { onToggleFavorite(item) }
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun RecommendationCard(
    item: RecommendationItem,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.tipo,
                        style = MaterialTheme.typography.labelSmall,
                        color = DoradoAtenea,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = item.lugar,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${item.ciudadRegion}, ${item.pais}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Precio sustituyendo a la descripción
                Text(
                    text = if (item.precio > 0) "${item.precio}€" else "Gratis",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar() {
    MediumTopAppBar(
        title = {
            Text(
                text = "Hola, Vítor 👋",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        },
        actions = {
            IconButton(onClick = { }) {
                Icon(Icons.Default.Notifications, null, tint = MaterialTheme.colorScheme.onTertiary, modifier = Modifier.size(28.dp))
            }
            Box(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text("VS", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = MaterialTheme.colorScheme.secondary)
    )
}

private val previewItems = listOf(
    RecommendationItem("Hotel Rosewood Mayakoba", "Hotel", "México", "Riviera Maya", 800, ""),
    RecommendationItem("Chichén Itzá", "Sitio Arqueológico", "México", "Yucatán", 35, ""),
    RecommendationItem("Pujol", "Restaurante", "México", "CDMX", 200, "")
)

@Preview(showBackground = true, name = "Modo Claro")
@Composable
fun HomeScreenPreviewLight() {
    Hermes_travelappTheme(darkTheme = false) {
        HomeScreen(items = previewItems)
    }
}

@Preview(showBackground = true, name = "Modo Oscuro", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenPreviewDark() {
    Hermes_travelappTheme(darkTheme = true) {
        HomeScreen(items = previewItems)
    }
}
