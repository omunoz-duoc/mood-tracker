package cl.duoc.dsy1105.moodtracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.duoc.dsy1105.moodtracker.data.local.entities.MoodEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDao {

    @Insert
    suspend fun insertMoodEntry(moodEntry: MoodEntry): Long

    @Query("SELECT * FROM mood_entries WHERE userId = :userId ORDER BY date DESC")
    fun getAllMoodEntriesForUser(userId: Long): Flow<List<MoodEntry>>

    @Query("SELECT * FROM mood_entries WHERE userId = :userId ORDER BY date DESC LIMIT :limit")
    suspend fun getRecentMoodEntries(userId: Long, limit: Int): List<MoodEntry>

    @Query("SELECT * FROM mood_entries WHERE id = :entryId")
    suspend fun getMoodEntryById(entryId: Long): MoodEntry?

    @Query("DELETE FROM mood_entries WHERE id = :entryId")
    suspend fun deleteMoodEntry(entryId: Long)

    @Query("DELETE FROM mood_entries WHERE userId = :userId")
    suspend fun deleteAllMoodEntriesForUser(userId: Long)

    @Query("SELECT COUNT(*) FROM mood_entries WHERE userId = :userId")
    suspend fun getMoodEntryCount(userId: Long): Int
}
