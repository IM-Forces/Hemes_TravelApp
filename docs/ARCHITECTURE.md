# Architecture

## Pattern: MVVM + Repository

Hermes follows **MVVM (Model-View-ViewModel)** with a Repository layer separating each feature's business logic from its data sources.

- **View** — Jetpack Compose screens, stateless where possible, driven by `StateFlow`/`LiveData` from their ViewModel
- **ViewModel** — one `@HiltViewModel` per feature (auth, trips, activities, hotels, reservations, account, theme), exposes UI state and validation
- **Repository** — one interface + implementation per domain, hiding whether data comes from Room, Retrofit, or Firebase
- **Room Database** — local SQLite persistence, source of truth for trips, itineraries, users, access logs and reservations
- **Retrofit** — remote hotel search/booking REST API
- **Firebase Auth** — email/password authentication only; no business data is stored in Firebase
- **Hilt** — dependency injection across all layers

```
UI (Compose)
   │  collectAsState()
   ▼
ViewModel (@HiltViewModel)
   │  suspend / Flow
   ▼
Repository (interface)
   │
   ├──► RoomDAO ──► SQLite (local, offline-first)
   └──► ApiService ──► REST hotel API (remote)
```

## Tech Stack

- **Kotlin** — primary language
- **Jetpack Compose** — declarative UI
- **Navigation Component** — two-level navigation (root + bottom tabs)
- **Material Design 3** — theming and components
- **Room** — DAO / Entities / TypeConverters for local persistence
- **Hilt** — dependency injection
- **Firebase Auth** — email/password authentication
- **Retrofit + OkHttp** — REST networking for the hotel booking API
- **Coil** — async image loading (`AsyncImage`)
- **Coroutines + Flow** — async operations and reactive UI updates
- **SharedPreferences** — lightweight scalar user preferences (language, dark mode)
- **Minimum SDK: API 26 (Android 8.0)** — chosen to balance modern APIs with broad device coverage

## Project Structure

```
hermes_travelapp/
├── ui/
│   ├── screens/          # Compose screens
│   ├── theme/            # Colors, typography, theme
│   └── viewmodels/       # Hilt-injected ViewModels
├── domain/
│   ├── model/            # Domain data classes (Trip, TripDay, ItineraryItem, User, Hotel…)
│   ├── repository/       # Repository interfaces (data-source agnostic contracts)
│   └── ValidationUtils   # Date and field validation logic
└── data/
    ├── database/
    │   ├── dao/          # TripDao, TripDayDao, ItineraryItemDao, UserDao, AccessLogDao, ReservationDao
    │   ├── entities/      # Room entities
    │   ├── mapper/        # Entity <-> domain-model mapping functions
    │   └── AppDatabase    # Room database class + TypeConverters
    ├── remote/
    │   ├── api/           # Retrofit service interfaces
    │   ├── dto/            # API response/request DTOs
    │   └── mapper/         # DTO <-> domain-model mapping functions
    ├── repository/        # Repository implementations (Room + Retrofit + Firebase)
    └── PreferencesManager  # SharedPreferences wrapper
```

## Navigation

Two-level navigation system:

1. **Root navigation** — authentication and full-screen flows
   - Splash → Login → Register → Main
   - Main → full-screen pages (Trip overview, Day itinerary, Create trip, Account, Preferences, About, Terms, Reservations)
2. **Bottom navigation** — main app tabs inside `MainScreen`
   - Home · Explore/Hotels · Trips · Favorites · Profile

```
Splash
  ├─► Login ──► ForgotPassword
  │     └─► Register
  └─► Main (bottom nav)
        ├─ Home
        ├─ Explore (Hotel search → results → detail → booking)
        ├─ Trips ──► TripOverview ──► DayItinerary
        │              └─► CreateTrip
        ├─ Favorites
        └─ Profile ──► Account / Preferences / Terms / About / Reservations
```

## Database Schema

**Database name:** `hermes_database` · **Room version:** 4 · **Migration strategy:** `fallbackToDestructiveMigration` in development (see [Migration Strategy](#migration-strategy) for the production plan).

### Entity Diagram

```
┌──────────────────────┐         ┌───────────────────┐         ┌───────────────────────┐
│        users         │ 1     * │      trips        │ 1     * │      trip_days        │
│──────────────────────│         │───────────────────│         │───────────────────────│
│ id (PK)              │────────►│ id (PK)           │────────►│ id (PK)               │
│ name, email, login    │         │ title, description │         │ trip_id (FK)          │
│ username (unique)     │         │ startDate, endDate  │         │ dayNumber  [INTEGER]  │
│ birthdate  [LONG]     │         │ budget, spent [INT] │         │ date       [LONG]     │
│ address, country,     │         │ progress [REAL]     │         └───────────┬───────────┘
│   phone                │         │ user_id (FK)        │                     │ 1
│ acceptEmails [INT]    │         └──────────┬──────────┘                     │ *
│ profileInitials        │                    │ 1                  ┌───────────▼───────────┐
│ activeTripCount,        │                    │ *                  │    itinerary_items    │
│   countriesVisited      │         ┌──────────▼──────────┐         │───────────────────────│
└──────────────────────┘         │    reservations      │         │ id, trip_id, day_id   │
                                    │───────────────────────│         │ title, description    │
┌──────────────────────┐         │ id (PK), trip_id (FK) │         │ date [LONG]           │
│      access_log      │         │ hotelName, roomId      │         │ time [TEXT]           │
│──────────────────────│         │ checkInDate/OutDate    │         │ location, cost         │
│ id, userId            │         │ hotelImageUrl, roomImage│        └───────────────────────┘
│ datetime [LONG]        │         └───────────────────────┘
│ type ("IN"/"OUT")      │
└──────────────────────┘
```

### Key Tables

| Table | Purpose |
|---|---|
| `users` | Local mirror of the authenticated profile; Firebase handles credentials, this table holds display data |
| `trips` | User-owned trips, cascades to `trip_days` on delete |
| `trip_days` | Auto-generated from a trip's date range; holds a photo gallery per day |
| `itinerary_items` | Timed activities within a specific day |
| `reservations` | Local mirror of hotel bookings made through the remote API, optionally linked to a trip |
| `access_log` | Login/logout audit trail (`userId`, `datetime`, `type`) |

### Type Converters

Room cannot store `java.time` types natively, so `AppTypeConverters` (registered via `@ProvidedTypeConverter`) bridges:

| Kotlin type | SQLite column | Conversion |
|---|---|---|
| `LocalDate` | `INTEGER` | `date.toEpochDay()` ↔ `LocalDate.ofEpochDay(value)` |
| `LocalTime` | `TEXT` | `"HH:mm"` string ↔ `LocalTime.parse(...)` |
| `List<String>` | `TEXT` | comma-joined string ↔ split list (used for photo galleries) |

### Migration Strategy

During active development, `fallbackToDestructiveMigration()` drops and rebuilds the schema on version bumps — acceptable pre-release, since there is no production data to preserve. Before any real release, explicit `Migration` objects must replace this:

```kotlin
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE reservations (...)")
    }
}
```

## Dependency Injection (Hilt)

| Module | Provides |
|---|---|
| `DatabaseModule` | `AppDatabase`, all DAOs, `AppTypeConverters` |
| `NetworkModule` | `OkHttpClient`, `Retrofit`, `HotelApiService` |
| `FirebaseModule` | `FirebaseAuth` |
| `RepositoryModule` | `@Binds` mapping each repository interface to its implementation |

## Design Decisions

**Why Room over raw SQLite?**
Compile-time query verification, type-safe DAOs, and native `Flow` support — avoids manual cursor management entirely.

**Why `Flow` in every DAO?**
`Flow<List<T>>` lets Compose recompose automatically on database change; ViewModels expose these as `StateFlow` with zero manual refresh logic.

**Why mirror remote bookings into Room?**
Hotel data is fetched over REST, but a confirmed reservation is written locally immediately after success. This keeps the reservations list, and the "hotel reservation" badge on a trip, fully functional offline.

**Why client-side UUIDs for entity IDs?**
Avoids a network round-trip for ID assignment and supports offline-first creation of trips, days and activities.

**Why SharedPreferences for user preferences?**
Language and theme are small scalar values with no relational structure — SharedPreferences is the simplest correct tool, versus over-engineering with Room or DataStore for two booleans and a string.

## Testing

- **Instrumented tests** (`androidTest`) run all DAO CRUD paths against a Room **in-memory database**, isolated from the real app database.
- **Unit tests** (`test`) cover ViewModels and repository logic with MockK, mocking `android.util.Log` statically for JVM compatibility, and `kotlinx-coroutines-test` for coroutine-heavy flows (hotel search, booking confirmation, reservation linking).
