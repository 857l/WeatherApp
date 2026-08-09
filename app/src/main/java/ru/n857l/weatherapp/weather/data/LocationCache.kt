package ru.n857l.weatherapp.weather.data

interface LocationCache {
    val lat: Float
    val lon: Float
    val dateTime: Long
}