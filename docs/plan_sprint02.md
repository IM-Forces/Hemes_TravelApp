# Sprint 02 - Planning Document

**Project:** Hermes Travel App  
**Sprint Duration:** 02/03/2026 - 22/03/2026  
**Team:** Ivan Gil Cañizares, Marco Beruet Morelli  
**Delivery Deadline:** 22/03/2026 23:55

---

## Sprint Goal

Implement the core business logic for Hermes Travel App, including:
- CRUD operations for Trips and Activities (in-memory)
- MVVM architecture implementation
- Data validation and error handling
- User settings with SharedPreferences
- Multi-language support (EN, ES, CA)
- Unit testing for critical operations

**Target:** Deliver v2.0.0 release with functional trip management logic

---

## Sprint Backlog

### T1. Travel Management Logic (4 points)

#### T1.1 - Trip CRUD Operations (in-memory)

| Task | Description | Assigned To |
|------|-------------|-------------|
| Create Trip data model | Define Trip.kt with required fields | Ivan |
| Implement TripRepository interface | Define CRUD contract | Marco |
| Create FakeTripDataSource | In-memory storage with mutableList | Marco |
| Implement TripRepositoryImpl | Repository implementation | Marco |
| Create TripViewModel | Handle UI state and business logic | Ivan |
| Implement addTrip() | Create new trip with validation | Ivan |
| Implement editTrip() | Update existing trip | Ivan |
| Implement deleteTrip() | Remove trip from list | Marco |
| Connect UI to ViewModel | Wire up TripsScreen | Both |

**Trip Model Requirements:**
- title (String)
- startDate (String) - format: dd/MM/YYYY
- endDate (String) - format: dd/MM/YYYY
- description (String)

#### T1.2 - Activity CRUD Operations (in-memory)

| Task | Description | Assigned To |
|------|-------------|-------------|
| Create Activity data model | Define ItineraryItem.kt | Ivan |
| Implement ActivityRepository interface | Define CRUD contract | Marco |
| Create FakeActivityDataSource | In-memory storage | Marco |
| Implement ActivityRepositoryImpl | Repository implementation | Marco |
| Create ActivityViewModel | Handle activity state | Ivan |
| Implement addActivity() | Add activity to trip | Ivan |
| Implement updateActivity() | Edit existing activity | Marco |
| Implement deleteActivity() | Remove activity | Marco |
| Connect UI to ViewModel | Wire up TripDetailScreen | Both |

**Activity Model Requirements:**
- title (String)
- description (String)
- date (LocalDate)
- time (LocalTime)

#### T1.3 - Data Validation

| Task | Description | Assigned To |
|------|-------------|-------------|
| Implement DatePicker components | For all date fields | Marco |
| Validate trip dates | startDate < endDate | Ivan |
| Validate activity dates | Within trip range | Ivan |
| Validate required fields | Non-empty validation | Both |
| Add error messages to UI | Clear feedback for users | Ivan |
| Block invalid inputs | Prevent bad data entry | Marco |

**Validation Rules:**
- Dates must use DatePicker (no free text)
- Trip start date must be before end date
- Activities must be within trip date range
- All required fields must be filled

#### T1.4 - User Settings with SharedPreferences

| Task | Description | Assigned To |
|------|-------------|-------------|
| Create Preferences data model | User settings structure | Ivan |
| Implement PreferencesManager | SharedPreferences wrapper | Marco |
| Save username | Store and retrieve | Marco |
| Save date of birth | Store as text | Marco |
| Save dark mode preference | Boolean setting | Ivan |
| Save language preference | EN/ES/CA | Ivan |
| Load preferences on app start | Restore saved settings | Marco |
| Connect to PreferencesScreen | Update UI based on settings | Ivan |

**Required Settings:**
- username (String)
- dateOfBirth (String)
- darkMode (Boolean)
- language (String: "en", "es", "ca")

#### T1.5 - Multi-language Support

| Task | Description | Assigned To |
|------|-------------|-------------|
| Create strings.xml (English) | Base language | Ivan |
| Create strings.xml (Spanish) | ES translation | Marco |
| Create strings.xml (Catalan) | CA translation | Ivan |
| Implement language switching | Runtime language change | Marco |
| Test all screens in 3 languages | Verify translations | Both |

---

### T2. Itinerary Flow Design (3 points)

#### T2.1 - Architecture Structure

| Task | Description | Assigned To |
|------|-------------|-------------|
| Define navigation flow | Menu → Trips → Trip Detail → Itinerary | Marco |
| Implement MVVM layers | UI → ViewModel → Repository → DataSource | Both |
| Create repository interfaces | Contracts for data access | Marco |
| Implement ViewModels | Business logic layer | Ivan |

#### T2.2 - UI Flow Implementation

| Task | Description | Assigned To |
|------|-------------|-------------|
| Update TripsScreen | List all trips with CRUD | Ivan |
| Update TripDetailScreen | Show trip info + activities | Marco |
| Create AddTripScreen | Form for new trip | Ivan |
| Create EditTripScreen | Form for editing trip | Ivan |
| Create AddActivityScreen | Form for new activity | Marco |
| Create EditActivityScreen | Form for editing activity | Marco |

#### T2.3 - Dynamic Updates

| Task | Description | Assigned To |
|------|-------------|-------------|
| Implement StateFlow in ViewModels | Reactive state management | Ivan |
| Update trip list dynamically | Reflect changes immediately | Ivan |
| Update activity list dynamically | Real-time UI updates | Marco |
| Handle loading states | Show progress indicators | Both |
| Handle error states | Display error messages | Both |

---

### T3. Validation and Testing (3 points)

#### T3.1 - Input Validation

| Task | Description | Assigned To |
|------|-------------|-------------|
| Implement ViewModel validation | Business logic checks | Ivan |
| Implement UI validation | Field-level checks | Marco |
| Show error messages for empty fields | Clear feedback | Ivan |
| Show error messages for invalid dates | Date-specific errors | Marco |
| Show error for activities outside trip range | Validation feedback | Ivan |

#### T3.2 - Unit Testing

| Task | Description | Assigned To |
|------|-------------|-------------|
| Write tests for Trip CRUD | Test repository operations | Marco |
| Write tests for Activity CRUD | Test repository operations | Ivan |
| Write tests for validation logic | Edge cases and constraints | Both |
| Write tests for date validation | Boundary conditions | Marco |
| Achieve >70% code coverage | Test critical paths | Both |

#### T3.3 - User Interaction Simulation

| Task | Description | Assigned To |
|------|-------------|-------------|
| Test complete user flows | End-to-end scenarios | Both |
| Log all CRUD operations | Use Logcat for debugging | Both |
| Log validation errors | Track error scenarios | Marco |
| Test rotation and state preservation | ViewModel state | Ivan |

#### T3.4 - Documentation Updates

| Task | Description | Assigned To |
|------|-------------|-------------|
| Document test results | Record findings | Both |
| Document bugs fixed | Track resolutions | Both |
| Update design.md | Reflect architecture changes | Both |
| Create final_sprint02.md | Retrospective document | Both |

#### T3.5 - Logging and Comments

| Task | Description | Assigned To |
|------|-------------|-------------|
| Add DEBUG logs for operations | Track app behavior | Marco |
| Add ERROR logs for failures | Catch exceptions | Marco |
| Add code comments | Explain complex logic | Both |
| Follow Kotlin conventions | Clean code practices | Both |

---

## Deliverables

| Deliverable | Description | Assigned To |
|-------------|-------------|-------------|
| Release v2.0.0 | GitHub release with tag | Both |
| Demo video | Screen recording showing features | Ivan |
| plan_sprint02.md | This planning document | Both |
| final_sprint02.md | Sprint retrospective | Both |
| Unit test report | Test coverage results | Marco |

**Video Requirements:**
- Location: `/docs/evidence/v2.0.0/demo.mp4`
- Show all implemented tasks
- Use Android Studio emulator recording or phone recording

---

## Task Distribution Summary

### Ivan's Responsibilities (50%)
**Data Models & ViewModels:**
- Trip.kt data model
- ItineraryItem.kt data model
- TripViewModel implementation
- ActivityViewModel implementation

**CRUD Operations:**
- addTrip(), editTrip()
- addActivity()
- Validation logic in ViewModels

**UI Screens:**
- AddTripScreen, EditTripScreen
- Update TripsScreen with CRUD
- Error message displays
- PreferencesScreen integration

**Settings & Localization:**
- Dark mode toggle
- Language switcher
- English strings.xml
- Catalan strings.xml

**Testing:**
- Activity CRUD tests
- Validation tests

### Marco's Responsibilities (50%)
**Architecture & Repository:**
- TripRepository interface
- ActivityRepository interface
- TripRepositoryImpl
- ActivityRepositoryImpl
- FakeTripDataSource
- FakeActivityDataSource

**CRUD Operations:**
- deleteTrip()
- updateActivity(), deleteActivity()

**UI Components:**
- DatePicker implementation
- TripDetailScreen updates
- AddActivityScreen, EditActivityScreen
- Loading and error states

**Persistence:**
- PreferencesManager (SharedPreferences)
- Save/load all settings
- Preferences on app start

**Localization:**
- Spanish strings.xml
- Language switching logic

**Testing & Logging:**
- Trip CRUD tests
- Date validation tests
- Logcat implementation
- Test documentation

### Shared Responsibilities
- Navigation flow design
- MVVM architecture implementation
- Dynamic UI updates (StateFlow)
- End-to-end testing
- Documentation (design.md, sprint docs)
- Code reviews
- Bug fixing
- Video recording

---

## Architecture Diagram

```
┌─────────────────────────────────────────┐
│              UI Layer                   │
│  (TripsScreen, TripDetailScreen, etc.)  │
└──────────────┬──────────────────────────┘
               │ Observes State
               ▼
┌─────────────────────────────────────────┐
│          ViewModel Layer                │
│   (TripViewModel, ActivityViewModel)    │
│         (Business Logic)                │
└──────────────┬──────────────────────────┘
               │ Calls Methods
               ▼
┌─────────────────────────────────────────┐
│        Repository Interface             │
│   (TripRepository, ActivityRepository)  │
└──────────────┬──────────────────────────┘
               │ Implements
               ▼
┌─────────────────────────────────────────┐
│      Repository Implementation          │
│  (TripRepositoryImpl, ActivityRepoImpl) │
└──────────────┬──────────────────────────┘
               │ Uses
               ▼
┌─────────────────────────────────────────┐
│          Data Source (InMemory)         │
│      (FakeTripDataSource, etc.)         │
│       mutableListOf<Trip>()             │
└─────────────────────────────────────────┘
```

---

## Definition of Done

A task is considered complete when:
- Code is written following MVVM pattern
- ViewModel manages UI state correctly
- Data validation is implemented and working
- UI shows clear error messages
- State is preserved on rotation
- Unit tests are written (where applicable)
- Code includes appropriate logging
- Code is documented with comments
- Changes are committed with descriptive messages
- Changes are pushed to GitHub
- Feature works in all 3 languages

---

## Risks & Mitigation

| Risk | Impact | Mitigation Strategy |
|------|--------|---------------------|
| ViewModel state loss on rotation | High | Use StateFlow and proper ViewModel lifecycle |
| Complex date validation logic | Medium | Use LocalDate/LocalTime, write comprehensive tests |
| SharedPreferences not persisting | Medium | Test app restart scenarios thoroughly |
| Translation inconsistencies | Low | Review all strings together, use placeholders |
| Testing time constraints | Medium | Focus on critical CRUD operations first |
| InMemory data structure complexity | Medium | Keep it simple with mutableListOf |

---

## Sprint Timeline

**Week 1 (02-08 Mar):**
- Set up architecture (Repository, ViewModel)
- Implement Trip CRUD (in-memory)
- Create data models

**Week 2 (09-15 Mar):**
- Implement Activity CRUD
- Add validation logic
- Implement SharedPreferences
- Start multi-language support

**Week 3 (16-22 Mar):**
- Complete all UI screens
- Write unit tests
- Fix bugs and polish
- Create documentation and video
- Final testing
- Create release v2.0.0

---

## Notes

- Sprint 02 focuses on **logic**, not design/polish
- All data is **in-memory** (no database yet)
- **MVVM pattern** is mandatory
- **DatePicker** must be used for all dates
- **SharedPreferences** for user settings only
- Unit tests should cover **CRUD operations** and **validation**
- Video demo is **required** for submission

---

**Created:** Start of Sprint 02  
**Last Updated:** Planning Phase  
**Next Review:** Sprint 02 Retrospective (final_sprint02.md)
