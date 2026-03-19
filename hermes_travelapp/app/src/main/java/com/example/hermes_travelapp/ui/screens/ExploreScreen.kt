package com.example.hermes_travelapp.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hermes_travelapp.R
import com.example.hermes_travelapp.domain.RecommendationItem
import com.example.hermes_travelapp.ui.theme.Hermes_travelappTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    favorites: List<RecommendationItem> = emptyList(),
    onToggleFavorite: (RecommendationItem) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    
    val allItems = remember { loadRecommendationsFromAssets(context) }
    
    val filteredItems = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            allItems.filter { item ->
                item.lugar.contains(searchQuery, ignoreCase = true) ||
                item.pais.contains(searchQuery, ignoreCase = true) ||
                item.ciudadRegion.contains(searchQuery, ignoreCase = true) ||
                item.tipo.contains(searchQuery, ignoreCase = true) ||
                item.precio.toString().contains(searchQuery)
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.secondary)
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.explore_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    placeholder = { Text(stringResource(R.string.explore_search_placeholder)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    singleLine = true,
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Search, contentDescription = "Clear") 
                            }
                        }
                    }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            if (searchQuery.isNotEmpty() && filteredItems.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.explore_no_results, searchQuery),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            } else if (searchQuery.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.explore_start_typing),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            items(filteredItems) { item ->
                val isFavorite = favorites.any { it.lugar == item.lugar }
                RecommendationCard(
                    item = item,
                    isFavorite = isFavorite,
                    onToggleFavorite = { onToggleFavorite(item) }
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Explore Mode Light")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Explore Mode Dark"
)
@Composable
fun ExploreScreenPreview() {
    Hermes_travelappTheme {
        ExploreScreen()
    }
}
