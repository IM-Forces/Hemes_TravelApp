package com.example.hermes_travelapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hermes_travelapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsScreen(
    onBack: () -> Unit = {},
    onAccept: () -> Unit = {},
    onReject: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Terms & Conditions", style = MaterialTheme.typography.titleLarge) },
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
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = BlancoMarmol
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Last Updated: January 2025",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TermsSection(
                title = "1. Acceptance of Terms",
                content = "By accessing and using Hermes Travel App, you agree to be bound by these Terms and Conditions. If you do not agree with any part of these terms, you must not use our services."
            )

            TermsSection(
                title = "2. User Responsibilities",
                content = "Users are responsible for maintaining the confidentiality of their account information and for all activities that occur under their account. You agree to provide accurate and complete information when registering."
            )

            TermsSection(
                title = "3. Travel Planning and Bookings",
                content = "Hermes provides tools for travel planning and information purposes. While we strive for accuracy, we are not responsible for changes in travel schedules, prices, or availability provided by third-party services."
            )

            TermsSection(
                title = "4. Privacy Policy",
                content = "Your use of Hermes is also governed by our Privacy Policy. Please review it to understand how we collect, use, and protect your personal information."
            )

            TermsSection(
                title = "5. Limitation of Liability",
                content = "Hermes shall not be liable for any direct, indirect, incidental, or consequential damages resulting from the use or inability to use the app or any information obtained through it."
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "If you have any questions regarding these terms, please contact us at support@hermesapp.com",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botones de Aceptar y Rechazar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TerracotaSuave
                    )
                ) {
                    Text("Decline", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DoradoAtenea,
                        contentColor = NegroCeramica
                    )
                ) {
                    Text("Accept", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun TermsSection(title: String, content: String) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = DoradoAtenea,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Justify
        )
    }
}

@Preview(showBackground = true, name = "Terms Mode Light")
@Composable
fun TermsScreenPreviewLight() {
    Hermes_travelappTheme(darkTheme = false) {
        TermsScreen()
    }
}

@Preview(showBackground = true, name = "Terms Mode Dark")
@Composable
fun TermsScreenPreviewDark() {
    Hermes_travelappTheme(darkTheme = true) {
        TermsScreen()
    }
}