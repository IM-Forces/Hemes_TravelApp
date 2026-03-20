package com.example.hermes_travelapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hermes_travelapp.R
import com.example.hermes_travelapp.ui.theme.Hermes_travelappTheme
import com.example.hermes_travelapp.ui.viewmodels.AccountViewModel
import com.example.hermes_travelapp.ui.viewmodels.TripViewModel

@Composable
fun ProfileScreen(
    onNavigateToAccount: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onNavigateToPreferences: () -> Unit = {},
    onNavigateToTerms: () -> Unit = {},
    accountViewModel: AccountViewModel = viewModel(),
    tripViewModel: TripViewModel = viewModel()
) {
    val username by accountViewModel.username.collectAsState()
    val email by accountViewModel.email.collectAsState()
    val trips by tripViewModel.trips.collectAsState()

    ProfileScreenContent(
        username = username,
        email = email,
        tripCount = trips.size,
        onNavigateToAccount = onNavigateToAccount,
        onNavigateToAbout = onNavigateToAbout,
        onNavigateToPreferences = onNavigateToPreferences,
        onNavigateToTerms = onNavigateToTerms
    )
}

@Composable
fun ProfileScreenContent(
    username: String,
    email: String,
    tripCount: Int = 0,
    onNavigateToAccount: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onNavigateToPreferences: () -> Unit = {},
    onNavigateToTerms: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val initials = if (username.length >= 2) username.take(2).uppercase() else username.uppercase()
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(top = 64.dp, bottom = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = username,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ProfileStatBadge(
                        text = "$tripCount " + stringResource(R.string.nav_trips),
                        icon = Icons.Default.TravelExplore
                    )
                }
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = stringResource(R.string.prefs_user_profile),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                
                ProfileOptionItem(
                    title = stringResource(R.string.profile_account),
                    icon = Icons.Default.Person,
                    onClick = onNavigateToAccount
                )
                
                ProfileOptionItem(
                    title = stringResource(R.string.prefs_title),
                    icon = Icons.Default.Settings,
                    onClick = onNavigateToPreferences
                )
                
                Text(
                    text = "Support & Legal",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
                )
                
                ProfileOptionItem(
                    title = stringResource(R.string.profile_terms),
                    icon = Icons.Default.Description,
                    onClick = onNavigateToTerms
                )
                
                ProfileOptionItem(
                    title = stringResource(R.string.profile_about),
                    icon = Icons.Default.Info,
                    onClick = onNavigateToAbout
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                
                OutlinedButton(
                    onClick = { /* Acción mock */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                        .height(56.dp),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.tertiary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.profile_logout),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileStatBadge(text: String, icon: ImageVector) {
    Surface(
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ProfileOptionItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
    }
}

@Preview(showBackground = true, name = "Profile Mode Light")
@Composable
fun ProfileScreenPreviewLight() {
    Hermes_travelappTheme(darkTheme = false) {
        ProfileScreenContent(
            username = "Vítor Da Silva",
            email = "vitor.dasilva@example.com",
            tripCount = 3
        )
    }
}

@Preview(showBackground = true, name = "Profile Mode Dark")
@Composable
fun ProfileScreenPreviewDark() {
    Hermes_travelappTheme(darkTheme = true) {
        ProfileScreenContent(
            username = "Vítor Da Silva",
            email = "vitor.dasilva@example.com",
            tripCount = 3
        )
    }
}
