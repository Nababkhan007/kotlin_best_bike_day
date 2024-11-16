package com.khan.bestbikeday.data

object DummyData {
    fun getDummyForecasts(): List<DailyForecast> = listOf(
        DailyForecast(
            dt = System.currentTimeMillis() / 1000,
            temp = Temperature(
                max = 22.0,
                min = 15.0,
                day = 18.5
            ),
            windSpeed = 5.2,
            weather = listOf(
                Weather(
                    description = "Partly cloudy",
                    main = "Clouds",
                    icon = "02d"
                )
            ),
            precipitation = 0.0
        ),
        DailyForecast(
            dt = System.currentTimeMillis() / 1000 + 86400,
            temp = Temperature(
                max = 24.0,
                min = 16.0,
                day = 20.0
            ),
            windSpeed = 4.8,
            weather = listOf(
                Weather(
                    description = "Sunny",
                    main = "Clear",
                    icon = "01d"
                )
            ),
            precipitation = 0.0
        ),
        DailyForecast(
            dt = System.currentTimeMillis() / 1000 + 172800,
            temp = Temperature(
                max = 20.0,
                min = 14.0,
                day = 17.0
            ),
            windSpeed = 6.1,
            weather = listOf(
                Weather(
                    description = "Light rain",
                    main = "Rain",
                    icon = "10d"
                )
            ),
            precipitation = 2.5
        ),
        DailyForecast(
            dt = System.currentTimeMillis() / 1000 + 259200,
            temp = Temperature(
                max = 21.0,
                min = 15.0,
                day = 18.0
            ),
            windSpeed = 5.5,
            weather = listOf(
                Weather(
                    description = "Clear sky",
                    main = "Clear",
                    icon = "01d"
                )
            ),
            precipitation = 0.0
        )
    )
} 