package com.khan.bestbikeday.data

data class WeatherForecastResponse(
    val list: List<ForecastItem>,
    val city: City
)

data class ForecastItem(
    val dt: Long,
    val main: MainWeather,
    val weather: List<Weather>,
    val wind: Wind,
    val rain: Rain? = null
)

data class MainWeather(
    val temp: Double,
    val temp_min: Double,
    val temp_max: Double
)

data class Weather(
    val main: String,
    val description: String,
    val icon: String
)

data class Wind(
    val speed: Double
)

data class Rain(
    val `3h`: Double? = 0.0
)

data class City(
    val name: String,
    val country: String
)

// Add these new data classes
data class DailyForecast(
    val dt: Long,
    val temp: Temperature,
    val windSpeed: Double,
    val weather: List<Weather>,
    val precipitation: Double
)

data class Temperature(
    val max: Double,
    val min: Double,
    val day: Double
)

// Helper function to convert API response to our app's model
fun ForecastItem.toDailyForecast(): DailyForecast {
    return DailyForecast(
        dt = dt,
        temp = Temperature(
            max = main.temp_max,
            min = main.temp_min,
            day = main.temp
        ),
        windSpeed = wind.speed,
        weather = weather,
        precipitation = rain?.`3h` ?: 0.0
    )
} 