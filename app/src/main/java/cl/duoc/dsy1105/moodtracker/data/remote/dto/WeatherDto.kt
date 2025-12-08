package cl.duoc.dsy1105.moodtracker.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Weather API DTOs for OpenWeatherMap
 *
 * To use:
 * 1. Sign up for free API key at https://openweathermap.org/api
 * 2. Add API key to local.properties: WEATHER_API_KEY=your_key_here
 * 3. Configure BuildConfig to read the key
 */

@JsonClass(generateAdapter = true)
data class WeatherResponse(
    val main: WeatherMain,
    val weather: List<WeatherCondition>,
    val name: String  // City name
)

@JsonClass(generateAdapter = true)
data class WeatherMain(
    val temp: Double,         // Temperature in Celsius (when units=metric)
    @Json(name = "feels_like")
    val feelsLike: Double,
    val humidity: Int,
    val pressure: Int
)

@JsonClass(generateAdapter = true)
data class WeatherCondition(
    val id: Int,
    val main: String,         // e.g., "Clear", "Clouds", "Rain"
    val description: String,  // e.g., "clear sky", "few clouds"
    val icon: String         // Icon code (e.g., "01d")
)

/**
 * Simplified weather data for UI display
 */
data class WeatherInfo(
    val temperature: Double,
    val description: String,
    val icon: String,
    val cityName: String
)

/**
 * Extension function to convert API response to UI model
 */
fun WeatherResponse.toWeatherInfo(): WeatherInfo {
    return WeatherInfo(
        temperature = main.temp,
        description = weather.firstOrNull()?.description ?: "Unknown",
        icon = weather.firstOrNull()?.icon ?: "",
        cityName = name
    )
}
