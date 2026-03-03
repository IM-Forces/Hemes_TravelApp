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
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hermes_travelapp.ui.theme.Hermes_travelappTheme

@Composable
fun ProfileScreen(
    onNavigateToAbout: () -> Unit = {},
    onNavigateToPreferences: () -> Unit = {},
    onNavigateToTerms: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    // Detectamos si el tema actual es oscuro basándonos en la luminosidad del fondo
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    
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
            // Header Section with Theme-aware Gradient
            val headerGradientColor = if (isDark) {
                MaterialTheme.colorScheme.secondary // Azul Egeo para oscuro
            } else {
                MaterialTheme.colorScheme.tertiary // Terracota para claro
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                headerGradientColor.copy(alpha = 0.7f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(top = 64.dp, bottom = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Avatar de Usuario (120dp)
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "AJ",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Alex Johnson",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "alex.johnson@example.com",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats/Badges
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProfileStatBadge(text = "3 Active Trips", icon = Icons.Default.TravelExplore)
                        ProfileStatBadge(text = "12 Countries", icon = Icons.Default.Map)
                    }
                }
            }
            
            // Options Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Account",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                
                ProfileOptionItem(
                    title = "Personal Info",
                    icon = Icons.Default.Person,
                    onClick = { /* Acción mock */ }
                )
                
                ProfileOptionItem(
                    title = "Preferences",
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
                    title = "Terms & Conditions",
                    icon = Icons.Default.Description,
                    onClick = onNavigateToTerms
                )
                
                ProfileOptionItem(
                    title = "About Hermes",
                    icon = Icons.Default.Info,
                    onClick = onNavigateToAbout
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Botón de Cerrar Sesión destacado
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
                        text = "Sign Out",
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
            // Icono con fondo circular llamativo
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
        ProfileScreen()
    }
}

@Preview(showBackground = true, name = "Profile Mode Dark")
@Composable
fun ProfileScreenPreviewDark() {
    Hermes_travelappTheme(darkTheme = true) {
        ProfileScreen()
    }
}
