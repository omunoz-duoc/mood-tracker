package cl.duoc.dsy1105.moodtracker.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.dsy1105.moodtracker.data.local.AppDatabase
import cl.duoc.dsy1105.moodtracker.data.local.SessionManager
import cl.duoc.dsy1105.moodtracker.data.repository.MoodRepository
import cl.duoc.dsy1105.moodtracker.domain.model.MoodType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

data class HomeUiState(
    val moodEntries: List<UiMoodEntry> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class UiMoodEntry(
    val id: Long,
    val moodType: String,
    val moodEmoji: String,
    val date: Date,
    val tags: List<String>,
    val note: String,
    val hasAudio: Boolean = false,
    val audioDuration: Int? = null, // Duration in seconds
    val imageUris: List<String> = emptyList()
)

class HomeViewModel(context: Context) : ViewModel() {

    private val moodRepository: MoodRepository
    private val sessionManager: SessionManager

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(context)
        moodRepository = MoodRepository(database.moodDao())
        sessionManager = SessionManager(context)

        // Start collecting mood entries
        startCollectingMoodEntries()
    }

    private fun startCollectingMoodEntries() {
        viewModelScope.launch {
            try {
                // Get current user ID from session
                val userId = sessionManager.userIdFlow.firstOrNull()

                if (userId == null) {
                    Log.w("HomeViewModel", "No active session found")
                    _uiState.value = HomeUiState(
                        isLoading = false,
                        errorMessage = "No se encontró sesión activa"
                    )
                    return@launch
                }

                // Continuously collect mood entries from database
                moodRepository.getAllMoodEntriesForUser(userId)
                    .catch { e ->
                        Log.e("HomeViewModel", "Error collecting mood entries", e)
                        _uiState.value = HomeUiState(
                            isLoading = false,
                            errorMessage = "Error al cargar entradas: ${e.message}"
                        )
                    }
                    .collect { entries ->
                        try {
                            val uiEntries = entries.mapNotNull { entry ->
                                try {
                                    val moodType = MoodType.valueOf(entry.moodType)
                                    UiMoodEntry(
                                        id = entry.id,
                                        moodType = moodType.displayName,
                                        moodEmoji = moodType.emoji,
                                        date = Date(entry.date),
                                        tags = emptyList(), // Tags not implemented yet
                                        note = entry.note ?: "",
                                        hasAudio = entry.audioUri != null,
                                        audioDuration = entry.audioDuration,
                                        imageUris = entry.getImageUriList()
                                    )
                                } catch (e: IllegalArgumentException) {
                                    Log.e("HomeViewModel", "Invalid mood type: ${entry.moodType}", e)
                                    null // Skip invalid entries
                                }
                            }

                            _uiState.value = HomeUiState(
                                moodEntries = uiEntries,
                                isLoading = false
                            )
                        } catch (e: Exception) {
                            Log.e("HomeViewModel", "Error mapping entries", e)
                            _uiState.value = HomeUiState(
                                isLoading = false,
                                errorMessage = "Error al procesar entradas: ${e.message}"
                            )
                        }
                    }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading mood entries", e)
                _uiState.value = HomeUiState(
                    isLoading = false,
                    errorMessage = "Error al cargar entradas: ${e.message}"
                )
            }
        }
    }

    fun loadMoodEntries() {
        // Just trigger a reload by restarting the collection
        // In this implementation, the collection is continuous, so this is a no-op
        // The Flow will automatically emit when database changes
        Log.d("HomeViewModel", "loadMoodEntries called (collection is continuous)")
    }

    fun deleteMoodEntry(entryId: Long) {
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "Deleting mood entry with ID: $entryId")
                moodRepository.deleteMoodEntry(entryId)
                // Flow will automatically emit updated list
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error deleting mood entry", e)
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al eliminar entrada: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
