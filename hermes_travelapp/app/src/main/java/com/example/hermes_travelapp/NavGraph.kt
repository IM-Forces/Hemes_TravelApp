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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.hermes_travelapp.data.PreferencesManager
import com.example.hermes_travelapp.data.repository.TripDayRepositoryImpl
import com.example.hermes_travelapp.data.repository.TripRepositoryImpl
import com.example.hermes_travelapp.domain.RecommendationItem
import com.example.hermes_travelapp.domain.Trip
import com.example.hermes_travelapp.domain.generateDaysForTrip
import com.example.hermes_travelapp.ui.screens.*
import com.example.hermes_travelapp.ui.viewmodels.*
import com.example.hermes_travelapp.ui.theme.Hermes_travelappTheme

sealed class BottomNavItem(val route: String, val icon: ImageVector, val labelRes: Int) {
    object Home : BottomNavItem("home", Icons.Default.Home, R.string.nav_home)
    object Explore : BottomNavItem("explore", Icons.Default.Search, R.string.nav_explore)
    object Trips : BottomNavItem("trips", Icons.Default.Place, R.string.nav_trips)
    object Favorites : BottomNavItem("favorites", Icons.Default.Favorite, R.string.nav_favorites)
    object Profile : BottomNavItem("profile", Icons.Default.Person, R.string.nav_profile)
}

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    themeViewModel: ThemeViewModel
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    
    val preferencesManager = remember { PreferencesManager(context) }
    val tripRepository = remember { TripRepositoryImpl() }
    val tripDayRepository = remember { TripDayRepositoryImpl() }
    
    val tripViewModel: TripViewModel = viewModel(
        factory = ViewModelFactory(tripRepository = tripRepository)
    )
    val tripDayViewModel: TripDayViewModel = viewModel(
        factory = ViewModelFactory(tripDayRepository = tripDayRepository)
    )
    val accountViewModel: AccountViewModel = viewModel(
        factory = ViewModelFactory(preferencesManager = preferencesManager)
    )
    
    var tripToEdit by remember { mutableStateOf<Trip?>(null) }
    var selectedTrip by remember { mutableStateOf<Trip?>(null) }
    
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
                accountViewModel = accountViewModel,
                themeViewModel = themeViewModel,
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
                    navController.navigate("tripOverview/${trip.id}")
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

        composable("tripOverview/{tripId}") { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: "1"
            TripOverviewScreen(
                tripId = tripId,
                tripViewModel = tripViewModel,
                tripDayViewModel = tripDayViewModel,
                onDayClick = { dayId -> 
                    navController.navigate("dayItinerary/$tripId/$dayId")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("dayItinerary/{tripId}/{dayId}") { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: "1"
            val dayId = backStackEntry.arguments?.getString("dayId") ?: "1"
            DayItineraryScreen(
                tripId = tripId,
                dayId = dayId,
                tripViewModel = tripViewModel,
                tripDayViewModel = tripDayViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToEditActivity = { activityId -> /* TODO */ }
            )
        }

        composable("createTrip") {
            CreateTripScreen(
                tripToEdit = tripToEdit,
                tripViewModel = tripViewModel,
                onBack = { 
                    tripToEdit = null
                    tripViewModel.clearError()
                    navController.popBackStack() 
                },
                onSaveTrip = { trip ->
                    val success = if (tripToEdit == null) {
                        val added = tripViewModel.addTrip(trip)
                        if (added) {
                            generateDaysForTrip(trip, tripDayRepository)
                        }
                        added
                    } else {
                        val edited = tripViewModel.editTrip(trip)
                        if (edited) {
                            generateDaysForTrip(trip, tripDayRepository)
                        }
                        edited
                    }
                    if (success) {
                        tripToEdit = null
                        navController.popBackStack()
                    }
                }
            )
        }

        composable("about") { AboutScreen(onBack = { navController.popBackStack() }) }
        composable("preferences") { 
            PreferencesScreen(
                onBack = { navController.popBackStack() },
                themeViewModel = themeViewModel
            ) 
        }
        composable("account") {
            AccountScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = accountViewModel
            )
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
fun MainScreen(
    rootNavController: NavHostController,
    tripViewModel: TripViewModel,
    accountViewModel: AccountViewModel,
    themeViewModel: ThemeViewModel,
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
    
    val trips by tripViewModel.trips.collectAsState()

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
                        label = { Text(text = stringResource(screen.labelRes), style = MaterialTheme.typography.labelSmall) },
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
                    favorites = favoritePlaces,
                    accountViewModel = accountViewModel
                ) 
            }
            composable(BottomNavItem.Explore.route) { ExploreScreen() }
            composable(BottomNavItem.Trips.route) { 
                TripsScreen(
                    trips = trips,
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
                    onNavigateToAccount = { rootNavController.navigate("account") },
                    onNavigateToAbout = { rootNavController.navigate("about") },
                    onNavigateToPreferences = { rootNavController.navigate("preferences") },
                    onNavigateToTerms = { rootNavController.navigate("terms") },
                    accountViewModel = accountViewModel,
                    tripViewModel = tripViewModel
                ) 
            }
        }
    }
}
