package com.example.hermes_travelapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hermes_travelapp.R
import com.example.hermes_travelapp.ui.theme.Hermes_travelappTheme

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.hermes_no_bg),
                contentDescription = "Logo Hermes Travel App",
                modifier = Modifier.size(250.dp).padding(bottom = 32.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "Welcome to Hermes",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = "",
                onValueChange = { },
                label = { Text("Email", style = MaterialTheme.typography.bodyMedium) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = "",
                onValueChange = { },
                label = { Text("Password", style = MaterialTheme.typography.bodyMedium) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Login", color = MaterialTheme.colorScheme.onPrimary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onNavigateToRegister) {
                Text(
                    "Don't have an account? Register",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    Hermes_travelappTheme {
        LoginScreen()
    }
}