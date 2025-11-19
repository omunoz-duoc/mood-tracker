package cl.duoc.dsy1105.moodtracker.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.dsy1105.moodtracker.data.local.AppDatabase
import cl.duoc.dsy1105.moodtracker.data.local.SessionManager
import cl.duoc.dsy1105.moodtracker.data.repository.MoodRepository
import cl.duoc.dsy1105.moodtracker.domain.model.MoodType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class MoodUiState(
    val isLoading: Boolean = false,
    val isSaveSuccessful: Boolean = false,
    val errorMessage: String? = null
)

class MoodViewModel(context: Context) : ViewModel() {

    private val moodRepository: MoodRepository
    private val sessionManager: SessionManager

    private val _uiState = MutableStateFlow(MoodUiState())
    val uiState: StateFlow<MoodUiState> = _uiState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(context)
        moodRepository = MoodRepository(database.moodDao())
        sessionManager = SessionManager(context)
    }

    fun saveMoodEntry(moodType: MoodType, note: String? = null) {
        viewModelScope.launch {
            _uiState.value = MoodUiState(isLoading = true)

            try {
                // Get current user ID from session
                val userId = sessionManager.userIdFlow.firstOrNull()

                if (userId == null) {
                    _uiState.value = MoodUiState(
                        isLoading = false,
                        errorMessage = "No se encontró sesión activa"
                    )
                    return@launch
                }

                // Save mood entry
                moodRepository.saveMoodEntry(userId, moodType, note)

                _uiState.value = MoodUiState(
                    isLoading = false,
                    isSaveSuccessful = true
                )
            } catch (e: Exception) {
                _uiState.value = MoodUiState(
                    isLoading = false,
                    errorMessage = "Error al guardar: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun resetSaveSuccess() {
        _uiState.value = _uiState.value.copy(isSaveSuccessful = false)
    }
}
