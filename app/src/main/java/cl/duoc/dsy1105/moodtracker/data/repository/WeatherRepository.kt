package cl.duoc.dsy1105.moodtracker.data.repository

import android.util.Log
import cl.duoc.dsy1105.moodtracker.data.remote.NetworkModule
import cl.duoc.dsy1105.moodtracker.data.remote.api.WeatherApiService
import cl.duoc.dsy1105.moodtracker.data.remote.dto.WeatherInfo
import cl.duoc.dsy1105.moodtracker.data.remote.dto.toWeatherInfo

/**
 * Repository for fetching weather data from OpenWeatherMap API
 *
 * Usage example:
 * ```
 * val weatherRepo = WeatherRepository()
 * val weather = weatherRepo.getCurrentWeather(latitude = -33.4489, longitude = -70.6693)
 * if (weather != null) {
 *     println("${weather.temperature}°C, ${weather.description}")
 * }
 * ```
 *
 * Note: Requires WEATHER_API_KEY to be set in NetworkModule
 */
class WeatherRepository(
    private val weatherApiService: WeatherApiService = NetworkModule.weatherApiService
) {

    /**
     * Get current weather by coordinates
     *
     * @param latitude Latitude of the location
     * @param longitude Longitude of the location
     * @return WeatherInfo if successful, null otherwise
     */
    suspend fun getCurrentWeather(latitude: Double, longitude: Double): WeatherInfo? {
        return try {
            val response = weatherApiService.getCurrentWeather(
                latitude = latitude,
                longitude = longitude,
                apiKey = NetworkModule.WEATHER_API_KEY
            )

            val weatherInfo = response.toWeatherInfo()
            Log.d("WeatherRepository", "Weather fetched: ${weatherInfo.temperature}°C, ${weatherInfo.description}")

            weatherInfo
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Failed to fetch weather", e)
            null
        }
    }

    /**
     * Get current weather by city name
     *
     * @param cityName Name of the city (e.g., "Santiago")
     * @return WeatherInfo if successful, null otherwise
     */
    suspend fun getCurrentWeatherByCity(cityName: String): WeatherInfo? {
        return try {
            val response = weatherApiService.getCurrentWeatherByCity(
                cityName = cityName,
                apiKey = NetworkModule.WEATHER_API_KEY
            )

            val weatherInfo = response.toWeatherInfo()
            Log.d("WeatherRepository", "Weather fetched for $cityName: ${weatherInfo.temperature}°C")

            weatherInfo
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Failed to fetch weather for $cityName", e)
            null
        }
    }

    /**
     * Get weather icon URL
     * Icon codes: https://openweathermap.org/weather-conditions
     *
     * @param iconCode Icon code from API (e.g., "01d")
     * @return Full URL to icon image
     */
    fun getIconUrl(iconCode: String): String {
        return "https://openweathermap.org/img/wn/${iconCode}@2x.png"
    }
}
