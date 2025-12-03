package ru.n857l.weatherapp.weatherscreen.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.glance.text.Text
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.n857l.weatherapp.weatherscreen.domain.WeatherInCity
import java.io.Serializable

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel
) {
    val weatherScreenUi = viewModel.state.collectAsStateWithLifecycle()
    WeatherScreenUi(
        weatherScreenUi.value
    )
}

@Composable
fun WeatherScreenUi(
    weatherUi: WeatherUi
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        weatherUi.Show()
    }
}

interface WeatherUi : Serializable {

    @Composable
    fun Show() = Unit

    data object Empty : WeatherUi {
        private fun readResolve(): Any = Empty
    }

    data class Base(
        private val weatherInCity: WeatherInCity
    ) : WeatherUi {

        @Composable
        override fun Show() {
            Text(
                text = weatherInCity.cityName
            )
            Text(
                text = weatherInCity.temperature.toString() + "°C"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWeatherScreenUi() {
    WeatherScreenUi(
        weatherUi = WeatherUi.Base(
            WeatherInCity(
                cityName = "Moscow",
                temperature = 12.1f
            )
        )
    )
}