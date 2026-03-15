package com.example.hermes_travelapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.hermes_travelapp.domain.Trip
import com.example.hermes_travelapp.ui.screens.*
import com.example.hermes_travelapp.ui.theme.Hermes_travelappTheme

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Explore : BottomNavItem("explore", Icons.Default.Search, "Explore")
    object Trips : BottomNavItem("trips", Icons.Default.Place, "Trips")
    object Favorites : BottomNavItem("favorites", Icons.Default.Favorite, "Favorites")
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Profile")
}

class TripViewModel : ViewModel() {
    private val _trips = mutableStateListOf<Trip>()
    val trips: List<Trip> get() = _trips

    fun addTrip(trip: Trip) {
        _trips.add(trip)
    }

    fun editTrip(updatedTrip: Trip) {
        val index = _trips.indexOfFirst { it.id == updatedTrip.id }
        if (index != -1) {
            _trips[index] = updatedTrip
        }
    }

    fun deleteTrip(tripId: String) {
        _trips.removeAll { it.id == tripId }
    }
}

@Composable
fun NavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val tripViewModel: TripViewModel = viewModel()
    
    var tripToEdit by remember { mutableStateOf<Trip?>(null) }
    var selectedTrip by remember { mutableStateOf<Trip?>(null) }
    
    // Estado compartido para favoritos en memoria
    val favoritePlaces = remember { mutableStateListOf<RecommendationItem>() }

    NavHost(navController = navController, startDestination = "splash", modifier = modifier) {

        composable("splash") {
            SplashScreen(onNavigateToLogin = {
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }

        composable("login") {
            LoginScreen(
                onLoginClick = { navController.navigate("main") },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterClick = { navController.navigate("main") },
                onNavigateToLogin = { navController.navigate("login") }
            )
        }

        composable("main") { 
            MainScreen(
                rootNavController = navController,
                tripViewModel = tripViewModel,
                onEditTrip = { trip ->
                    tripToEdit = trip
                    navController.navigate("createTrip")
                },
                onCreateTrip = {
                    tripToEdit = null
                    navController.navigate("createTrip")
                },
                onTripClick = { trip ->
                    selectedTrip = trip
                    navController.navigate("tripDetail")
                },
                favoritePlaces = favoritePlaces,
                onToggleFavorite = { item ->
                    if (favoritePlaces.any { it.lugar == item.lugar }) {
                        favoritePlaces.removeAll { it.lugar == item.lugar }
                    } else {
                        favoritePlaces.add(item)
                    }
                }
            ) 
        }

        composable("tripDetail") {
            TripDetailScreen(
                trip = selectedTrip,
                onBack = { navController.popBackStack() }
            )
        }

        composable("createTrip") {
            CreateTripScreen(
                tripToEdit = tripToEdit,
                onBack = { 
                    tripToEdit = null
                    navController.popBackStack() 
                },
                onSaveTrip = { trip ->
                    if (tripToEdit == null) {
                        tripViewModel.addTrip(trip)
                    } else {
                        tripViewModel.editTrip(trip)
                    }
                    tripToEdit = null
                    navController.popBackStack()
                }
            )
        }

        composable("about") { AboutScreen(onBack = { navController.popBackStack() }) }
        composable("preferences") { PreferencesScreen(onBack = { navController.popBackStack() }) }
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
fun MainScreen(
    rootNavController: NavHostController,
    tripViewModel: TripViewModel,
    onEditTrip: (Trip) -> Unit,
    onCreateTrip: () -> Unit,
    onTripClick: (Trip) -> Unit,
    favoritePlaces: List<RecommendationItem>,
    onToggleFavorite: (RecommendationItem) -> Unit
) {
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
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(text = screen.label, style = MaterialTheme.typography.labelSmall) },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
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
            composable(BottomNavItem.Home.route) { 
                HomeScreen(
                    onToggleFavorite = onToggleFavorite,
                    favorites = favoritePlaces
                ) 
            }
            composable(BottomNavItem.Explore.route) { ExploreScreen() }
            composable(BottomNavItem.Trips.route) { 
                TripsScreen(
                    trips = tripViewModel.trips,
                    onTripClick = onTripClick,
                    onEditTripClick = onEditTrip,
                    onCreateTripClick = onCreateTrip,
                    onDeleteTripClick = { id -> tripViewModel.deleteTrip(id) }
                )
            }
            composable(BottomNavItem.Favorites.route) { 
                FavoritesScreen(
                    favorites = favoritePlaces,
                    onRemoveFavorite = onToggleFavorite
                ) 
            }
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
