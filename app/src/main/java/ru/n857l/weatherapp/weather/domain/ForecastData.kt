package ru.n857l.weatherapp.weather.domain

data class ForecastData(
    val hours: List<ForecastHour>,
    val days: List<ForecastDay>
)