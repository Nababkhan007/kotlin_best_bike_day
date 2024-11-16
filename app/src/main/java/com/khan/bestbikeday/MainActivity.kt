package com.khan.bestbikeday

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.khan.bestbikeday.data.BikeScoreCalculator
import com.khan.bestbikeday.data.DailyForecast
import com.khan.bestbikeday.data.DummyData
import com.khan.bestbikeday.ui.theme.BestBikeDayTheme
import com.khan.bestbikeday.ui.theme.MontserratFontFamily
import com.khan.bestbikeday.ui.theme.ScoreFair
import com.khan.bestbikeday.ui.theme.ScoreGood
import com.khan.bestbikeday.ui.theme.ScorePerfect
import com.khan.bestbikeday.ui.theme.ScorePoor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    private val weatherScope = CoroutineScope(Dispatchers.Main + Job())

    // Define DataStore instance
    private val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
        produceFile = { File(applicationContext.filesDir, "settings.preferences_pb") }
    )

    // Define the key
    private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { true }

        Handler(Looper.getMainLooper()).postDelayed({
            splashScreen.setKeepOnScreenCondition { false }
        }, 1000)

        enableEdgeToEdge()
        setContent {
            val isDarkMode = remember {
                dataStore.data
                    .map { preferences -> preferences[IS_DARK_MODE] ?: false }
            }.collectAsState(initial = false)

            BestBikeDayTheme(darkTheme = isDarkMode.value) {
                WeatherForecastScreen(
                    isDarkMode = isDarkMode.value,
                    onThemeToggle = { newMode ->
                        weatherScope.launch {
                            dataStore.edit { preferences ->
                                preferences[IS_DARK_MODE] = newMode
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun WeatherForecastScreen(
    isDarkMode: Boolean,
    onThemeToggle: (Boolean) -> Unit
) {
    var forecasts by remember { mutableStateOf<List<DailyForecast>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            forecasts = WeatherApi.getWeatherForecast(
                lat = 40.7128,
                lon = -74.0060
            )
            isLoading = false
        } catch (e: Exception) {
            forecasts = DummyData.getDummyForecasts()
            isLoading = false
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 4.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 40.dp, end = 16.dp, bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_bike),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Best Bike Day",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.5).sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    shape = CircleShape
                                )
                        ) {
                            IconButton(
                                onClick = { onThemeToggle(!isDarkMode) },
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = if (isDarkMode) "Switch to Light Mode" else "Switch to Dark Mode",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (isLoading) {
            LoadingScreen(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                            )
                        )
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(forecasts.size) { index ->
                    DailyForecastItem(forecasts[index])
                }
            }
        }
    }
}

@Composable
private fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_bike),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}

@Composable
fun DailyForecastItem(forecast: DailyForecast) {
    val bikeScore = remember(forecast) {
        BikeScoreCalculator.calculateBikeScore(forecast)
    }

    val scoreColor = remember(bikeScore) {
        val ScoreAvoid = null
        when {
            bikeScore >= 80 -> ScorePerfect
            bikeScore >= 60 -> ScoreGood
            bikeScore >= 40 -> ScoreFair
            bikeScore >= 20 -> ScorePoor
            else -> ScoreAvoid
        }
    }

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            scoreColor!!.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WeatherIcon(forecast.weather.firstOrNull()?.main ?: "")
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
                                    .format(Date(forecast.dt * 1000)),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = forecast.weather.firstOrNull()?.description?.capitalize()
                                    ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        WeatherInfoItem(
                            iconRes = R.drawable.ic_temperature,
                            label = "Temperature",
                            value = "${forecast.temp.day.roundToInt()}°C"
                        )
                        WeatherInfoItem(
                            iconRes = R.drawable.ic_wind,
                            label = "Wind",
                            value = "${forecast.windSpeed} m/s"
                        )
                        if (forecast.precipitation > 0) {
                            WeatherInfoItem(
                                iconRes = R.drawable.ic_water_drop,
                                label = "Rain",
                                value = "${forecast.precipitation} mm"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))
                BikeScoreIndicator(score = bikeScore)
            }
        }
    }
}

@Composable
private fun BikeScoreIndicator(score: Int) {
    val scoreColor = when {
        score >= 80 -> Color(0xFF4CAF50) // Green
        score >= 60 -> Color(0xFF8BC34A) // Light Green
        score >= 40 -> Color(0xFFFFC107) // Amber
        score >= 20 -> Color(0xFFFF9800) // Orange
        else -> Color(0xFFF44336) // Red
    }

    Column(
        horizontalAlignment = Alignment.End
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(80.dp)
        ) {
            Canvas(modifier = Modifier.size(80.dp)) {
                // Background circle
                drawArc(
                    color = scoreColor.copy(alpha = 0.2f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 8f, cap = StrokeCap.Round)
                )

                // Progress arc
                drawArc(
                    color = scoreColor,
                    startAngle = -90f,
                    sweepAngle = (score * 3.6f),
                    useCenter = false,
                    style = Stroke(width = 8f, cap = StrokeCap.Round)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$score%",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = scoreColor
                )
                Text(
                    text = when {
                        score >= 80 -> "Perfect!"
                        score >= 60 -> "Good"
                        score >= 40 -> "Fair"
                        score >= 20 -> "Poor"
                        else -> "Avoid"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun WeatherInfoItem(
    iconRes: Int,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun WeatherIcon(weatherMain: String) {
    val iconRes = when (weatherMain.lowercase()) {
        "clear" -> R.drawable.ic_sun
        "clouds" -> R.drawable.ic_cloud
        "rain" -> R.drawable.ic_rain
        "snow" -> R.drawable.ic_snow
        else -> R.drawable.ic_cloud
    }

    Icon(
        painter = painterResource(id = iconRes),
        contentDescription = weatherMain,
        modifier = Modifier.size(24.dp),
        tint = MaterialTheme.colorScheme.primary
    )
}

// Add this extension function
private fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}