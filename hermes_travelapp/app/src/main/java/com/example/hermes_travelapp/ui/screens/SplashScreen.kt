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
            .background(Color(0xFF202020))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.hermeslogocompleto),
                contentDescription = "Logo Hermes Travel App",
                modifier = Modifier
                    .size(300.dp)
                    .graphicsLayer {
                        renderEffect = null
                    },
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Barra de carga
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .width(250.dp)
                    .height(6.dp),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f),
                drawStopIndicator = {}
            )
        }

        Text(
            text = "v1.0.0",
            color = Color.White.copy(alpha = 0.5f),
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