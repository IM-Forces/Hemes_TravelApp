# Design Document - Hermes Travel App

## Sprint Schedule

| Sprint | Deliverable | Deadline | Weight |
|--------|-------------|----------|--------|
| **Sprint 1** | Splash Screen, Navigation, Screens, Scaffolding, Domain Model | 01/03/2026 | 0.8 pts |
| **Sprint 2** | Travel List (CRUD), Trip Itinerary (CRUD) | 15/03/2026 | 1.0 pts |
| **Sprint 3** | Data Persistence, External APIs, User Preferences | 12/04/2026 | 0.8 pts |
| **Sprint 4** | Login Screen, Authentication | 26/04/2026 | 1.0 pts |
| **Sprint 5** | Images, Documents, Hotel Reservations, Maps | 16/05/2026 | 1.4 pts |

---

## Architecture

### Pattern: MVVM (Model-View-ViewModel)

We chose MVVM because it's the recommended architecture for modern Android apps and works well with Jetpack Compose.

**Current Status (Sprint 01):**
- ✅ **View** - All screens implemented with Jetpack Compose
- ✅ **Navigation** - Two-level navigation (root + bottom tabs)
- ✅ **Domain Model** - Data classes defined
- ⏳ **ViewModel** - Planned for Sprint 02
- ⏳ **Repository** - Planned for Sprint 02

---

## Technology Stack

### Current (Sprint 01)
- **Kotlin** - Main programming language
- **Jetpack Compose** - Modern UI framework
- **Navigation Component** - Screen navigation
- **Material Design 3** - UI components and theming
- **Minimum SDK: API 26 (Android 8.0)** - Covers 94%+ of devices

### Future Sprints
- Local database (Sprint 3)
- Authentication system (Sprint 4)
- External APIs (Sprint 3)
- Image handling (Sprint 5)
- Interactive maps (Sprint 5)

---

## Project Structure

```
hermes_travelapp/
├── ui/
│   ├── screens/          # All app screens
│   ├── theme/            # Colors, typography, theme
│   └── navigation/       # Navigation graph
├── domain/
│   └── models/           # Data classes (Trip, User, etc.)
└── data/
    ├── repository/       # Data access (future)
    └── local/            # Local storage (future)
```

**Why this structure?**
- Clear separation between UI, business logic, and data
- Easy to find files
- Scales well as project grows

---

## Navigation

### Two-Level Navigation System

1. **Root Navigation** - Authentication and full-screen flows
   - Splash → Login → Register → Main
   - Main → Full-screen pages (TripDetail, CreateTrip, About, etc.)

2. **Bottom Navigation** - Main app tabs (inside MainScreen)
   - Home, Explore, Trips, Favorites, Profile

```
Splash → Login → Register → Main (with bottom nav)
                              ├─ Home
                              ├─ Explore
                              ├─ Trips
                              ├─ Favorites
                              └─ Profile
```

**Why bottom navigation with 5 tabs?**
- Easy to reach with thumb
- Standard pattern users know
- All main features always accessible

---

## Implemented Screens

### Authentication Flow
- **SplashScreen** - App logo and loading
- **LoginScreen** - User login (UI only, logic in Sprint 4)
- **RegisterScreen** - New account (UI only, logic in Sprint 4)

### Main Tabs
- **HomeScreen** - Dashboard
- **ExploreScreen** - Discover destinations
- **TripsScreen** - List of trips
- **FavoritesScreen** - Saved items
- **ProfileScreen** - User settings

### Other Screens
- **TripDetailScreen** - Trip itinerary details
- **CreateTripScreen** - Form to create new trip
- **AboutScreen** - App info and team
- **PreferencesScreen** - App settings
- **TermsScreen** - Terms and conditions

---

## Data Model (Planned)

The app will manage the following main entities:

### **User**
- Basic user information (id, email, name)
- Profile photo
- Account creation date

### **Trip**
- Trip details (title, destination, dates)
- Budget and currency
- Status (planned, ongoing, completed)
- Cover image

### **ItineraryItem**
- Activity details (title, description)
- Date and time information
- Location data
- Category (transport, accommodation, activity, etc.)
- Cost
- Completion status

### **Location**
- Geographic coordinates (latitude, longitude)
- Address and place name

### **Expense**
- Amount and currency
- Category (transport, food, activities, etc.)
- Date and description

### **Preferences**
- Language selection
- Currency preference
- Theme (light, dark, system)
- Notification settings

### Relationships
- One User can have many Trips
- One Trip can have many ItineraryItems
- One Trip can have many Expenses
- Each ItineraryItem has one Location
- Each User has one Preferences profile

**Note:** Detailed implementation with Kotlin data classes will be done in Sprint 02. For now, this is the conceptual model to guide our UI design.

---

## Design Decisions

### Why Jetpack Compose?
- Modern and less code than XML
- Easier to update UI
- Google's recommended approach

### Why MVVM?
- Separates UI from logic
- Easier to test
- Works well with Compose

### Why Long for dates?
- Easy to work with in Kotlin
- Can convert to any format needed
- Simple to compare

### Why separate Location class?
- Can reuse for different items
- Ready for maps integration
- Keeps data organized

---

## Next Steps

### Sprint 02 (15/03/2026)
- Create ViewModels for screens
- Implement CRUD for Trips (Create, Read, Update, Delete)
- Implement CRUD for Itinerary Items
- Use in-memory storage for now

### Sprint 03 (12/04/2026)
- Add local database
- Save data permanently
- Connect to external APIs (weather, currency, etc.)
- Complete Preferences screen functionality

### Sprint 04 (26/04/2026)
- Implement authentication
- User registration and login
- Secure session management

### Sprint 05 (16/05/2026)
- Add camera for photos
- Upload documents (PDFs)
- Hotel reservation features
- Interactive maps

---

**Version:** 1.0  
**Authors:** Ivan Gil Cañizares, Marco Beruet Morelli  
**Course:** Aplicaciones para Dispositivos Móviles (105025-2526)  
**Institution:** Universitat de Lleida - Campus Igualada
