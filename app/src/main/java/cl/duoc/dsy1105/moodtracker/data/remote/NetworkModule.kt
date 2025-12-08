package cl.duoc.dsy1105.moodtracker.data.remote

import cl.duoc.dsy1105.moodtracker.data.remote.api.MoodApiService
import cl.duoc.dsy1105.moodtracker.data.remote.api.UserApiService
import cl.duoc.dsy1105.moodtracker.data.remote.api.WeatherApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Network module for configuring Retrofit and API services
 *
 * IMPORTANT: Update these URLs with your actual Google Cloud VM IPs
 * For local testing, you can use:
 * - USER_SERVICE_URL = "http://10.0.2.2:8080/" (Android emulator to localhost)
 * - MOOD_SERVICE_URL = "http://10.0.2.2:8081/" (Android emulator to localhost)
 *
 * For production/demo:
 * - Replace with actual VM IP addresses
 * - Example: "http://34.168.123.456:8080/"
 */
object NetworkModule {

    // TODO: Replace with actual Google Cloud VM IPs when deploying
    private const val USER_SERVICE_URL = "http://10.0.2.2:8080/"
    private const val MOOD_SERVICE_URL = "http://10.0.2.2:8081/"
    private const val WEATHER_API_URL = "https://api.openweathermap.org/data/2.5/"

    /**
     * Weather API Key
     * TODO: Replace with your actual OpenWeatherMap API key
     * Get one at: https://openweathermap.org/api
     */
    const val WEATHER_API_KEY = "YOUR_API_KEY_HERE"  // Replace with actual key

    /**
     * OkHttpClient with logging interceptor for debugging
     */
    private val okHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Moshi instance for JSON serialization/deserialization
     */
    private val moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    /**
     * UserApiService instance for user authentication
     */
    val userApiService: UserApiService by lazy {
        Retrofit.Builder()
            .baseUrl(USER_SERVICE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(UserApiService::class.java)
    }

    /**
     * MoodApiService instance for mood tracking
     */
    val moodApiService: MoodApiService by lazy {
        Retrofit.Builder()
            .baseUrl(MOOD_SERVICE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(MoodApiService::class.java)
    }

    /**
     * WeatherApiService instance for weather data
     */
    val weatherApiService: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(WEATHER_API_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(WeatherApiService::class.java)
    }
}
