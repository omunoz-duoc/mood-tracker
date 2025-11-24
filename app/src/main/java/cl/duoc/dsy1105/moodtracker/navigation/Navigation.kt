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
import cl.duoc.dsy1105.moodtracker.ui.screens.AddDetailsScreen
import cl.duoc.dsy1105.moodtracker.ui.screens.EmailLoginScreen
import cl.duoc.dsy1105.moodtracker.ui.screens.HistoryScreen
import cl.duoc.dsy1105.moodtracker.ui.screens.HomeScreen
import cl.duoc.dsy1105.moodtracker.ui.screens.LoadingScreen
import cl.duoc.dsy1105.moodtracker.ui.screens.MoodSelectionScreen
import cl.duoc.dsy1105.moodtracker.ui.screens.OnboardingScreen
import cl.duoc.dsy1105.moodtracker.ui.screens.RegisterScreen
import cl.duoc.dsy1105.moodtracker.ui.screens.WelcomeScreen
import cl.duoc.dsy1105.moodtracker.ui.viewmodel.HistoryViewModel
import cl.duoc.dsy1105.moodtracker.ui.viewmodel.MoodViewModel
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Loading : Screen("loading")
    object Onboarding : Screen("onboarding")
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object MoodSelection : Screen("mood_selection")
    object History : Screen("history")
    object AddDetails : Screen("add_details")
}

@Composable
fun MoodTrackerNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val isLoggedIn by sessionManager.isLoggedInFlow.collectAsState(initial = false)
    val onboardingCompleted by sessionManager.onboardingCompletedFlow.collectAsState(initial = false)
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
                    val destination = when {
                        !onboardingCompleted -> Screen.Onboarding.route
                        isLoggedIn -> Screen.Home.route
                        else -> Screen.Welcome.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Loading.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    scope.launch {
                        sessionManager.setOnboardingCompleted()
                        val destination = if (isLoggedIn) Screen.Home.route else Screen.Welcome.route
                        navController.navigate(destination) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onSocialLogin = { provider ->
                    // TODO: Implement social login for provider
                }
            )
        }

        composable(Screen.Login.route) {
            EmailLoginScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onForgotPassword = {
                    // TODO: Navigate to forgot password screen
                },
                onSocialLogin = { provider ->
                    // TODO: Implement social login for provider
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onRegistrationSuccess = {
                    navController.popBackStack()
                },
                onSocialRegister = { provider ->
                    // TODO: Implement social registration for provider
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
                        navController.navigate(Screen.Welcome.route) {
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
                },
                onMoodSelected = { moodType ->
                    navController.navigate(Screen.MoodSelection.route)
                },
                onSearchClick = {
                    // TODO: Navigate to search screen
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

        composable(Screen.AddDetails.route) {
            AddDetailsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSave = { noteText, audioUri, imageUris ->
                    // TODO: Save the details to database
                }
            )
        }
    }
}
