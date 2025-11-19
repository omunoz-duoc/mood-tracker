package cl.duoc.dsy1105.moodtracker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.dsy1105.moodtracker.data.local.AppDatabase
import cl.duoc.dsy1105.moodtracker.data.local.SessionManager
import cl.duoc.dsy1105.moodtracker.data.repository.UserRepository
import cl.duoc.dsy1105.moodtracker.ui.screens.HomeScreen
import cl.duoc.dsy1105.moodtracker.ui.screens.LoginScreen
import cl.duoc.dsy1105.moodtracker.ui.screens.RegisterScreen
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
}

@Composable
fun MoodTrackerNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val isLoggedIn by sessionManager.isLoggedInFlow.collectAsState(initial = false)
    val userId by sessionManager.userIdFlow.collectAsState(initial = null)
    val scope = rememberCoroutineScope()

    // Get user repository for loading user data
    val userRepository = remember {
        val database = AppDatabase.getDatabase(context)
        UserRepository(database.userDao())
    }

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegistrationSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                userEmail = "", // TODO: Load user email from userId
                onLogout = {
                    scope.launch {
                        sessionManager.clearSession()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                onTrackMood = {
                    // TODO: Navigate to mood tracking screen in Phase 2
                }
            )
        }
    }
}
