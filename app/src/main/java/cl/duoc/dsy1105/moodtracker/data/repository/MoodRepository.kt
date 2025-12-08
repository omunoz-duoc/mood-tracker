package cl.duoc.dsy1105.moodtracker.data.repository

import android.util.Log
import cl.duoc.dsy1105.moodtracker.data.local.dao.MoodDao
import cl.duoc.dsy1105.moodtracker.data.local.entities.MoodEntry
import cl.duoc.dsy1105.moodtracker.data.remote.NetworkModule
import cl.duoc.dsy1105.moodtracker.data.remote.api.MoodApiService
import cl.duoc.dsy1105.moodtracker.data.remote.dto.MoodEntryRequest
import cl.duoc.dsy1105.moodtracker.data.remote.dto.toMoodEntry
import cl.duoc.dsy1105.moodtracker.domain.model.MoodType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MoodRepository(
    private val moodDao: MoodDao,
    private val moodApiService: MoodApiService = NetworkModule.moodApiService
) {

    /**
     * Save a new mood entry
     * Offline-first strategy: Save to local immediately, sync to API in background
     */
    suspend fun saveMoodEntry(userId: Long, moodType: MoodType, note: String? = null): Long {
        val moodEntry = MoodEntry.create(userId, moodType, note)

        // Save to local immediately (offline support)
        val localId = moodDao.insertMoodEntry(moodEntry)
        Log.d("MoodRepository", "Mood entry saved locally: $localId")

        // Sync to API in background (non-blocking)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = MoodEntryRequest(
                    userId = userId,
                    moodType = moodType.name,
                    note = note,
                    date = moodEntry.date
                )
                val response = moodApiService.createMood(request)
                Log.d("MoodRepository", "Mood entry synced to API: ${response.id}")

                // Optional: Update local entry with API-generated ID if needed
                // This would require updating the entry with the server ID
            } catch (e: Exception) {
                Log.e("MoodRepository", "Failed to sync mood to API (will retry later)", e)
                // TODO: Mark for later sync in a pending sync table
            }
        }

        return localId
    }

    /**
     * Save a new mood entry with details (audio and images)
     */
    suspend fun saveMoodEntryWithDetails(
        userId: Long,
        moodType: MoodType,
        note: String? = null,
        audioUri: String? = null,
        audioDuration: Int? = null,
        imageUris: List<String>? = null
    ): Long {
        val moodEntry = MoodEntry.createWithDetails(userId, moodType, note, audioUri, audioDuration, imageUris)
        return moodDao.insertMoodEntry(moodEntry)
    }

    /**
     * Update an existing mood entry
     */
    suspend fun updateMoodEntry(moodEntry: MoodEntry) {
        moodDao.updateMoodEntry(moodEntry)
    }

    /**
     * Update mood entry note
     */
    suspend fun updateMoodEntryNote(entryId: Long, newNote: String) {
        val entry = moodDao.getMoodEntryById(entryId)
        entry?.let {
            val updatedEntry = it.copy(note = newNote)
            moodDao.updateMoodEntry(updatedEntry)
        }
    }

    /**
     * Get all mood entries for a user as a Flow (live updates)
     * Offline-first strategy: Fetch from API and update local cache, then return local Flow
     */
    fun getAllMoodEntriesForUser(userId: Long): Flow<List<MoodEntry>> {
        // Fetch from API in background and update local cache
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiMoods = moodApiService.getUserMoods(userId)
                Log.d("MoodRepository", "Fetched ${apiMoods.size} moods from API")

                // Clear local and replace with API data (simple sync strategy)
                withContext(Dispatchers.IO) {
                    moodDao.deleteAllMoodEntriesForUser(userId)
                    apiMoods.forEach { dto ->
                        moodDao.insertMoodEntry(dto.toMoodEntry())
                    }
                }

                Log.d("MoodRepository", "Local cache updated from API")
            } catch (e: Exception) {
                Log.e("MoodRepository", "Failed to fetch from API, using local cache", e)
                // Continue using local cache
            }
        }

        // Return local Flow (always up-to-date)
        return moodDao.getAllMoodEntriesForUser(userId)
    }

    /**
     * Get recent mood entries for a user
     */
    suspend fun getRecentMoodEntries(userId: Long, limit: Int = 10): List<MoodEntry> {
        return moodDao.getRecentMoodEntries(userId, limit)
    }

    /**
     * Get mood entry by ID
     */
    suspend fun getMoodEntryById(entryId: Long): MoodEntry? {
        return moodDao.getMoodEntryById(entryId)
    }

    /**
     * Delete a mood entry
     * Offline-first strategy: Delete from local immediately, sync deletion to API
     */
    suspend fun deleteMoodEntry(entryId: Long) {
        // Delete from local immediately
        moodDao.deleteMoodEntry(entryId)
        Log.d("MoodRepository", "Mood entry deleted locally: $entryId")

        // Sync deletion to API in background
        CoroutineScope(Dispatchers.IO).launch {
            try {
                moodApiService.deleteMood(entryId)
                Log.d("MoodRepository", "Mood entry deleted from API: $entryId")
            } catch (e: Exception) {
                Log.e("MoodRepository", "Failed to delete from API", e)
                // Entry is already deleted locally, so this is not critical
            }
        }
    }

    /**
     * Get total count of mood entries for a user
     */
    suspend fun getMoodEntryCount(userId: Long): Int {
        return moodDao.getMoodEntryCount(userId)
    }
}
