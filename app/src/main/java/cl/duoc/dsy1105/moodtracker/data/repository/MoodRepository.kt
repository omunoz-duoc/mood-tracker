package cl.duoc.dsy1105.moodtracker.data.repository

import cl.duoc.dsy1105.moodtracker.data.local.dao.MoodDao
import cl.duoc.dsy1105.moodtracker.data.local.entities.MoodEntry
import cl.duoc.dsy1105.moodtracker.domain.model.MoodType
import kotlinx.coroutines.flow.Flow

class MoodRepository(private val moodDao: MoodDao) {

    /**
     * Save a new mood entry
     */
    suspend fun saveMoodEntry(userId: Long, moodType: MoodType, note: String? = null): Long {
        val moodEntry = MoodEntry.create(userId, moodType, note)
        return moodDao.insertMoodEntry(moodEntry)
    }

    /**
     * Save a new mood entry with details (audio and images)
     */
    suspend fun saveMoodEntryWithDetails(
        userId: Long,
        moodType: MoodType,
        note: String? = null,
        audioUri: String? = null,
        imageUris: List<String>? = null
    ): Long {
        val moodEntry = MoodEntry.createWithDetails(userId, moodType, note, audioUri, imageUris)
        return moodDao.insertMoodEntry(moodEntry)
    }

    /**
     * Get all mood entries for a user as a Flow (live updates)
     */
    fun getAllMoodEntriesForUser(userId: Long): Flow<List<MoodEntry>> {
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
     */
    suspend fun deleteMoodEntry(entryId: Long) {
        moodDao.deleteMoodEntry(entryId)
    }

    /**
     * Get total count of mood entries for a user
     */
    suspend fun getMoodEntryCount(userId: Long): Int {
        return moodDao.getMoodEntryCount(userId)
    }
}
