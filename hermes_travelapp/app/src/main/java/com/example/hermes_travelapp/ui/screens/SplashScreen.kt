package com.example.hermes_travelapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import com.example.hermes_travelapp.R
import com.example.hermes_travelapp.ui.theme.Hermes_travelappTheme
import kotlinx.coroutines.delay

// Paleta Grecia Clásica
private val NegroCeramica = Color(0xFF1A1A1A)
private val DoradoAtenea = Color(0xFFC8A45A)
private val BlancoMarmol = Color(0xFFF5F5F2)
private val AzulEgeo = Color(0xFF2F5D8C)
private val AzulOscuro = Color(0xFF1A1A2E)


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
            .background(AzulOscuro)
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

            // Título HERMES
            Text(
                text = "HERMES",
                color = DoradoAtenea,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 12.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Línea decorativa con diamante central
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.width(220.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(DoradoAtenea.copy(alpha = 0.5f))
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(5.dp)
                        .graphicsLayer { rotationZ = 45f }
                        .background(DoradoAtenea)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(DoradoAtenea.copy(alpha = 0.5f))
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Subtítulo TRAVEL APP
            Text(
                text = "TRAVEL APP",
                color = BlancoMarmol,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 6.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(56.dp))

            // Texto "Starting your Journey..."
            Text(
                text = "Starting your Journey...",
                color = BlancoMarmol.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Barra de carga dorada
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .width(250.dp)
                    .height(6.dp),
                color = DoradoAtenea,
                trackColor = BlancoMarmol.copy(alpha = 0.2f),
                drawStopIndicator = {}
            )

        }

        Text(
            text = "v1.0.0 - Sprint 01",
            color = BlancoMarmol.copy(alpha = 0.5f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
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