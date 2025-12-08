package cl.duoc.dsy1105.moodtracker.data.remote.api

import cl.duoc.dsy1105.moodtracker.data.remote.dto.LoginRequest
import cl.duoc.dsy1105.moodtracker.data.remote.dto.RegisterRequest
import cl.duoc.dsy1105.moodtracker.data.remote.dto.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit API service for user authentication
 * Base URL configured in NetworkModule
 */
interface UserApiService {

    @POST("api/users/register")
    suspend fun register(@Body request: RegisterRequest): UserResponse

    @POST("api/users/login")
    suspend fun login(@Body request: LoginRequest): UserResponse

    @GET("api/users/{userId}")
    suspend fun getUserById(@Path("userId") userId: Long): UserResponse
}
