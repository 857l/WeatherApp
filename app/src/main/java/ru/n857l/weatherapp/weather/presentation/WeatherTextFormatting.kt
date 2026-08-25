package ru.n857l.weatherapp.weather.presentation

import java.util.Locale

fun String.capitalizedWeatherDescription(): String =
    trim().replaceFirstChar { char ->
        if (char.isLowerCase()) char.titlecase(Locale.ENGLISH) else char.toString()
    }
