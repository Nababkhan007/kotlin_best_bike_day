package com.khan.bestbikeday.data

object BikeScoreCalculator {
    // Temperature ranges (in Celsius)
    private const val IDEAL_TEMP_MIN = 18.0
    private const val IDEAL_TEMP_MAX = 25.0
    private const val ACCEPTABLE_TEMP_MIN = 10.0
    private const val ACCEPTABLE_TEMP_MAX = 30.0

    // Wind speed ranges (in m/s)
    private const val IDEAL_WIND_MAX = 5.0
    private const val ACCEPTABLE_WIND_MAX = 10.0

    // Precipitation thresholds (in mm)
    private const val IDEAL_PRECIP_MAX = 0.0
    private const val ACCEPTABLE_PRECIP_MAX = 2.0

    fun calculateBikeScore(forecast: DailyForecast): Int {
        val tempScore = calculateTemperatureScore(forecast.temp.day)
        val windScore = calculateWindScore(forecast.windSpeed)
        val precipScore = calculatePrecipitationScore(forecast.precipitation)

        // Weight the scores (temperature being most important, then precipitation, then wind)
        return ((tempScore * 0.5) + (precipScore * 0.3) + (windScore * 0.2)).toInt()
            .coerceIn(0, 100)
    }

    private fun calculateTemperatureScore(temp: Double): Double {
        return when {
            // Ideal temperature range (100%)
            temp in IDEAL_TEMP_MIN..IDEAL_TEMP_MAX -> 100.0
            
            // Too cold
            temp < ACCEPTABLE_TEMP_MIN -> 0.0
            temp < IDEAL_TEMP_MIN -> {
                val range = IDEAL_TEMP_MIN - ACCEPTABLE_TEMP_MIN
                val diff = temp - ACCEPTABLE_TEMP_MIN
                (diff / range) * 100
            }
            
            // Too hot
            temp > ACCEPTABLE_TEMP_MAX -> 0.0
            temp > IDEAL_TEMP_MAX -> {
                val range = ACCEPTABLE_TEMP_MAX - IDEAL_TEMP_MAX
                val diff = ACCEPTABLE_TEMP_MAX - temp
                (diff / range) * 100
            }
            
            else -> 100.0
        }
    }

    private fun calculateWindScore(windSpeed: Double): Double {
        return when {
            windSpeed <= IDEAL_WIND_MAX -> 100.0
            windSpeed <= ACCEPTABLE_WIND_MAX -> {
                val range = ACCEPTABLE_WIND_MAX - IDEAL_WIND_MAX
                val diff = ACCEPTABLE_WIND_MAX - windSpeed
                (diff / range) * 100
            }
            else -> 0.0
        }
    }

    private fun calculatePrecipitationScore(precipitation: Double): Double {
        return when {
            precipitation <= IDEAL_PRECIP_MAX -> 100.0
            precipitation <= ACCEPTABLE_PRECIP_MAX -> {
                val range = ACCEPTABLE_PRECIP_MAX - IDEAL_PRECIP_MAX
                val diff = ACCEPTABLE_PRECIP_MAX - precipitation
                (diff / range) * 100
            }
            else -> 0.0
        }
    }
} 