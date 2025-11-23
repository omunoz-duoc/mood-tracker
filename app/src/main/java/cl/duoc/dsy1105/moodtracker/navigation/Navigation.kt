package cl.duoc.dsy1105.moodtracker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.dsy1105.moodtracker.data.local.AppDatabase
import cl.duoc.dsy1105.moodtracker.data.local.NotificationPreferences
import cl.duoc.dsy1105.moodtracker.data.local.SessionManager
import cl.duoc.dsy1105.moodtracker.data.repository.UserRepository
import cl.duoc.dsy1105.moodtracker.notifications.NotificationHelper
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.dsy1105.moodtracker.ui.screens.HistoryScreen
import cl.duoc.dsy1105.moodtracker.ui.screens.HomeScreen
import cl.duoc.dsy1105.moodtracker.ui.screens.LoadingScreen
import cl.duoc.dsy1105.moodtracker.ui.screens.LoginScreen
import cl.duoc.dsy1105.moodtracker.ui.screens.MoodSelectionScreen
import cl.duoc.dsy1105.moodtracker.ui.screens.RegisterScreen
import cl.duoc.dsy1105.moodtracker.ui.viewmodel.HistoryViewModel
import cl.duoc.dsy1105.moodtracker.ui.viewmodel.MoodViewModel
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Loading : Screen("loading")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object MoodSelection : Screen("mood_selection")
    object History : Screen("history")
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

    // Notification management
    val notificationHelper = remember { NotificationHelper(context) }
    val notificationPreferences = remember { NotificationPreferences(context) }
    val notificationsEnabled by notificationPreferences.notificationsEnabledFlow.collectAsState(initial = false)

    NavHost(
        navController = navController,
        startDestination = Screen.Loading.route
    ) {
        composable(Screen.Loading.route) {
            LoadingScreen(
                onNavigateToNext = {
                    val destination = if (isLoggedIn) Screen.Home.route else Screen.Login.route
                    navController.navigate(destination) {
                        popUpTo(Screen.Loading.route) { inclusive = true }
                    }
                }
            )
        }

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
            var userEmail by remember { mutableStateOf("") }

            LaunchedEffect(userId) {
                userId?.let { id ->
                    val user = userRepository.getUserById(id)
                    userEmail = user?.email ?: ""
                }
            }

            HomeScreen(
                userEmail = userEmail,
                notificationsEnabled = notificationsEnabled,
                onLogout = {
                    scope.launch {
                        sessionManager.clearSession()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                onTrackMood = {
                    navController.navigate(Screen.MoodSelection.route)
                },
                onViewHistory = {
                    navController.navigate(Screen.History.route)
                },
                onToggleNotifications = { enabled ->
                    scope.launch {
                        notificationPreferences.setNotificationsEnabled(enabled)
                        if (enabled) {
                            // Schedule daily notification at 8:00 PM
                            notificationHelper.scheduleDailyNotification(20, 0)
                        } else {
                            notificationHelper.cancelDailyNotification()
                        }
                    }
                }
            )
        }

        composable(Screen.MoodSelection.route) {
            val moodViewModel: MoodViewModel = viewModel { MoodViewModel(context) }
            val uiState by moodViewModel.uiState.collectAsState()

            LaunchedEffect(uiState.isSaveSuccessful) {
                if (uiState.isSaveSuccessful) {
                    moodViewModel.resetSaveSuccess()
                    navController.popBackStack()
                }
            }

            MoodSelectionScreen(
                onMoodSelected = { moodType, note ->
                    moodViewModel.saveMoodEntry(moodType, note)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.History.route) {
            val historyViewModel: HistoryViewModel = viewModel { HistoryViewModel(context) }
            val uiState by historyViewModel.uiState.collectAsState()

            HistoryScreen(
                moodEntries = uiState.moodEntries,
                isLoading = uiState.isLoading,
                errorMessage = uiState.errorMessage,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
