import com.khan.bestbikeday.BuildConfig
import com.khan.bestbikeday.data.DailyForecast
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import com.khan.bestbikeday.data.WeatherForecastResponse
import com.khan.bestbikeday.data.toDailyForecast

interface WeatherService {
    @GET("data/2.5/forecast")
    suspend fun getWeatherForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String
    ): WeatherForecastResponse
}

object WeatherApi {
    private const val BASE_URL = "https://api.openweathermap.org/"
    private var API_KEY = BuildConfig.WEATHER_API_KEY

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: WeatherService = retrofit.create(WeatherService::class.java)

    suspend fun getWeatherForecast(lat: Double, lon: Double): List<DailyForecast> {
        val response = service.getWeatherForecast(
            lat = lat,
            lon = lon,
            apiKey = API_KEY
        )
        // Convert API response to our app's model
        return response.list.map { it.toDailyForecast() }
    }
} 