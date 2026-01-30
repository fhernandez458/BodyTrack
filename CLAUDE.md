# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Install debug APK to connected device/emulator
./gradlew installDebug

# Build release APK
./gradlew assembleRelease

# Run unit tests
./gradlew testDebugUnitTest

# Run instrumented/Android tests
./gradlew connectedAndroidTest

# Clean build
./gradlew clean assembleDebug
```

## Architecture Overview

BodyTrack is an Android fitness tracking app built with Kotlin and Jetpack Compose following **MVVM + Repository pattern**.

### Layer Structure

```
Compose UI (Screens) → ViewModels (StateFlow) → Repository → Room DB / Retrofit API
```

### Key Components

**Dependency Injection (Koin)**
- `di/AppModules.kt` - Repository and ViewModel bindings
  - ViewModels are registered with `viewModelOf` (factory per `ViewModelStoreOwner`, NOT app-wide singletons). Each NavBackStackEntry gets its own ViewModel instance.
  - Repository is a singleton (`single<ExerciseRepository>`)
- `di/NetworkModule.kt` - Retrofit setup with RapidAPI interceptor
- `di/DatabaseModule.kt` - Room database singleton

**Navigation**
- `NavigationRoot.kt` - Single NavHost with parameterized routes:
  - `bodyListView` → `exerciseListPage/{bodyPart}` → `exercisePage/{exerciseId}`
- Navigation arguments are passed via route parameters (not shared ViewModel state)
- Data fetching is triggered by `LaunchedEffect` in destination composables using the navigation argument as the key
- `BodyPageListView` is a pure UI list of body parts — it does NOT hold a ViewModel. Data fetching for exercises happens in `ExerciseListRoot`.

**Data Layer**
- `data/ExerciseRepository.kt` - Interface abstracting API and local DB access
  - `getListOfExercisesForBodyPart(bodyPart, offset)` returns `ExercisesByBodyPartResponse?` (includes both `data` list and pagination `metadata`)
- `data/ExerciseDao.kt` - Room DAO with `@Transaction` methods for atomic operations
- `networking/ExerciseApi.kt` - Retrofit interface for ExerciseDB API

**API Integration**
- **STATUS: The ExerciseDB RapidAPI is no longer available.** Exercise search and retrieval endpoints are non-functional. A replacement API or local data source is needed.
- Base URL: `https://exercisedb-api1.p.rapidapi.com/`
- API key stored in `gradle.properties` (injected via `APIKeyInterceptor`)
- Main endpoints: `/api/v1/exercises/search` and `/exercises/{exerciseId}`
- Search endpoint supports pagination via `offset` and `limit` query parameters. Response includes `Metadata` with `currentPage`, `totalPages`, `totalExercises`.

### Data Flow Pattern

1. UI sends events to ViewModel (sealed class events, e.g., `BodyPageEvent`)
2. ViewModel processes events sequentially via a `Channel` → `receiveAsFlow().collect`
3. ViewModel calls Repository methods
4. Repository fetches from API or local DB
5. ViewModel exposes state via `StateFlow` (uses `_uiState.update { it.copy(...) }`)
6. Compose UI collects and renders state via `collectAsStateWithLifecycle()`

### Pagination

The exercise list supports infinite-scroll pagination:
- `BodyPageUiState` tracks `currentPage`, `hasMorePages`, and `isLoadingMore`
- `BodyPageEvent.OnLoadMoreExercises` appends the next page of results
- UI uses `snapshotFlow` on `LazyListState` to detect when the user scrolls near the end of the list

### Database Schema

- `exercise_data_table` - Workout session records
- `set_data_table` - Individual sets (weight, reps, FK to exercise_data)
- `movement_table` - Cached exercise definitions from API

## Tech Stack

- **UI**: Jetpack Compose with Material3
- **DI**: Koin 4.1.1
- **Networking**: Retrofit 3.0.0 + OkHttp 5.3.0 + Gson
- **Database**: Room 2.8.3
- **Images**: Coil 3.3.0
- **Async**: Kotlin Coroutines + StateFlow
- **Min SDK**: 29 (Android 10), **Target/Compile SDK**: 36

## Coding Guidelines

**Priority: Simple, readable, and efficient code over clever abstractions.**

- Prefer `val` over `var`
- Use trailing commas in multi-line declarations
- Prefer expression bodies for simple functions
- Use `when` exhaustively with sealed classes

**Compose best practices:**
- Keep Composables small and focused
- Use `LazyColumn` for lists (not `Column` with `forEach`)
- Use `key` parameter in `LazyColumn` items
- Use `LaunchedEffect` for side effects triggered by state changes
- Use `snapshotFlow` inside `LaunchedEffect` to observe Compose snapshot state (e.g., `LazyListState`)
- Do NOT call ViewModel event functions directly in the composable body — use `LaunchedEffect` to avoid infinite recomposition loops
- Collect flows with lifecycle awareness (`collectAsStateWithLifecycle`)

**Gson / Kotlin nullability:**
- Gson does not enforce Kotlin non-null types. Fields that may be null in API JSON responses should be declared nullable in data classes (e.g., `val nextPage: String?`), even if they are "expected" to always be present.
