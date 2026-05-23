package com.example.hermes_travelapp

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hermes_travelapp.ui.theme.Hermes_travelappTheme
import androidx.compose.foundation.layout.Box
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.hermes_travelapp.domain.model.RecommendationItem
import com.example.hermes_travelapp.domain.model.Trip
import com.example.hermes_travelapp.ui.screens.*
import com.example.hermes_travelapp.ui.viewmodels.*
import kotlinx.coroutines.launch

sealed class BottomNavItem(val route: String, val icon: ImageVector, val labelRes: Int) {
    object Home : BottomNavItem("home", Icons.Default.Home, R.string.nav_home)
    object Explore : BottomNavItem("explore", Icons.Default.Hotel, R.string.nav_hotels)
    object Trips : BottomNavItem("trips", Icons.Default.Place, R.string.nav_trips)
    object Favorites : BottomNavItem("favorites", Icons.Default.Favorite, R.string.nav_favorites)
    object Profile : BottomNavItem("profile", Icons.Default.Person, R.string.nav_profile)
}

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    themeViewModel: ThemeViewModel
) {
    val navController = rememberNavController()
    
    val authViewModel: AuthViewModel = hiltViewModel()
    val tripViewModel: TripViewModel = hiltViewModel()
    val tripDayViewModel: TripDayViewModel = hiltViewModel()
    val accountViewModel: AccountViewModel = hiltViewModel()
    val activityViewModel: ActivityViewModel = hiltViewModel()
    
    var tripToEdit by remember { mutableStateOf<Trip?>(null) }
    
    val favoritePlaces = remember { mutableStateListOf<RecommendationItem>() }

    NavHost(navController = navController, startDestination = "splash", modifier = modifier) {
        composable("splash") {
            SplashScreen(
                isLoggedIn = authViewModel.isLoggedIn(),
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                onLoginSuccess = { 
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToForgotPassword = { navController.navigate("forgotPassword") },
                authViewModel = authViewModel
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterClick = { 
                    navController.navigate("main") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.navigate("login") }
            )
        }

        composable("forgotPassword") {
            ForgotPasswordScreen(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("forgotPassword") { inclusive = true }
                    }
                }
            )
        }

        composable("main") { 
            MainScreen(
                rootNavController = navController,
                tripViewModel = tripViewModel,
                accountViewModel = accountViewModel,
                authViewModel = authViewModel,
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
                activityViewModel = activityViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("createTrip") {
            val scope = rememberCoroutineScope()
            CreateTripScreen(
                tripToEdit = tripToEdit,
                tripViewModel = tripViewModel,
                onBack = {
                    tripViewModel.clearError()
                    navController.popBackStack() 
                },
                onSaveTrip = { trip ->
                    scope.launch {
                        val success = if (tripToEdit == null) {
                            val added = tripViewModel.addTrip(trip)
                            if (added) {
                                tripDayViewModel.generateDays(trip)
                            }
                            added
                        } else {
                            val edited = tripViewModel.editTrip(trip)
                            if (edited) {
                                tripDayViewModel.generateDays(trip)
                            }
                            edited
                        }
                        if (success) {
                            navController.popBackStack()
                        }
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
    authViewModel: AuthViewModel,
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

    LaunchedEffect(Unit) {
        tripViewModel.loadTrips()
        accountViewModel.loadUserData()
    }

    Scaffold(
        bottomBar = {
            Column {
                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                )
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background,
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    items.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { 
                            it.route == screen.route || 
                            (screen == BottomNavItem.Explore && (it.route?.startsWith("hotelList") == true || it.route?.startsWith("hotelDetail") == true))
                        } == true

                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(text = stringResource(screen.labelRes), style = MaterialTheme.typography.labelSmall) },
                            selected = selected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary
                            ),
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
                    accountViewModel = accountViewModel,
                    onSearchHotelsClick = {
                        navController.navigate(BottomNavItem.Explore.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onProfileClick = {
                        navController.navigate(BottomNavItem.Profile.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                ) 
            }
            composable(BottomNavItem.Explore.route) { backStackEntry ->
                // Use ViewModel scoped to "main" (the parent route)
                val mainEntry = remember(backStackEntry) {
                    rootNavController.getBackStackEntry("main")
                }
                val hotelViewModel: HotelViewModel = hiltViewModel(mainEntry)
                
                val username by accountViewModel.username.collectAsState()
                HotelSearchScreen(
                    viewModel = hotelViewModel,
                    onNavigateToResults = {
                        val city = hotelViewModel.city.value
                        val start = hotelViewModel.startDate.value.replace("/", "-")
                        val end = hotelViewModel.endDate.value.replace("/", "-")
                        navController.navigate("hotelList/$city/$start/$end")
                    },
                    onProfileClick = {
                        navController.navigate(BottomNavItem.Profile.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    showBack = false,
                    username = username
                )
            }
            composable("hotelList/{city}/{start}/{end}") { backStackEntry ->
                val city = backStackEntry.arguments?.getString("city") ?: ""
                val start = backStackEntry.arguments?.getString("start") ?: ""
                val end = backStackEntry.arguments?.getString("end") ?: ""
                
                val mainEntry = remember(backStackEntry) {
                    rootNavController.getBackStackEntry("main")
                }
                val hotelViewModel: HotelViewModel = hiltViewModel(mainEntry)

                HotelListScreen(
                    city = city,
                    startDate = start.replace("-", "/"),
                    endDate = end.replace("-", "/"),
                    viewModel = hotelViewModel,
                    onBack = { navController.popBackStack() },
                    onHotelClick = { hotel ->
                        navController.navigate("hotelDetail/${hotel.id}/$city/$start/$end")
                    }
                )
            }
            composable("hotelDetail/{hotelId}/{city}/{start}/{end}") { backStackEntry ->
                val hotelId = backStackEntry.arguments?.getString("hotelId") ?: ""
                val city = backStackEntry.arguments?.getString("city") ?: ""
                val start = backStackEntry.arguments?.getString("start") ?: ""
                val end = backStackEntry.arguments?.getString("end") ?: ""
                
                val mainEntry = remember(backStackEntry) {
                    rootNavController.getBackStackEntry("main")
                }
                val hotelViewModel: HotelViewModel = hiltViewModel(mainEntry)

                HotelDetailScreen(
                    hotelId = hotelId,
                    city = city,
                    startDate = start.replace("-", "/"),
                    endDate = end.replace("-", "/"),
                    viewModel = hotelViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(BottomNavItem.Trips.route) { 
                val username by accountViewModel.username.collectAsState()
                TripsScreen(
                    trips = trips,
                    onTripClick = onTripClick,
                    onEditTripClick = onEditTrip,
                    onCreateTripClick = onCreateTrip,
                    onDeleteTripClick = { id -> tripViewModel.deleteTrip(id) },
                    onProfileClick = {
                        navController.navigate(BottomNavItem.Profile.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    username = username
                )
            }
            composable(BottomNavItem.Favorites.route) { 
                FavoritesScreen(
                    favorites = favoritePlaces,
                    onRemoveFavorite = onToggleFavorite,
                    onProfileClick = {
                        navController.navigate(BottomNavItem.Profile.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    accountViewModel = accountViewModel
                ) 
            }
            composable(BottomNavItem.Profile.route) { 
                ProfileScreen(
                    onNavigateToAccount = { rootNavController.navigate("account") },
                    onNavigateToAbout = { rootNavController.navigate("about") },
                    onNavigateToPreferences = { rootNavController.navigate("preferences") },
                    onNavigateToTerms = { rootNavController.navigate("terms") },
                    onNavigateToHotelSearch = {
                        navController.navigate(BottomNavItem.Explore.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onLogout = {
                        authViewModel.signOut()
                        rootNavController.navigate("login") {
                            popUpTo("main") { inclusive = true }
                        }
                    },
                    accountViewModel = accountViewModel,
                    tripViewModel = tripViewModel
                ) 
            }
        }
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Preview(showBackground = true, name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MainScreenPreview() {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Explore,
        BottomNavItem.Trips,
        BottomNavItem.Favorites,
        BottomNavItem.Profile
    )
    
    Hermes_travelappTheme {
        Scaffold(
            bottomBar = {
                Column {
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                    )
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.background,
                    ) {
                        items.forEach { screen ->
                            NavigationBarItem(
                                icon = { Icon(screen.icon, contentDescription = null) },
                                label = { Text(text = stringResource(screen.labelRes), style = MaterialTheme.typography.labelSmall) },
                                selected = screen == BottomNavItem.Trips,
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primary
                                ),
                                onClick = { }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                TripsScreen(
                    trips = listOf(
                        Trip("1", "Atenas", "🏛️", "20/05/2024", "25/05/2024", "Explorando la ciudad antigua.")
                    ),
                    username = "Marco"
                )
            }
        }
    }
}
