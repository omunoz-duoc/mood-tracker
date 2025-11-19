package cl.duoc.dsy1105.moodtracker.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.dsy1105.moodtracker.data.local.AppDatabase
import cl.duoc.dsy1105.moodtracker.data.local.SessionManager
import cl.duoc.dsy1105.moodtracker.data.repository.UserRepository
import cl.duoc.dsy1105.moodtracker.domain.validators.LoginValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val isLoginSuccessful: Boolean = false,
    val errorMessage: String? = null
)

class LoginViewModel(context: Context) : ViewModel() {

    private val userRepository: UserRepository
    private val sessionManager: SessionManager

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(context)
        userRepository = UserRepository(database.userDao())
        sessionManager = SessionManager(context)
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            // Validate inputs first
            val validation = LoginValidator.validateLogin(email, password)

            if (!validation.isValid) {
                _uiState.value = LoginUiState(
                    errorMessage = "Por favor, corrige los errores del formulario"
                )
                return@launch
            }

            _uiState.value = LoginUiState(isLoading = true)

            try {
                val user = userRepository.login(email, password)

                if (user != null) {
                    // Save session
                    sessionManager.saveUserId(user.id)

                    _uiState.value = LoginUiState(
                        isLoading = false,
                        isLoginSuccessful = true
                    )
                } else {
                    _uiState.value = LoginUiState(
                        isLoading = false,
                        errorMessage = "Email o contraseña incorrectos"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState(
                    isLoading = false,
                    errorMessage = "Error al iniciar sesión: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun resetLoginSuccess() {
        _uiState.value = _uiState.value.copy(isLoginSuccessful = false)
    }
}
