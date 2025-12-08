package cl.duoc.dsy1105.moodtracker.data.remote.dto

import cl.duoc.dsy1105.moodtracker.data.local.entities.MoodEntry
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MoodEntryRequest(
    val userId: Long,
    val moodType: String,
    val note: String? = null,
    val audioUri: String? = null,
    val audioDuration: Int? = null,
    val imageUris: String? = null,
    val date: Long
)

@JsonClass(generateAdapter = true)
data class MoodEntryResponse(
    val id: Long,
    val userId: Long,
    val moodType: String,
    val note: String?,
    val audioUri: String?,
    val audioDuration: Int?,
    val imageUris: String?,
    val date: Long
)

/**
 * Extension function to convert MoodEntryResponse to MoodEntry entity
 */
fun MoodEntryResponse.toMoodEntry(): MoodEntry {
    return MoodEntry(
        id = id,
        userId = userId,
        moodType = moodType,
        note = note,
        audioUri = audioUri,
        audioDuration = audioDuration,
        imageUris = imageUris,
        date = date
    )
}
