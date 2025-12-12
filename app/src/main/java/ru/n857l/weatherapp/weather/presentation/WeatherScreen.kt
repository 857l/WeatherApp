package ru.n857l.weatherapp.weather.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.n857l.weatherapp.findcity.presentation.LoadingUi
import ru.n857l.weatherapp.findcity.presentation.NoConnectionErrorUi
import java.io.Serializable

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel
) {
    val weatherScreenUi = viewModel.state.collectAsStateWithLifecycle()
    WeatherScreenUi(
        weatherUi = weatherScreenUi.value,
        onRetryClick = viewModel::loadWeather
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
        val pressure: String,
        val humidity: String,
        val seaLevelPressure: String,
        val groundLevelPressure: String,
        val speed: String,
        val degree: String,
        val gust: String,
        val clouds: String,
        val visibility: String
    ) : WeatherUi {

        @Composable
        override fun Show(onRetryClick: () -> Unit) {
            Text(
                text = cityName,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = temperature,
                fontSize = 48.sp
            )
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

//@Preview(showBackground = true)
//@Composable
//fun PreviewWeatherScreenUi() {
//    WeatherScreenUi(
//        weatherUi = WeatherUi.Base(
//                cityName = "Moscow",
//            temperature = "12.1°C"
//        ),
//        onRetryClick = {}
//    )
//} TODO

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