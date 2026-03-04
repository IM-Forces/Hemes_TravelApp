package com.example.hermes_travelapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.hermes_travelapp.ui.screens.*
import com.example.hermes_travelapp.ui.theme.Hermes_travelappTheme
import com.example.hermes_travelapp.ui.theme.AzulEgeo

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

        // Pantalla principal con su propio sistema de navegación inferior
        composable("main") { 
            MainScreen(rootNavController = navController) 
        }

        // Pantalla de detalles del viaje (Fuera del menú inferior para pantalla completa)
        composable("tripDetail") {
            TripDetailScreen(onBack = { navController.popBackStack() })
        }

        // Pantallas adicionales
        composable("about") { 
            AboutScreen(onBack = { navController.popBackStack() }) 
        }
        
        composable("preferences") { 
            PreferencesScreen(onBack = { navController.popBackStack() }) 
        }
        
        composable("terms") { 
            TermsScreen(
                onBack = { navController.popBackStack() },
                onAccept = { navController.popBackStack() },
                onReject = { navController.popBackStack() }
            ) 
        }
    }
}

@Composable
fun MainScreen(rootNavController: NavHostController) {
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
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.secondary,
                tonalElevation = 0.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = {
                            Text(
                                text = screen.label,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = BottomNavItem.Home.route,
            Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) { HomeScreen() }
            composable(BottomNavItem.Explore.route) { ExploreScreen() }
            composable(BottomNavItem.Trips.route) { 
                TripsScreen(onTripClick = { rootNavController.navigate("tripDetail") }) 
            }
            composable(BottomNavItem.Favorites.route) { FavoritesScreen() }
            composable(BottomNavItem.Profile.route) { 
                ProfileScreen(
                    onNavigateToAbout = { rootNavController.navigate("about") },
                    onNavigateToPreferences = { rootNavController.navigate("preferences") },
                    onNavigateToTerms = { rootNavController.navigate("terms") }
                ) 
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    Hermes_travelappTheme(darkTheme = false) {
        MainScreen(rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreviewDark() {
    Hermes_travelappTheme(darkTheme = true) {
        MainScreen(rememberNavController())
    }
}