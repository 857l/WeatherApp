package ru.n857l.weatherapp.weather.domain

import java.io.Serializable

data class WeatherInCity(
    val cityName: String,
    val temperature: Float
) : Serializable