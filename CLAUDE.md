# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Mood Tracker is an Android mobile application for the DUOC UC Mobile Applications course (DSY1105). Built with Kotlin and Jetpack Compose, it follows modern Android development practices.

The app allows users to track their daily mood/emotions, persist data locally using Room (SQLite), and visualize their emotional history over time.

**Package namespace**: `cl.duoc.dsy1105.moodtracker`

### Core Features

1. **User Authentication**: Login and registration system with form validation
2. **Mood Tracking**: Daily emotion selection with visual feedback and notes
3. **Data Persistence**: SQLite database using Room for users and mood entries
4. **History Visualization**: View past mood entries with simple charts
5. **Native Resources**: Haptic feedback on interactions, local notifications for reminders

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
- **Navigation**: Navigation Compose
- **Database**: Room (SQLite wrapper) - to be added
- **Min SDK**: 24 (Android 7.0)
- **Target/Compile SDK**: 36
- **Build Tool**: Gradle 8.13.1
- **Native Resources**: Vibration (Haptic Feedback), Local Notifications - to be added

### Project Structure

The codebase follows a modular architecture organized by layers:

```
app/src/main/java/cl/duoc/dsy1105/moodtracker/
├── MainActivity.kt                      # App entry point
├── ui/                                  # UI layer
│   ├── screens/                         # Screen composables
│   │   ├── LoginScreen.kt              # ✓ Implemented
│   │   ├── RegisterScreen.kt           # ✓ Placeholder
│   │   ├── HomeScreen.kt               # Future: Phase 2
│   │   ├── MoodSelectionScreen.kt      # Future: Phase 2
│   │   └── HistoryScreen.kt            # Future: Phase 3
│   ├── components/                      # Reusable UI components (future)
│   └── theme/                           # Material3 theme configuration
│       ├── Theme.kt
│       ├── Color.kt
│       └── Type.kt
├── domain/                              # Business logic (future)
│   └── validators/                      # Form validation logic (Phase 1.2)
├── data/                                # Data layer (Phase 1.3+)
│   ├── local/                           # Room database
│   │   ├── entities/                    # User, MoodEntry
│   │   ├── dao/                         # Data Access Objects
│   │   └── AppDatabase.kt              # Room database instance
│   └── repository/                      # Repository pattern
│       ├── UserRepository.kt           # User auth & management
│       └── MoodRepository.kt           # Mood tracking
└── navigation/                          # Navigation graph
    └── Navigation.kt                    # ✓ Implemented
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
- **Navigation Compose**: For screen navigation
- **Room** (to be added): SQLite database with compile-time verification
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

The project is being developed in structured phases with specific commit conventions:

### Phase 1: Authentication (Current)
- **1.1**: Login screen layout (`feat: add login screen layout`) ✓
- **1.2**: Form validation logic with visual feedback and animations
- **1.3**: Room database setup with User entity and DAO
- **1.4**: Functional login with session persistence (DataStore or Session table)
- **1.5**: Complete registration flow

### Phase 2: Mood Tracker Core
- **2.1**: Home screen with greeting and CTA
- **2.2**: Mood selection screen with 5-7 emotions, haptic feedback, and animations
- **2.3**: Optional notes form with validation
- **2.4**: MoodEntry entity and database persistence

### Phase 3: Visualization
- **3.1**: History screen with mood entries list/charts
- **3.2**: Entry animations

### Phase 4: Native Resources
- **4.1**: Local notifications for daily mood tracking reminders

### Phase 5: Project Closure
- Final polish, documentation, and APK export

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

### Database Guidelines

When implementing Room (Phase 1.3+):
- Entities go in `data/local/entities/`
- DAOs go in `data/local/dao/`
- Database instance in `data/local/AppDatabase.kt`
- Repositories implement data access patterns in `data/repository/`
- Planned entities: `User(id, email, passwordHash)`, `MoodEntry(id, userId, moodType, date, note?)`

### Native Resources

- **Haptic Feedback**: Use Vibrator/VibratorManager for touch feedback on mood selection
- **Local Notifications**: WorkManager or AlarmManager for daily reminders

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