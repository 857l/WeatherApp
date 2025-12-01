package ru.n857l.weatherapp.weatherscreen.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import java.io.Serializable

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel
) {
    val weatherScreenUi = viewModel.state.collectAsStateWithLifeCycle()
    WeatherScreenUi(
        weatherScreenUi
    )
}

@Composable
fun WeatherScreenUi(
    weatherUi: WeatherUi
) {
    Column {
        weatherUi.Show()
    }
}

interface WeatherUi : Serializable {

    @Composable
    fun Show() = Unit

    data object Empty : WeatherUi
}