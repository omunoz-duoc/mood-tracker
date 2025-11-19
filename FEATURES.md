# Feature Changelog

This file tracks all features added to the Mood Tracker app, organized by phase and commit.

## Phase 1: Authentication

### feat: add login screen layout
**Branch**: `dev` (initial setup)
**Commit**: `63df7de`

**Files Added:**
- `app/src/main/java/cl/duoc/dsy1105/moodtracker/ui/screens/LoginScreen.kt` - Login screen UI with email/password fields and navigation
- `app/src/main/java/cl/duoc/dsy1105/moodtracker/ui/screens/RegisterScreen.kt` - Placeholder register screen
- `app/src/main/java/cl/duoc/dsy1105/moodtracker/navigation/Navigation.kt` - Navigation graph with Login and Register routes

**Files Modified:**
- `gradle/libs.versions.toml` - Added Navigation Compose dependency version
- `app/build.gradle.kts` - Added Navigation Compose implementation
- `app/src/main/java/cl/duoc/dsy1105/moodtracker/MainActivity.kt` - Updated to use navigation instead of greeting

**Description:**
Implemented basic login screen layout with email and password input fields, "Iniciar sesión" button, and "Crear cuenta" link that navigates to register screen. Set up Navigation Compose for app-wide navigation management.

---

### feat: add login form validation
**Branch**: `feature/login-validation`
**Status**: In Progress

**Files Added:**
- `app/src/main/java/cl/duoc/dsy1105/moodtracker/domain/validators/ValidationResult.kt` - Data classes for validation results
- `app/src/main/java/cl/duoc/dsy1105/moodtracker/domain/validators/LoginValidator.kt` - Login validation logic with email and password rules

**Files Modified:**
- `app/src/main/java/cl/duoc/dsy1105/moodtracker/ui/screens/LoginScreen.kt` - Integrated validation with error display, icons, and shake animation

**Description:**
Implemented form validation logic separated from UI components. Created `LoginValidator` with rules for email format validation (using Android Patterns) and password length validation (minimum 6 characters). Added visual feedback with error icons, error messages below fields, and shake animation when validation fails. Errors are cleared automatically when user starts typing.

**Validation Rules:**
- Email: Must not be empty and must match valid email pattern
- Password: Must not be empty and must be at least 6 characters long

---

### feat: configure Room database
**Branch**: `feature/room-database`
**Status**: Completed

**Files Added:**
- `app/src/main/java/cl/duoc/dsy1105/moodtracker/data/local/entities/User.kt` - User entity with id, email, passwordHash, createdAt
- `app/src/main/java/cl/duoc/dsy1105/moodtracker/data/local/dao/UserDao.kt` - Data Access Object with authentication queries
- `app/src/main/java/cl/duoc/dsy1105/moodtracker/data/local/AppDatabase.kt` - Room database singleton instance
- `app/src/main/java/cl/duoc/dsy1105/moodtracker/data/repository/UserRepository.kt` - Repository pattern for user management

**Files Modified:**
- `gradle/libs.versions.toml` - Added Room and KSP versions and library definitions
- `app/build.gradle.kts` - Added KSP plugin and Room dependencies

**Description:**
Configured Room database for local SQLite persistence. Created User entity with unique email constraint and auto-generated ID. Implemented UserDao with suspend functions for user registration, login, and queries. Set up AppDatabase singleton with fallback to destructive migration. Created UserRepository with password hashing (SHA-256) and clean API for user authentication operations.

**Database Schema:**
- **users** table: id (PK, auto-increment), email (unique), passwordHash, createdAt
- UserDao queries: insertUser, getUserByEmail, login, emailExists, getUserById, deleteAllUsers

**Features:**
- Password hashing with SHA-256 before storage
- Email uniqueness constraint at database level
- Suspend functions for coroutine support
- Repository pattern separating data layer from UI

---

### feat: add session management and login functionality
**Branch**: `feature/login-functionality`
**Status**: Completed

**Files Added:**
- `app/src/main/java/cl/duoc/dsy1105/moodtracker/data/local/SessionManager.kt` - DataStore-based session persistence
- `app/src/main/java/cl/duoc/dsy1105/moodtracker/ui/screens/HomeScreen.kt` - Home screen with user email display and logout
- `app/src/main/java/cl/duoc/dsy1105/moodtracker/ui/viewmodel/LoginViewModel.kt` - ViewModel for login business logic

**Files Modified:**
- `gradle/libs.versions.toml` - Added DataStore and ViewModel Compose dependencies
- `app/build.gradle.kts` - Added DataStore and ViewModel implementations
- `app/src/main/java/cl/duoc/dsy1105/moodtracker/ui/screens/LoginScreen.kt` - Integrated with LoginViewModel and added loading states
- `app/src/main/java/cl/duoc/dsy1105/moodtracker/navigation/Navigation.kt` - Added Home route and session-based navigation flow

**Description:**
Implemented functional login with session persistence using DataStore Preferences. Created SessionManager to handle user session with Flow-based reactive state. Developed LoginViewModel following MVVM pattern to connect UI with UserRepository. Added loading states, error handling, and automatic navigation on successful login. HomeScreen displays user email and provides logout functionality that clears session and navigates back to login.

**Features:**
- DataStore Preferences for session persistence
- SessionManager with Flow<Long?> for userId and isLoggedIn state
- LoginViewModel with StateFlow<LoginUiState>
- Loading indicators during login
- Error message display
- Auto-navigation on successful login
- Logout functionality with session clearing

---

### feat: add complete registration flow
**Branch**: `feature/registration-flow`
**Status**: Completed

**Files Added:**
- `app/src/main/java/cl/duoc/dsy1105/moodtracker/domain/validators/RegisterValidator.kt` - Registration validation with password confirmation
- `app/src/main/java/cl/duoc/dsy1105/moodtracker/ui/viewmodel/RegisterViewModel.kt` - ViewModel for registration logic

**Files Modified:**
- `app/src/main/java/cl/duoc/dsy1105/moodtracker/ui/screens/RegisterScreen.kt` - Complete registration form implementation

**Description:**
Implemented complete registration flow with form validation. Created RegisterValidator extending LoginValidator with password confirmation matching. Developed RegisterViewModel to handle user registration with UserRepository. Updated RegisterScreen with full form including email, password, and confirm password fields. Added shake animation for validation errors, loading states during registration, and automatic navigation back to login on successful registration.

**Validation Rules:**
- Email: Must be valid email format
- Password: Minimum 6 characters
- Confirm Password: Must match password field
- Email uniqueness: Validated at database level

**Features:**
- Three-field registration form (email, password, confirm password)
- Real-time validation with visual feedback
- Shake animation on validation errors
- Loading indicator during registration
- Error handling for duplicate emails
- Auto-navigation to login on success

---

## Phase 2: Mood Tracker Core

### feat: add mood tracking database schema
**Branch**: `feature/mood-selection`
**Commit**: `28c92c4`

**Files Added:**
- `app/src/main/java/cl/duoc/dsy1105/moodtracker/domain/model/MoodType.kt` - Enum with 7 mood types (Happy, Sad, Anxious, Calm, Angry, Excited, Tired)
- `app/src/main/java/cl/duoc/dsy1105/moodtracker/data/local/entities/MoodEntry.kt` - MoodEntry entity with foreign key to User
- `app/src/main/java/cl/duoc/dsy1105/moodtracker/data/local/dao/MoodDao.kt` - Data Access Object for mood entries
- `app/src/main/java/cl/duoc/dsy1105/moodtracker/data/repository/MoodRepository.kt` - Repository for mood entry management

**Files Modified:**
- `app/src/main/java/cl/duoc/dsy1105/moodtracker/data/local/AppDatabase.kt` - Updated to version 2, added MoodEntry entity

**Description:**
Created database schema for mood tracking. Implemented MoodType enum with 7 emotions, each with emoji, display name, and color. Created MoodEntry entity with foreign key relationship to User with CASCADE delete. Added indices on userId and date for query optimization. Implemented MoodDao with suspend functions for inserting and querying mood entries. Created MoodRepository following repository pattern.

**Database Schema:**
- **mood_entries** table: id (PK, auto-increment), userId (FK to users), moodType (String), note (nullable), date (timestamp)
- Foreign Key: userId CASCADE on delete
- Indices: userId, date

**Mood Types:**
- HAPPY (😊, Feliz, Gold)
- SAD (😢, Triste, Steel Blue)
- ANXIOUS (😰, Ansioso, Tomato)
- CALM (😌, Tranquilo, Pale Green)
- ANGRY (😠, Enojado, Crimson)
- EXCITED (🤩, Emocionado, Deep Pink)
- TIRED (😴, Cansado, Medium Purple)

---

### feat: add mood selection screen with haptic feedback
**Branch**: `feature/mood-selection`
**Commit**: `9444d86`

**Files Added:**
- `app/src/main/java/cl/duoc/dsy1105/moodtracker/ui/screens/MoodSelectionScreen.kt` - Mood selection UI with grid layout and animations

**Files Modified:**
- `app/src/main/AndroidManifest.xml` - Added VIBRATE permission

**Description:**
Created mood selection screen with LazyVerticalGrid displaying mood cards in 2-column layout. Implemented MoodCard component with spring scale animation on press. Added MoodNoteDialog for optional note input after mood selection. Integrated haptic feedback using Vibrator/VibratorManager APIs supporting both Android S+ and legacy versions. Each mood card displays emoji, display name, and color-coded background when selected.

**Features:**
- LazyVerticalGrid with 2 columns for mood cards
- Spring-based scale animation (DampingRatioMediumBouncy)
- Haptic feedback on mood selection (50ms vibration)
- Optional note input via dialog
- Color-coded mood cards with alpha transparency
- Support for Android S+ VibratorManager and legacy Vibrator API

**UI Components:**
- MoodSelectionScreen: Main composable with grid layout
- MoodCard: Individual mood card with animation
- MoodNoteDialog: AlertDialog for note input
- performHapticFeedback: Version-compatible vibration function

---

### feat: integrate mood tracking with navigation and session
**Branch**: `feature/mood-selection`
**Commit**: `804e6c6`

**Files Added:**
- `app/src/main/java/cl/duoc/dsy1105/moodtracker/ui/viewmodel/MoodViewModel.kt` - ViewModel for mood entry saving

**Files Modified:**
- `app/src/main/java/cl/duoc/dsy1105/moodtracker/navigation/Navigation.kt` - Added MoodSelection route and user email loading
- `app/src/main/java/cl/duoc/dsy1105/moodtracker/ui/screens/HomeScreen.kt` - Added onTrackMood callback parameter

**Description:**
Integrated mood tracking into navigation flow. Created MoodViewModel with state management for saving mood entries. Added MoodSelection route to navigation graph. Updated HomeScreen to show user email loaded from database using LaunchedEffect. Implemented auto-navigation back to HomeScreen on successful mood entry save. Connected MoodViewModel with SessionManager and MoodRepository for session-aware mood tracking.

**Features:**
- MoodViewModel with StateFlow<MoodUiState>
- Session-aware mood entry creation (userId from SessionManager)
- Auto-navigation on successful save
- User email display in HomeScreen via UserRepository
- Error handling with user-friendly Spanish messages
- Loading states during mood entry save

**Navigation Flow:**
1. User clicks "Track Mood" on HomeScreen
2. Navigate to MoodSelectionScreen
3. User selects mood and optionally adds note
4. MoodViewModel saves entry to database
5. Auto-navigate back to HomeScreen on success

---

## Upcoming Features

### Phase 3: Visualization
**Planned Features:**
- Mood history screen with list of past entries
- Calendar view showing mood trends
- Statistics and insights

### Phase 4: Native Resources
**Planned Features:**
- Local notifications for mood tracking reminders
- Notification scheduling and management

### Phase 5: Project Closure
**Planned Features:**
- Final documentation
- APK export for submission
- Project delivery
