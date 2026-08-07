package ru.n857l.weatherapp.weather.presentation

fun weatherIconUrl(icon: String, size: Int = 2): String =
    "https://openweathermap.org/img/wn/$icon@${size}x.png"