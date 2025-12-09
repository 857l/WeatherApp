package ru.n857l.weatherapp.weather.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
        modifier = Modifier.fillMaxSize()
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
        val temperature: String
    ) : WeatherUi {

        @Composable
        override fun Show(onRetryClick: () -> Unit) {
            Text(text = cityName)
            Text(text = temperature)
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
            temperature = "12.1°C"
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