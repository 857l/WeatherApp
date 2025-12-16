package ru.n857l.weatherapp.weather.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ru.n857l.weatherapp.findcity.presentation.LoadingUi
import ru.n857l.weatherapp.findcity.presentation.NoConnectionErrorUi
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
    WeatherScreenUi(
        weatherUi = weatherScreenUi.value,
        onRetryClick = viewModel::loadWeather,
    )
}

@Composable
fun WeatherScreenUi(
    weatherUi: WeatherUi,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        weatherUi.Show(onRetryClick)
    }
}

interface WeatherUi : Serializable {

    @Composable
    fun Show(onRetryClick: () -> Unit) = Unit

    data object Empty : WeatherUi {
        private fun readResolve(): Any = Empty
    }

    data class Base(
        val cityName: String,
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
        val time: String
    ) : WeatherUi {

        @Composable
        override fun Show(onRetryClick: () -> Unit) {
            Text(
                text = cityName,
                fontSize = 24.sp
            )
            Text(
                text = temperature,
                fontSize = 42.sp,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            Text(text = minMaxTemperature)
            Text(text = feelsTemperature)
            Text(text = pressure)
            Text(text = humidity)
            Text(text = seaLevelPressure)
            Text(text = groundLevelPressure)
            Text(text = speed)
            Text(text = degree)
            Text(text = gust)
            Text(text = time)
        }
    }

    data object NoConnectionError : WeatherUi {
        private fun readResolve(): Any = NoConnectionError

        @Composable
        override fun Show(onRetryClick: () -> Unit) {
            NoConnectionErrorUi(onRetryClick)
        }
    }

    data object Loading : WeatherUi {
        private fun readResolve(): Any = Loading

        @Composable
        override fun Show(onRetryClick: () -> Unit) {
            LoadingUi()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWeatherScreenUi() {
    WeatherScreenUi(
        weatherUi = WeatherUi.Base(
            cityName = "Moscow",
            temperature = "12.1°",
            feelsTemperature = "Feels like 12.1°",
            pressure = "12.1 мм рт. ст.",
            humidity = "60%",
            seaLevelPressure = "12.1 мм рт. ст.",
            groundLevelPressure = "12.1 мм рт. ст.",
            speed = "12.1 м/c",
            degree = "12.1°",
            gust = "12.1",
            clouds = "12",
            visibility = "12 м",
            minMaxTemperature = "↑12.1° / ↓12.1°",
            time = "12:11"
        ),
        onRetryClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewNoInternetError() {
    WeatherUi.NoConnectionError.Show { }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoading() {
    WeatherUi.Loading.Show { }
}