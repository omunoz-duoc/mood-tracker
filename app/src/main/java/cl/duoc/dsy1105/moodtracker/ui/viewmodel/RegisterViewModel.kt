package cl.duoc.dsy1105.moodtracker.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.dsy1105.moodtracker.data.local.AppDatabase
import cl.duoc.dsy1105.moodtracker.data.repository.UserRepository
import cl.duoc.dsy1105.moodtracker.domain.validators.RegisterValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val isLoading: Boolean = false,
    val isRegistrationSuccessful: Boolean = false,
    val errorMessage: String? = null
)

class RegisterViewModel(context: Context) : ViewModel() {

    private val userRepository: UserRepository

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(context)
        userRepository = UserRepository(database.userDao())
    }

    fun register(email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            // Validate inputs first
            val validation = RegisterValidator.validateRegister(email, password, confirmPassword)

            if (!validation.isValid) {
                _uiState.value = RegisterUiState(
                    errorMessage = "Por favor, corrige los errores del formulario"
                )
                return@launch
            }

            _uiState.value = RegisterUiState(isLoading = true)

            try {
                val userId = userRepository.registerUser(email, password)

                if (userId != null) {
                    _uiState.value = RegisterUiState(
                        isLoading = false,
                        isRegistrationSuccessful = true
                    )
                } else {
                    _uiState.value = RegisterUiState(
                        isLoading = false,
                        errorMessage = "Este email ya está registrado"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = RegisterUiState(
                    isLoading = false,
                    errorMessage = "Error al registrar usuario: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun resetRegistrationSuccess() {
        _uiState.value = _uiState.value.copy(isRegistrationSuccessful = false)
    }
}
