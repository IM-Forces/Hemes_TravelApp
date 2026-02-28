package com.example.hermes_travelapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.hermes_travelapp.ui.theme.Hermes_travelappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Hermes_travelappTheme {
                // Dejamos que NavGraph gestione toda la pantalla
                NavGraph()
            }
        }
    }
}