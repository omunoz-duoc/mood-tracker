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

## Upcoming Features

### Phase 1.4: Functional Login
**Planned Branch**: `feature/login-functionality`
- Connect UI with UserRepository
- Implement credential validation
- Add session persistence (DataStore)

### Phase 1.5: Complete Registration
**Planned Branch**: `feature/registration-flow`
- Implement full RegisterScreen
- Add registration validation
- Connect with database
