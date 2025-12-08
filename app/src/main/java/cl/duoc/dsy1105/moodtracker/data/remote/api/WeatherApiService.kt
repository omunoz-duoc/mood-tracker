package cl.duoc.dsy1105.moodtracker.data.remote.api

import cl.duoc.dsy1105.moodtracker.data.remote.dto.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API service for OpenWeatherMap
 *
 * API Documentation: https://openweathermap.org/api
 * Free tier: 1,000 API calls/day
 *
 * To get an API key:
 * 1. Sign up at https://openweathermap.org/api
 * 2. Navigate to API keys section
 * 3. Generate a new key (activation takes ~10 minutes)
 *
 * Example URL:
 * https://api.openweathermap.org/data/2.5/weather?lat=35&lon=139&appid=YOUR_API_KEY&units=metric&lang=es
 */
interface WeatherApiService {

    /**
     * Get current weather by coordinates
     *
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @param apiKey Your OpenWeatherMap API key
     * @param units Unit system (metric for Celsius, imperial for Fahrenheit)
     * @param lang Language code (es for Spanish)
     * @return WeatherResponse with current weather data
     */
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",  // Celsius
        @Query("lang") lang: String = "es"         // Spanish descriptions
    ): WeatherResponse

    /**
     * Get current weather by city name
     *
     * @param cityName Name of the city (e.g., "Santiago")
     * @param apiKey Your OpenWeatherMap API key
     * @param units Unit system
     * @param lang Language code
     * @return WeatherResponse
     */
    @GET("weather")
    suspend fun getCurrentWeatherByCity(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "es"
    ): WeatherResponse
}
