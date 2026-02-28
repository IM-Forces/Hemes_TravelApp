package com.example.hermes_travelapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DoradoAtenea,
    secondary = AzulEgeo,
    tertiary = AzulOscuro,
    background = AzulOscuro,
    surface = AzulOscuro,
    onPrimary = NegroCeramica,
    onSecondary = BlancoMarmol,
    onTertiary = BlancoMarmol,
    onBackground = BlancoMarmol,
    onSurface = BlancoMarmol
)

private val LightColorScheme = lightColorScheme(
    primary = DoradoAtenea,
    secondary = AzulEgeo,
    tertiary = AzulOscuro,
    background = AzulOscuro, // Forzamos el azul oscuro como fondo base de la marca
    surface = AzulOscuro,
    onPrimary = NegroCeramica,
    onSecondary = BlancoMarmol,
    onTertiary = BlancoMarmol,
    onBackground = BlancoMarmol,
    onSurface = BlancoMarmol
)

@Composable
fun Hermes_travelappTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}