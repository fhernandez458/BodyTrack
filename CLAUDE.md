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
- `di/AppModules.kt` - Repository and ViewModel bindings (ViewModels are singletons)
- `di/NetworkModule.kt` - Retrofit setup with RapidAPI interceptor
- `di/DatabaseModule.kt` - Room database singleton

**Navigation**
- `NavigationRoot.kt` - Single NavHost with parameterized routes:
  - `bodyListView` → `exerciseListPage/{bodyPart}` → `exercisePage/{exerciseId}`
- Navigation arguments are passed via route parameters (not shared ViewModel state)
- Data fetching is triggered by `LaunchedEffect` in destination composables using the navigation argument as the key

**Data Layer**
- `data/ExerciseRepository.kt` - Interface abstracting API and local DB access
- `data/ExerciseDao.kt` - Room DAO with `@Transaction` methods for atomic operations
- `networking/ExerciseApi.kt` - Retrofit interface for ExerciseDB API

**API Integration**
- Base URL: `https://exercisedb-api1.p.rapidapi.com/`
- API key stored in `gradle.properties` (injected via `APIKeyInterceptor`)
- Main endpoints: `/api/v1/exercises/search` and `/exercises/{exerciseId}`

### Data Flow Pattern

1. UI sends events to ViewModel (sealed class events, e.g., `BodyPageEvent`)
2. ViewModel calls Repository methods
3. Repository fetches from API or local DB
4. ViewModel exposes state via `StateFlow`
5. Compose UI collects and renders state

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