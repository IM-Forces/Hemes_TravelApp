# Changelog

All notable changes to this project are documented in this file.
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [4.0.0] — Hotel Booking & Media

### Added
- Retrofit-based integration with a REST hotel booking API (search, availability, booking, cancellation)
- Hotel search screen with city, date range, price and star-rating filters
- Hotel list and detail screens with Coil-powered image loading
- Local `ReservationEntity` mirroring confirmed bookings, automatically linked to a trip
- Reservations screen: list, cancel, and re-link reservations to trips
- Per-trip photo gallery: camera/gallery picker, cover photo selection, full-screen lightbox viewer
- "Has reservation" indicator on trip cards

## [3.0.0] — Persistence & Authentication

### Added
- Full Room database replacing all in-memory storage (Trips, TripDays, ItineraryItems, Users, AccessLog)
- Firebase Authentication: sign-in, sign-up with email verification, and password recovery
- Multi-user support — trips are scoped and filtered by the authenticated user
- Login/logout access-log auditing
- Instrumented DAO test suite running against an in-memory database

## [2.0.0] — Trip & Itinerary Logic

### Added
- Full CRUD for trips and itinerary activities (in-memory)
- Field- and date-range validation (start < end, activities within trip range)
- Persistent user settings via SharedPreferences (username, date of birth, dark mode, language)
- Multi-language support: English, Spanish, Catalan
- Unit test suite for CRUD operations and validation logic

## [1.0.0] — Foundation

### Added
- Two-level navigation architecture (root + bottom tabs)
- All primary screens scaffolded with Jetpack Compose and Material Design 3
- Domain model definitions (`User`, `Trip`, `ItineraryItem`, `FavoritePlace`, `Preferences`)
- Custom "Hermes" visual identity and color palette
