package ru.n857l.weatherapp.weather.domain

data class ForecastDay(
    val date: Long,
    val icon: String,
    val description: String,
    val tempMin: Float,
    val tempMax: Float
)