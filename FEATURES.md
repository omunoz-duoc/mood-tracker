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

## Upcoming Features

### Phase 1.2: Login Form Validation
**Planned Branch**: `feature/login-validation`
- Add `LoginValidator` class for form validation logic
- Implement validation rules (email format, password length ≥ 6)
- Add error display with icons and shake animation
- Visual feedback for invalid inputs

### Phase 1.3: Database Setup
**Planned Branch**: `feature/room-database`
- Configure Room database
- Create User entity
- Create UserDao
- Implement UserRepository

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
