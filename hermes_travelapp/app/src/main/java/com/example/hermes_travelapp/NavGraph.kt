package com.example.hermes_travelapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.hermes_travelapp.ui.screens.ExploreScreen
import com.example.hermes_travelapp.ui.screens.FavoritesScreen
import com.example.hermes_travelapp.ui.screens.HomeScreen
import com.example.hermes_travelapp.ui.screens.LoginScreen
import com.example.hermes_travelapp.ui.screens.ProfileScreen
import com.example.hermes_travelapp.ui.screens.RegisterScreen
import com.example.hermes_travelapp.ui.screens.SplashScreen
import com.example.hermes_travelapp.ui.screens.TripsScreen

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Explore : BottomNavItem("explore", Icons.Default.Search, "Explore")
    object Trips : BottomNavItem("trips", Icons.Default.Place, "Trips")
    object Favorites : BottomNavItem("favorites", Icons.Default.Favorite, "Favorites")
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Profile")
}

@Composable
fun NavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash", modifier = modifier) {

        // Pantalla de carga
        composable("splash") {
            SplashScreen(onNavigateToLogin = {
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }

        // Pantalla de login
        composable("login") {
            LoginScreen(
                onLoginClick = { navController.navigate("main") },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        // Pantalla de registro
        composable("register") {
            RegisterScreen(
                onRegisterClick = { navController.navigate("main") },
                onNavigateToLogin = { navController.navigate("login") }
            )
        }

        // Pantalla principal
        composable("main") { MainScreen() }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Explore,
        BottomNavItem.Trips,
        BottomNavItem.Favorites,
        BottomNavItem.Profile
    )
    Scaffold(
        bottomBar = {
            NavigationBar {
                // Comprobamos en que pantalla estamos actualmente
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                // Dibujamos cada boton de la barra de navegacion
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.label) },
                        // Si la ruta actual coincide con el boton, lo seleccionamos
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Evita la acumulacion de pantallas en el historial
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true  // Evita crear una nueva instancia de la pantalla
                                restoreState = true     // Recupera el estado de la pantalla
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->     // Inner padding para no tapar el contenido de la barra de navegacion
        NavHost(
            navController,
            startDestination = BottomNavItem.Home.route,
            Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) { HomeScreen() }
            composable(BottomNavItem.Explore.route) { ExploreScreen() }
            composable(BottomNavItem.Trips.route) { TripsScreen() }
            composable(BottomNavItem.Favorites.route) { FavoritesScreen() }
            composable(BottomNavItem.Profile.route) { ProfileScreen() }
        }
    }
}