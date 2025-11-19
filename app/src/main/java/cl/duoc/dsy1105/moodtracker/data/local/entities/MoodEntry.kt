package cl.duoc.dsy1105.moodtracker.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import cl.duoc.dsy1105.moodtracker.domain.model.MoodType

@Entity(
    tableName = "mood_entries",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"]), Index(value = ["date"])]
)
data class MoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val moodType: String, // Store enum name as string
    val note: String? = null,
    val date: Long = System.currentTimeMillis()
) {
    fun toMoodType(): MoodType {
        return MoodType.valueOf(moodType)
    }

    companion object {
        fun create(userId: Long, moodType: MoodType, note: String? = null): MoodEntry {
            return MoodEntry(
                userId = userId,
                moodType = moodType.name,
                note = note
            )
        }
    }
}
