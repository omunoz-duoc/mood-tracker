package cl.duoc.dsy1105.moodtracker.data.remote.api

import cl.duoc.dsy1105.moodtracker.data.remote.dto.MoodEntryRequest
import cl.duoc.dsy1105.moodtracker.data.remote.dto.MoodEntryResponse
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API service for mood tracking
 * Base URL configured in NetworkModule
 */
interface MoodApiService {

    @POST("api/moods")
    suspend fun createMood(@Body request: MoodEntryRequest): MoodEntryResponse

    @GET("api/moods/user/{userId}")
    suspend fun getUserMoods(@Path("userId") userId: Long): List<MoodEntryResponse>

    @GET("api/moods/{moodId}")
    suspend fun getMood(@Path("moodId") moodId: Long): MoodEntryResponse

    @PUT("api/moods/{moodId}")
    suspend fun updateMood(
        @Path("moodId") moodId: Long,
        @Body request: MoodEntryRequest
    ): MoodEntryResponse

    @DELETE("api/moods/{moodId}")
    suspend fun deleteMood(@Path("moodId") moodId: Long): Response<Unit>
}
