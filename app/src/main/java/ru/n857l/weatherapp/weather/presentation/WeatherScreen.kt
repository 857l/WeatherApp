package ru.n857l.weatherapp.weather.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import ru.n857l.weatherapp.R
import ru.n857l.weatherapp.findcity.presentation.ErrorUi
import ru.n857l.weatherapp.findcity.presentation.LoadingUi
import ru.n857l.weatherapp.ui.theme.SkyBottom
import ru.n857l.weatherapp.ui.theme.SkyTop
import java.io.Serializable

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    navController: NavController
) {
    BackHandler {
        navController.navigate("findCityScreen") {
            popUpTo("weatherScreen") {
                inclusive = true
            }
        }
    }

    val weatherScreenUi = viewModel.state.collectAsStateWithLifecycle()
    val forecastScreenUi = viewModel.forecastState.collectAsStateWithLifecycle()
    WeatherScreenUi(
        weatherUi = weatherScreenUi.value,
        forecastUi = forecastScreenUi.value,
        onRetryClick = viewModel::loadWeather,
    )
}

@Composable
fun WeatherScreenUi(
    weatherUi: WeatherUi,
    forecastUi: ForecastUi = ForecastUi.Empty,
    onRetryClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(SkyTop, SkyBottom))
            )
    ) {
        weatherUi.Show(onRetryClick, forecastUi)
    }
}

interface WeatherUi : Serializable {

    @Composable
    fun Show(onRetryClick: () -> Unit, forecastUi: ForecastUi) = Unit

    data object Empty : WeatherUi {
        private fun readResolve(): Any = Empty
    }

    data class Base(
        val cityName: String,
        val iconUrl: String,
        val description: String,
        val temperature: String,
        val feelsTemperature: String,
        val minMaxTemperature: String,
        val pressure: String,
        val humidity: String,
        val seaLevelPressure: String,
        val groundLevelPressure: String,
        val speed: String,
        val degree: String,
        val gust: String,
        val clouds: String,
        val visibility: String,
        val sunrise: String,
        val sunset: String,
        val time: String
    ) : WeatherUi {

        @Composable
        override fun Show(onRetryClick: () -> Unit, forecastUi: ForecastUi) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = cityName,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = time,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )

                AsyncImage(
                    model = iconUrl,
                    contentDescription = description,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(160.dp)
                )

                Text(
                    text = temperature,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White
                )
                Text(
                    text = description,
                    fontSize = 18.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$minMaxTemperature  •  $feelsTemperature",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                forecastUi.Show()

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    val details = listOf(
                        DetailItemData(Icons.Filled.Thermostat, R.string.detail_feels_like, feelsTemperature),
                        DetailItemData(Icons.Filled.WaterDrop, R.string.detail_humidity, humidity),
                        DetailItemData(Icons.Filled.Air, R.string.detail_wind, "$speed м/с, $degree"),
                        DetailItemData(Icons.Filled.Compress, R.string.detail_pressure, pressure),
                        DetailItemData(Icons.Filled.Visibility, R.string.detail_visibility, visibility),
                        DetailItemData(Icons.Filled.Air, R.string.detail_gust, "$gust м/с"),
                        DetailItemData(Icons.Filled.Cloud, R.string.detail_clouds, clouds),
                        DetailItemData(Icons.Filled.WbSunny, R.string.detail_sunrise, sunrise),
                        DetailItemData(Icons.Filled.NightsStay, R.string.detail_sunset, sunset)
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        details.chunked(2).forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowItems.forEach { item ->
                                    DetailCard(
                                        item = item,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (rowItems.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    data object NoConnectionError : WeatherUi {
        private fun readResolve(): Any = NoConnectionError

        @Composable
        override fun Show(onRetryClick: () -> Unit, forecastUi: ForecastUi) {
            ErrorUi(R.string.no_internet_connection, onRetryClick)
        }
    }

    data object ServiceUnavailableError : WeatherUi {
        private fun readResolve(): Any = ServiceUnavailableError

        @Composable
        override fun Show(onRetryClick: () -> Unit, forecastUi: ForecastUi) {
            ErrorUi(R.string.service_unavailable, onRetryClick)
        }
    }

    data object Loading : WeatherUi {
        private fun readResolve(): Any = Loading

        @Composable
        override fun Show(onRetryClick: () -> Unit, forecastUi: ForecastUi) {
            LoadingUi()
        }
    }
}

private data class DetailItemData(
    val icon: ImageVector,
    val labelRes: Int,
    val value: String
)

@Composable
private fun DetailCard(item: DetailItemData, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = stringResource(item.labelRes),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = item.value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWeatherScreenUi() {
    WeatherScreenUi(
        weatherUi = WeatherUi.Base(
            cityName = "Moscow",
            iconUrl = "https://openweathermap.org/img/wn/01d@4x.png",
            description = "Ясно",
            temperature = "12°",
            feelsTemperature = "11°",
            pressure = "745 мм рт. ст.",
            humidity = "60%",
            seaLevelPressure = "745 мм рт. ст.",
            groundLevelPressure = "740 мм рт. ст.",
            speed = "3.1",
            degree = "180°",
            gust = "5.2",
            clouds = "12",
            visibility = "10000 м",
            minMaxTemperature = "↑14° / ↓9°",
            time = "12:11",
            sunrise = "05:12",
            sunset = "21:47"
        ),
        forecastUi = ForecastUi.Base(
            hours = listOf(
                HourUi("06:00", "https://openweathermap.org/img/wn/01d@2x.png", "17°", 17f),
                HourUi("09:00", "https://openweathermap.org/img/wn/02d@2x.png", "18°", 18f),
                HourUi("12:00", "https://openweathermap.org/img/wn/03d@2x.png", "20°", 20f),
                HourUi("15:00", "https://openweathermap.org/img/wn/04d@2x.png", "20°", 20f),
                HourUi("18:00", "https://openweathermap.org/img/wn/10d@2x.png", "18°", 18f),
                HourUi("21:00", "https://openweathermap.org/img/wn/01n@2x.png", "15°", 15f)
            ),
            days = listOf(
                ForecastDayUi("Пн, 5 авг", "https://openweathermap.org/img/wn/01d@2x.png", "9°", "14°"),
                ForecastDayUi("Вт, 6 авг", "https://openweathermap.org/img/wn/02d@2x.png", "10°", "16°"),
                ForecastDayUi("Ср, 7 авг", "https://openweathermap.org/img/wn/10d@2x.png", "8°", "12°")
            )
        ),
        onRetryClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewNoInternetError() {
    WeatherUi.NoConnectionError.Show(onRetryClick = { }, forecastUi = ForecastUi.Empty)
}

@Preview(showBackground = true)
@Composable
fun PreviewLoading() {
    WeatherUi.Loading.Show(onRetryClick = { }, forecastUi = ForecastUi.Empty)
}