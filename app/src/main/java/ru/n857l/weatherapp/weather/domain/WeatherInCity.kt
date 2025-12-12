package ru.n857l.weatherapp.weather.domain

import java.io.Serializable

data class WeatherInCity(
    val cityName: String,
    val temperature: Float,
    val feelsTemperature: Float,
    val tempMin: Float,
    val tempMax: Float,
    val pressure: Int,
    val humidity: Int,
    val seaLevelPressure: Int,
    val groundLevelPressure: Int,
    val speed: Float,
    val degree: Int,
    val gust: Float,
    val clouds: Int,
    val dateTime: Long,
    val sunrise: Long,
    val sunset: Long,
    val visibility: Int
) : Serializable