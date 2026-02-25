package com.example.hermes_travelapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    Column {
        Text(text = "Home Screen")
        Button(onClick = { navController.navigate("profile") }) {
            Text(text = "Go to Profile")
        }
    }
}