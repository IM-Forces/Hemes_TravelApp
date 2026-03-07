# Sprint 01 - Planning Document

**Project:** Hermes Travel App  
**Sprint Duration:** Start of project - 01/03/2026  
**Team:** Ivan Gil Cañizares, Marco Beruet Morelli  

---

## Sprint Goal

Complete the foundational structure of Hermes Travel App, including:
- Project setup and configuration
- Navigation system implementation
- All main screen layouts with mock data
- Domain model definition
- Complete documentation

**Target:** Deliver v1.0.0 release by 01/03/2026 23:55

---

## Sprint Backlog

### T1. Project Setup (1 point)

| Task | Description | Assigned To |
|------|-------------|-------------|
| T1.1 | Define product name | Both |
| T1.2 | Generate app logo with AI | Ivan |
| T1.3 | Define Android version (API 26+) | Marco |
| T1.4 | Configure Kotlin version | Marco |
| T1.5 | Initialize Android Studio project | Marco |

---

### T2. Version Control (1 point)

| Task | Description | Assigned To |
|------|-------------|-------------|
| T2.1 | Create GitHub repository (public) | Marco |
| T2.2 | Initialize local Git repository | Marco |
| T2.3 | Add LICENSE file (Apache 2.0) | Ivan |
| T2.4 | Create CONTRIBUTING.md | Ivan |
| T2.5 | Create README.md | Both |
| T2.6 | Create /docs folder structure | Marco |
| T2.7 | Write design.md | Both |
| T2.8 | Setup branches (main) | Marco |
| T2.9 | Push initial code | Marco |
| T2.10 | Create Release v1.0.0 | Both |

---

### T3. Core Implementation (4 points)

#### Navigation & Structure (Marco)

| Task | Description |
|------|-------------|
| Setup Navigation Component | Configure Jetpack Navigation |
| Create NavGraph.kt | Root navigation structure |
| Implement Bottom Navigation | 5-tab navigation bar |
| Two-level navigation | Root + Bottom NavControllers |
| Navigation between screens | Connect all screens |

#### UI Screens - Authentication & Main (Ivan)

| Task | Description |
|------|-------------|
| SplashScreen | App entry with logo |
| LoginScreen | Login UI with form |
| RegisterScreen | Registration UI |
| HomeScreen | Dashboard layout |
| ExploreScreen | Explore destinations UI |

#### UI Screens - Trips & User (Marco)

| Task | Description |
|------|-------------|
| TripsScreen | Trip list layout |
| FavoritesScreen | Favorites layout |
| ProfileScreen | User profile UI |
| TripDetailScreen | Trip details layout |
| CreateTripScreen | New trip form |

#### UI Screens - Settings (Ivan)

| Task | Description |
|------|-------------|
| AboutScreen | Team info screen |
| PreferencesScreen | Settings UI |
| TermsScreen | Terms and conditions |

#### Theme & Design

| Task | Description | Assigned To |
|------|-------------|-------------|
| Define color palette | Material 3 colors | Both |
| Create Theme.kt | App theming | Marco |
| Typography setup | Font styles | Marco |
| Document colors | color-palette.md | Ivan |

#### Data Model

| Task | Description | Assigned To |
|------|-------------|-------------|
| Define User model | User data class | Ivan |
| Define Trip model | Trip data class | Ivan |
| Define ItineraryItem model | Activity data class | Marco |
| Define Location model | Location data class | Marco |
| Define Expense model | Expense data class | Ivan |
| Define Preferences model | Settings data class | Marco |
| Create domain model diagram | Visual representation | Both |

---

### T4. Customization (2 points)

| Task | Description | Assigned To |
|------|-------------|-------------|
| T4.1 | Splash Screen with logo | Ivan |
| T4.2 | About Page (team info) | Ivan |
| T4.3 | Terms & Conditions screen | Ivan |
| T4.4 | Preferences Screen (UI only) | Marco |

---

## Documentation Tasks

| Document | Description | Assigned To |
|----------|-------------|-------------|
| README.md | Project overview | Both |
| LICENSE | Apache 2.0 license | Ivan |
| CONTRIBUTING.md | Branching strategy | Ivan |
| design.md | Architecture decisions | Both |
| color-palette.md | Color documentation | Ivan |
| plan_sprint01.md | Sprint planning | Both |
| final_sprint01.md | Retrospective (after sprint) | Both |

---

## Task Distribution Summary

### Ivan's Responsibilities
**UI Screens (8 screens):**
- SplashScreen
- LoginScreen, RegisterScreen
- HomeScreen, ExploreScreen
- AboutScreen, PreferencesScreen, TermsScreen

**Other Tasks:**
- Project branding (logo, colors)
- GitHub documentation (LICENSE, CONTRIBUTING)
- Color palette documentation
- Data model: User, Trip, Expense

**Total estimated workload: ~50%**

### Marco's Responsibilities
**Navigation & Structure:**
- Complete navigation architecture
- Root NavController setup
- Bottom Navigation implementation
- Screen routing and connections

**UI Screens (5 screens):**
- TripsScreen, FavoritesScreen
- ProfileScreen
- TripDetailScreen, CreateTripScreen

**Other Tasks:**
- Project configuration and setup
- Git workflow and repository structure
- Theme and typography
- Data model: ItineraryItem, Location, Preferences

**Total estimated workload: ~50%**

### Shared Responsibilities
- Product naming and concept
- README and design documentation
- Domain model diagram
- Code reviews and testing
- Final release creation

---

## Definition of Done

A task is considered complete when:
- Code is written and functional
- No compilation errors
- Screen is accessible via navigation
- UI follows Material Design 3 guidelines
- Code is committed with descriptive message
- Changes are pushed to GitHub

---

## Risks & Mitigation

| Risk | Impact | Mitigation Strategy |
|------|--------|---------------------|
| Time constraint for all screens | High | Focus on layout first, polish later |
| Navigation complexity | Medium | Use simple Navigation Component patterns |
| Data model design | Medium | Keep it simple for Sprint 01, expand later |
| GitHub conflicts | Low | Communicate before pushing, work on different files |

---

## Sprint Timeline

- **Week 1:** Project setup, navigation structure, first screens
- **Week 2:** Complete all screens, navigation connections
- **Week 3:** Data model, documentation, testing
- **Final Days:** Polish, documentation review, create release

---

## Notes

- Sprint 01 focuses on UI and structure, no backend logic yet
- Authentication screens are UI-only, logic comes in Sprint 04
- Data model classes will have @TODO comments for future implementation
- All screens use mock/placeholder data for now
- Release v1.0.0 must be published before deadline

---

**Created:** Start of Sprint 01  
**Last Updated:** During Sprint 01  
**Next Review:** Sprint 01 Retrospective (final_sprint01.md)
