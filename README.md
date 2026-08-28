<div align="center">

<img src=".github/assets/logofinal.png" alt="Hermes Travel App logo" width="140"/>

# Hermes Travel App

**A native Android trip-planning app with real hotel booking, built on a fully offline-first, reactive architecture.**

[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM-orange)](docs/ARCHITECTURE.md)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-26-brightgreen)](hermes_travelapp/app/build.gradle.kts)

[Features](#features) · [Architecture](#architecture) · [Tech Stack](#tech-stack) · [Getting Started](#getting-started) · [Screenshots](#screenshots)

</div>

---

## Overview

Hermes is a full-featured Android travel companion: users plan multi-day trips, build day-by-day itineraries, search and book real hotel rooms through a REST API, manage a photo gallery per trip, and keep everything in sync locally for offline use.

It was built to demonstrate a **production-style Android codebase**: clean MVVM layering, a Repository pattern in front of both a local Room database and a remote REST API, dependency injection with Hilt, reactive state with Coroutines/Flow, and an instrumented test suite that runs against an in-memory database.

## Features

- 🔐 **Authentication** — email/password sign-up, sign-in, password recovery and email verification via Firebase Auth
- 🗺️ **Trip planning** — create, edit and delete trips with automatic day-by-day itinerary generation
- 📅 **Itinerary management** — add, edit and delete timed activities per day, with budget tracking
- 🏨 **Hotel search & booking** — live search against a REST hotel API (city, dates, price, rating filters), room selection and booking, automatically linked back to a trip
- 📖 **Reservation management** — list, cancel, and re-link existing hotel reservations
- 🖼️ **Photo gallery** — attach camera or gallery photos to each trip day, set a cover photo, full-screen lightbox viewer
- 🌐 **Multi-language** — English, Spanish and Catalan, switchable at runtime
- 🌓 **Light / dark theme**, persisted user preferences
- 🧪 **Offline-first persistence** — every remote booking is mirrored into a local Room database so the UI never blocks on network state

## Architecture

Hermes follows **MVVM with a Repository layer**, splitting local (Room) and remote (Retrofit) data sources behind a single domain-facing interface per feature.

```
UI (Jetpack Compose)
   ↓ observes StateFlow
ViewModel (Hilt-injected)
   ↓ calls
Repository interface  ──►  RepositoryImpl
                              ├── Room DAO (local persistence)
                              └── Retrofit API service (remote hotel data)
```

Full write-up — database schema, DI graph, navigation structure, and design rationale — lives in **[`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md)**.

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material Design 3 |
| Architecture | MVVM + Repository pattern |
| Dependency Injection | Hilt |
| Local persistence | Room (SQLite), SharedPreferences |
| Networking | Retrofit + OkHttp (Gson) |
| Async | Kotlin Coroutines + Flow |
| Auth | Firebase Authentication |
| Image loading | Coil |
| Testing | JUnit4, MockK, Room in-memory DB, `kotlinx-coroutines-test` |

## Getting Started

### Prerequisites

- Android Studio (latest stable)
- JDK 17
- An Android device or emulator running **API 26+**

### Setup

```bash
git clone https://github.com/<your-username>/hermes-travel-app.git
cd hermes-travel-app
```

1. Add your own `google-services.json` to `hermes_travelapp/app/` (required for Firebase Auth — see [Firebase Console](https://console.firebase.google.com)).
2. Open the project in Android Studio and let Gradle sync.
3. Run the `app` configuration on an emulator or physical device.

> The hotel booking API base URL is configured in [`NetworkModule.kt`](hermes_travelapp/app/src/main/java/com/example/hermes_travelapp/di/NetworkModule.kt).

### Running tests

```bash
# Unit tests (JVM)
./gradlew test

# Instrumented DAO tests (requires emulator/device)
./gradlew connectedAndroidTest
```

## Screenshots

<div align="center">
<!-- Replace with real screenshots/GIFs in .github/assets -->
<img src=".github/assets/screenshot_home.png" width="200"/>
<img src=".github/assets/screenshot_hotel_search.png" width="200"/>
<img src=".github/assets/screenshot_trip.png" width="200"/>
</div>

## Roadmap

- [ ] Interactive maps for trip destinations
- [ ] PDF document upload and storage per trip
- [ ] Push notifications for upcoming reservations
- [ ] CI pipeline (GitHub Actions) running lint + unit tests on PRs

See [`CHANGELOG.md`](CHANGELOG.md) for released version history.

## Contributing

Contributions are welcome. Please read [`CONTRIBUTING.md`](CONTRIBUTING.md) for the branching strategy and commit conventions before opening a PR.

## License

Distributed under the Apache License 2.0. See [`LICENSE`](LICENSE) for details.

## Author

**Ivan Gil Cañizares**
[GitHub](https://github.com/<your-username>) · [LinkedIn](https://linkedin.com/in/<your-profile>)

