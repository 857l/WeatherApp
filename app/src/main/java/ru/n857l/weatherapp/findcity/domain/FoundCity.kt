package ru.n857l.weatherapp.findcity.domain

import java.io.Serializable
import java.util.Locale

data class FoundCity(
    val name: String,
    val latitude: Float,
    val longitude: Float,
    val countryCode: String
) : Serializable {

    val countryName: String
        get() = Locale("", countryCode).displayCountry
}