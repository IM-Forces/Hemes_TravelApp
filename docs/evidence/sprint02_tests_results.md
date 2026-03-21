# Sprint 02 — Test Results Documentation
**Project:** Hermes Travel App  
**Course:** 105025-2526 — Applications for Mobile Devices  
**Institution:** University of Lleida (Campus Igualada)  
**Professor:** Vítor da Silva Verbel  
**Version:** v2.0.0 
**Date:** 21/03/2026  

---

## Summary

All unit tests have been executed successfully on the Android Studio emulator (Pixel 9a). No code changes were required after testing. All validations, CRUD operations, and ViewModel logic behaved as expected on the first execution.

| Test Class | Tests Passed | Total Tests | Time |
|---|---|---|---|
| `FakeTripDataSourceTest` | 5 | 5 | 1 sec 190 ms |
| `FakeActivityDataSourceTest` | 6 | 6 | 1 sec 589 ms |
| `TripRepositoryImplTest` | 4 | 4 | 1 sec 426 ms |
| `TripViewModelTest` | 6 | 6 | 1 sec 833 ms |
| `TripListViewModelTest` | 5 | 5 | 1 sec 425 ms |
| `ActivityViewModelTest` | 4 | 4 | 1 sec 864 ms |
| `TripCrudLoggingTest` | 3 | 3 | 1 sec 390 ms |
| `ValidationTest` | 5 | 5 | 45 ms |
| **TOTAL** | **38** | **38** | — |

---

## 1. FakeTripDataSourceTest

**File:** `com.example.hermes_travelapp.data.fakeDB.FakeTripDataSourceTest`  
**Result:** 5 tests passed — 1 sec 190 ms  
**Description:** Verifies the in-memory CRUD operations for trips directly at the data source level. Uses `mockStatic` on the Android `Log` class to avoid `RuntimeException: Stub!` in JVM unit tests.

<img width="1920" height="1032" alt="FakeTripDataSourceTest" src="https://github.com/user-attachments/assets/316a9a0d-3de3-4bad-a748-92a5820d8b0d" />

---

## 2. FakeActivityDataSourceTest

**File:** `com.example.hermes_travelapp.data.fakeDB.FakeActivityDataSourceTest`  
**Result:** 6 tests passed — 1 sec 589 ms  
**Description:** Verifies the in-memory CRUD operations for itinerary activities at the data source level. Includes validation of activity dates against trip date range. Uses `mockStatic` on `Log` to prevent JVM runtime errors.

<img width="1920" height="1033" alt="FakeActivityDataSourceTest" src="https://github.com/user-attachments/assets/c2a62c68-eef1-44eb-a983-0fa539d05b94" />

---

## 3. TripRepositoryImplTest

**File:** `com.example.hermes_travelapp.data.repository.TripRepositoryImplTest`  
**Result:** 4 tests passed — 1 sec 426 ms  
**Description:** Verifies that `TripRepositoryImpl` correctly delegates CRUD operations to `FakeTripDataSource`. Uses `mockStatic` on the Android `Log` class for JVM compatibility.

<img width="1920" height="1037" alt="TripRepositoryImplTest" src="https://github.com/user-attachments/assets/26c7a868-28f5-4e0a-8a84-dd9e96f19c04" />

---

## 4. TripViewModelTest

**File:** `com.example.hermes_travelapp.ui.viewmodels.TripViewModelTest`  
**Result:** 6 tests passed — 1 sec 833 ms  
**Description:** Verifies the ViewModel logic for trip CRUD operations. Tests that `StateFlow` updates correctly after add, edit and delete operations, and that validation errors are correctly set in the error state. Uses `InstantTaskExecutorRule` for synchronous execution.

<img width="1920" height="997" alt="TripViewModelTest" src="https://github.com/user-attachments/assets/5889426c-5286-40f9-8033-347f63b9a723" />

---

## 5. TripListViewModelTest

**File:** `com.example.hermes_travelapp.ui.viewmodels.TripListViewModelTest`  
**Result:** 5 tests passed — 1 sec 425 ms  
**Description:** Verifies the trip list ViewModel using a `FakeTripRepository` implementation. Tests that the observable trip list state updates correctly for all CRUD operations. Uses `@OptIn(ExperimentalCoroutinesApi::class)` for coroutine support and `InstantTaskExecutorRule`.

<img width="1920" height="1028" alt="TripListViewModelTest" src="https://github.com/user-attachments/assets/f6ec02f6-93fd-4191-9b65-17c40894a5b9" />

---

## 6. ActivityViewModelTest

**File:** `com.example.hermes_travelapp.ui.viewmodels.ActivityViewModelTest`  
**Result:** 4 tests passed — 1 sec 864 ms  
**Description:** Verifies the ViewModel logic for activity/itinerary CRUD operations. Tests that activities are correctly added, updated and deleted, and that date range validation errors are exposed via the ViewModel error state. Uses `InstantTaskExecutorRule` and a fake `ActivityRepository`.

<img width="1920" height="1040" alt="ActivityViewModelTest" src="https://github.com/user-attachments/assets/0d51b537-4f58-44ac-bc66-6c1c0348bf6f" />

---

## 7. TripCrudLoggingTest

**File:** `com.example.hermes_travelapp.ui.viewmodels.TripCrudLoggingTest`  
**Result:** 3 tests passed — 1 sec 390 ms  
**Description:** Verifies that all CRUD operations produce the expected `Log` calls at the correct levels (DEBUG, INFO, ERROR). Uses `mockStatic` on the Android `Log` class to intercept and assert log output during operations on `TripRepositoryImpl` and `TripViewModel`.

<img width="1920" height="1030" alt="TripCrudLoggingTest" src="https://github.com/user-attachments/assets/b2fe1bf5-cfce-47ba-b5f4-96f9521d96f1" />

---

## 8. ValidationTest

**File:** `com.example.hermes_travelapp.ui.viewmodels.ValidationTest`  
**Result:** 5 tests passed — 45 ms  
**Description:** Verifies all date validation rules required by the sprint. Uses future dates via `LocalDate.now().plusYears(1)` to avoid triggering the "date in the past" validation error. Tests cover: start date before end date, invalid date order, activity within trip range, activity outside trip range, and empty required fields.

<img width="1920" height="1028" alt="ValidationTest" src="https://github.com/user-attachments/assets/b99cecff-b71f-4160-9271-de6b4cb42119" />

---

## Notes

- All tests were executed on Android Studio with emulator **Pixel 9a**.
- `mockStatic(Log::class)` was required in all test classes that interact with the Android `Log` framework, since the JVM test environment does not provide Android stubs.
- No code modifications were required after running the tests.
- All 38 tests passed on first execution without any failures or errors.
- `InstantTaskExecutorRule` was used in all ViewModel tests to handle synchronous execution of `LiveData` and `StateFlow`.

---

*Document generated for Sprint 02 delivery — Hermes Travel App v2.0.0*
