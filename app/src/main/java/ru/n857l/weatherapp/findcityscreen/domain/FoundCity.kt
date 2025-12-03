package ru.n857l.weatherapp.findcityscreen.domain

import java.io.Serializable

data class FoundCity(
    val name: String,
    val latitude: Float,
    val longitude: Float
) : Serializable