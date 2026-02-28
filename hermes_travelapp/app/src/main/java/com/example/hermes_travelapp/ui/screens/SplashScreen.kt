package com.example.hermes_travelapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hermes_travelapp.R
import com.example.hermes_travelapp.ui.theme.Hermes_travelappTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToLogin: () -> Unit) {
    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        // Animación de la barra de progreso
        while (progress < 1f) {
            delay(30L)
            progress += 0.01f
        }
        onNavigateToLogin()
    }

    SplashScreenContent(progress = progress)
}

@Composable
fun SplashScreenContent(progress: Float = 0f) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.hermes_no_bg),
                contentDescription = "Logo Hermes Travel App",
                modifier = Modifier.size(400.dp),
                contentScale = ContentScale.Fit
            )

            // Título HERMES usando el estilo del Tema (Dorado Atenea)
            Text(
                text = "HERMES",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 48.sp, // Sobreescribimos solo el tamaño para el Splash
                letterSpacing = 12.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Línea decorativa
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.width(220.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(5.dp)
                        .graphicsLayer { rotationZ = 45f }
                        .background(MaterialTheme.colorScheme.primary)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Subtítulo TRAVEL APP usando el estilo del Tema (Blanco Mármol)
            Text(
                text = "TRAVEL APP",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 13.sp,
                letterSpacing = 6.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(56.dp))

            Text(
                text = "Starting your Journey...",
                style = MaterialTheme.typography.bodyMedium,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Barra de carga usando los colores del Tema
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .width(250.dp)
                    .height(6.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                drawStopIndicator = {}
            )
        }

        Text(
            text = "v1.0.0 - Sprint 01",
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    Hermes_travelappTheme {
        SplashScreenContent(progress = 0.5f)
    }
}