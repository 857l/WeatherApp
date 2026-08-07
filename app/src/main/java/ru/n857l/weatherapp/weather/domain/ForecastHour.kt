package ru.n857l.weatherapp.weather.domain

data class ForecastHour(
    val dateTime: Long,
    val icon: String,
    val description: String,
    val temperature: Float
)