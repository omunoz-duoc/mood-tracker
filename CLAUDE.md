# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Mood Tracker is an Android mobile application for the DUOC UC Mobile Applications course (DSY1105). Built with Kotlin and Jetpack Compose, it follows modern Android development practices.

The app allows users to track their daily mood/emotions, persist data locally using Room (SQLite), and visualize their emotional history over time.

**Package namespace**: `cl.duoc.dsy1105.moodtracker`

### Core Features

1. **User Authentication**: ✓ Login and registration system with form validation and session persistence
2. **Mood Tracking**: ✓ Daily emotion selection (7 moods) with haptic feedback and optional notes
3. **Data Persistence**: ✓ SQLite database using Room for users and mood entries with cascade delete
4. **History Visualization**: ✓ Animated mood history screen with chronological entry display
5. **Native Resources**: ✓ Haptic feedback (vibration) and local notifications for daily reminders

## Build System & Commands

This project uses Gradle with Kotlin DSL for build configuration.

### Essential Commands

```bash
# Build the project
./gradlew build

# Clean build artifacts
./gradlew clean

# Assemble debug APK
./gradlew assembleDebug

# Assemble release APK
./gradlew assembleRelease

# Install debug APK on connected device/emulator
./gradlew installDebug

# Run unit tests
./gradlew test

# Run unit tests for a specific variant
./gradlew testDebugUnitTest

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Run specific test class
./gradlew test --tests "cl.duoc.dsy1105.moodtracker.ExampleUnitTest"

# Check for dependency updates
./gradlew dependencyUpdates

# Lint checks
./gradlew lint
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

## Architecture

### Technology Stack

- **Language**: Kotlin 2.0.21
- **UI Framework**: Jetpack Compose (Material 3)
- **Navigation**: Navigation Compose 2.8.5
- **Database**: Room 2.6.1 (SQLite wrapper) with KSP annotation processing
- **State Management**: DataStore Preferences, StateFlow, ViewModel
- **Min SDK**: 24 (Android 7.0)
- **Target/Compile SDK**: 36
- **Build Tool**: Gradle 8.13.1
- **Native Resources**: Vibration (Haptic Feedback), Local Notifications (AlarmManager)

### Project Structure

The codebase follows a modular architecture organized by layers:

```
app/src/main/java/cl/duoc/dsy1105/moodtracker/
├── MainActivity.kt                      # ✓ App entry point
├── ui/                                  # UI layer
│   ├── screens/                         # Screen composables
│   │   ├── LoginScreen.kt              # ✓ Login with validation
│   │   ├── RegisterScreen.kt           # ✓ Registration with confirmation
│   │   ├── HomeScreen.kt               # ✓ Home with notifications toggle
│   │   ├── MoodSelectionScreen.kt      # ✓ Mood selection with haptics
│   │   └── HistoryScreen.kt            # ✓ Animated mood history
│   ├── viewmodel/                       # ViewModels
│   │   ├── LoginViewModel.kt           # ✓ Login state management
│   │   ├── RegisterViewModel.kt        # ✓ Registration state
│   │   ├── MoodViewModel.kt            # ✓ Mood tracking state
│   │   └── HistoryViewModel.kt         # ✓ History data loading
│   └── theme/                           # Material3 theme configuration
│       ├── Theme.kt
│       ├── Color.kt
│       └── Type.kt
├── domain/                              # Business logic
│   ├── model/                           # Domain models
│   │   └── MoodType.kt                 # ✓ Enum with 7 emotions
│   └── validators/                      # Form validation logic
│       ├── ValidationResult.kt         # ✓ Validation data classes
│       ├── LoginValidator.kt           # ✓ Email/password validation
│       └── RegisterValidator.kt        # ✓ Registration validation
├── data/                                # Data layer
│   ├── local/                           # Local data sources
│   │   ├── entities/                    # Database entities
│   │   │   ├── User.kt                 # ✓ User with unique email
│   │   │   └── MoodEntry.kt            # ✓ Mood entry with FK
│   │   ├── dao/                         # Data Access Objects
│   │   │   ├── UserDao.kt              # ✓ User CRUD operations
│   │   │   └── MoodDao.kt              # ✓ Mood entry operations
│   │   ├── AppDatabase.kt              # ✓ Room database (v2)
│   │   ├── SessionManager.kt           # ✓ DataStore session
│   │   └── NotificationPreferences.kt  # ✓ Notification settings
│   └── repository/                      # Repository pattern
│       ├── UserRepository.kt           # ✓ User auth with SHA-256
│       └── MoodRepository.kt           # ✓ Mood tracking
├── notifications/                       # Notification system
│   ├── NotificationHelper.kt           # ✓ Notification management
│   └── NotificationReceiver.kt         # ✓ Alarm broadcast receiver
└── navigation/                          # Navigation graph
    └── Navigation.kt                    # ✓ Complete navigation flow
```

### Key Configuration Files

- **gradle/libs.versions.toml**: Centralized version catalog for all dependencies
- **app/build.gradle.kts**: Module-level build configuration
- **build.gradle.kts**: Root project build configuration

### Dependency Management

The project uses Gradle version catalogs (`libs.versions.toml`) for centralized dependency management. All dependencies are accessed via `libs.*` references.

Key dependencies:
- AndroidX Core KTX, Lifecycle Runtime
- Activity Compose for Compose integration
- Compose BOM (Bill of Materials) for consistent Compose versions
- Material3 for modern UI components
- **Navigation Compose 2.8.5**: For screen navigation
- **Room 2.6.1**: SQLite database with KSP compile-time verification
- **DataStore Preferences 1.1.1**: Persistent key-value storage
- **ViewModel Compose**: State management and lifecycle awareness
- JUnit, Espresso for testing

### Compose Architecture

The app uses Jetpack Compose with:
- **Material3 Design System**: Modern Material Design theming
- **Dynamic Color Support**: Adapts to system theme on Android 12+
- **Edge-to-Edge Display**: Uses `enableEdgeToEdge()` for modern UI
- **Scaffold Pattern**: Standard layout structure with Compose

### Theme System

`MoodTrackerTheme` composable provides:
- Dark/light theme switching based on system preference
- Dynamic color support for Android 12+ devices
- Custom color schemes (Purple-based palette)
- Typography system via Material3

## Development Roadmap

All phases completed! The project followed structured development with feature branches and conventional commits.

### ✓ Phase 1: Authentication (Completed)
- **1.1**: Login screen layout with email/password fields
- **1.2**: Form validation with LoginValidator and visual feedback (shake animation)
- **1.3**: Room database with User entity, UserDao, and AppDatabase
- **1.4**: Functional login with DataStore session persistence and LoginViewModel
- **1.5**: Complete registration flow with RegisterValidator and password confirmation

### ✓ Phase 2: Mood Tracker Core (Completed)
- **2.1**: HomeScreen with user email, notification toggle, and navigation buttons
- **2.2**: MoodSelectionScreen with 7 emotions, haptic feedback, and scale animations
- **2.3**: Optional notes input via MoodNoteDialog
- **2.4**: MoodEntry entity with foreign key, MoodDao, and MoodRepository

### ✓ Phase 3: Visualization (Completed)
- **3.1**: HistoryScreen with LazyColumn displaying all mood entries
- **3.2**: Staggered entrance animations (slideInVertically + fadeIn)
- **3.3**: HistoryViewModel with Flow-based reactive data loading

### ✓ Phase 4: Native Resources (Completed)
- **4.1**: Local notifications with NotificationHelper and AlarmManager
- **4.2**: NotificationReceiver for scheduled daily reminders (8:00 PM)
- **4.3**: NotificationPreferences for persistent notification settings
- **Native resources**: Vibration (Phase 2) + Notifications (Phase 4)

### ✓ Phase 5: Project Closure (In Progress)
- Final documentation updates (CLAUDE.md, README.md)
- APK export for submission
- Project delivery preparation

## Development Guidelines

### Commit Convention

Use structured commit messages following the pattern from `instrucciones-especificas.md`:
- `feat: add login screen layout`
- `feat: add login form validation logic`
- `feat: configure Room database`
- `feat: add mood selection screen`
- `chore: update documentation`

### Validation Requirements

- **All validations must be in separate logic classes** (not in UI components)
- Use dedicated validator classes in `domain/validators/`
- Return validation results as objects with error messages and success flags
- Display errors with icons, text, and animations (e.g., shake animation)

### UI Requirements

- Clear, organized interfaces
- Functional animations (scale, shake, fade-in, etc.)
- Visual feedback for all interactions
- Material3 components and theming
- Use `@Preview` annotations for all composables

### Database Schema

Room database (version 2) with the following entities:

**User Table** (`users`):
- `id` (Long, PK, auto-increment)
- `email` (String, unique index)
- `passwordHash` (String, SHA-256)
- `createdAt` (Long, timestamp)

**MoodEntry Table** (`mood_entries`):
- `id` (Long, PK, auto-increment)
- `userId` (Long, FK to User with CASCADE delete)
- `moodType` (String, enum name)
- `note` (String?, optional)
- `date` (Long, timestamp)
- Indices on `userId` and `date` for query optimization

### Native Resources (Implemented)

1. **Haptic Feedback (Vibration)**:
   - 50ms vibration on mood selection
   - Version-compatible implementation (Android S+ VibratorManager, legacy Vibrator)
   - Vibration pattern in notification channel
   - Requires `VIBRATE` permission

2. **Local Notifications (AlarmManager)**:
   - Daily reminders at configurable time (default: 8:00 PM)
   - Repeating daily alarms with AlarmManager
   - Notification channel creation (Android O+)
   - Permission handling for Android 13+ (POST_NOTIFICATIONS)
   - Deep linking to open app when tapped
   - Requires `POST_NOTIFICATIONS`, `SCHEDULE_EXACT_ALARM`, `USE_EXACT_ALARM` permissions

### Testing

- Unit tests go in `app/src/test/java/` with same package structure
- Instrumented tests go in `app/src/androidTest/java/`
- Test validation logic thoroughly
- Use `testImplementation` for unit test dependencies
- Use `androidTestImplementation` for instrumented test dependencies

### Code Style

- Kotlin coding conventions apply
- Java 11 compatibility (`jvmTarget = "11"`)
- Use Compose for all UI (no XML layouts)
- Maintain modular architecture: separate UI, domain, and data layers
- Keep screens in `ui/screens/`, reusable components in `ui/components/`
- when adding a new feature, list the file changes as bullet points in a .md file to track which files and what feature was added
- The repository has 3 main branches: qa, prod and dev. We will work on dev, but when adding a new feature or working in something new you need to create a branch from dev (following the name convention feature/{my-feature}, e.g. feature/login-screen) and then merge this branch into dev
- keep updating @GUIA-PRESENTACION.md when making changes to the project