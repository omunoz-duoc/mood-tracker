package cl.duoc.dsy1105.moodtracker.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.dsy1105.moodtracker.data.local.AppDatabase
import cl.duoc.dsy1105.moodtracker.data.local.SessionManager
import cl.duoc.dsy1105.moodtracker.data.local.entities.MoodEntry
import cl.duoc.dsy1105.moodtracker.data.repository.MoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class HistoryUiState(
    val isLoading: Boolean = true,
    val moodEntries: List<MoodEntry> = emptyList(),
    val errorMessage: String? = null
)

class HistoryViewModel(context: Context) : ViewModel() {

    private val moodRepository: MoodRepository
    private val sessionManager: SessionManager

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(context)
        moodRepository = MoodRepository(database.moodDao())
        sessionManager = SessionManager(context)

        loadMoodHistory()
    }

    private fun loadMoodHistory() {
        viewModelScope.launch {
            _uiState.value = HistoryUiState(isLoading = true)

            try {
                // Get current user ID from session
                val userId = sessionManager.userIdFlow.firstOrNull()

                if (userId == null) {
                    _uiState.value = HistoryUiState(
                        isLoading = false,
                        errorMessage = "No se encontró sesión activa"
                    )
                    return@launch
                }

                // Collect mood entries for the user
                moodRepository.getAllMoodEntriesForUser(userId).collect { entries ->
                    _uiState.value = HistoryUiState(
                        isLoading = false,
                        moodEntries = entries
                    )
                }
            } catch (e: Exception) {
                _uiState.value = HistoryUiState(
                    isLoading = false,
                    errorMessage = "Error al cargar historial: ${e.message}"
                )
            }
        }
    }

    fun refresh() {
        loadMoodHistory()
    }
}
