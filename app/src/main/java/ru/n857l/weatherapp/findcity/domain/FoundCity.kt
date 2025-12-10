package ru.n857l.weatherapp.findcity.domain

import java.io.Serializable
import java.util.Locale

data class FoundCity(
    val name: String,
    val latitude: Float,
    val longitude: Float,
    private val countryCode: String,
    private val state: String? = null
) : Serializable {

    val fullCountryName: String
        get() = buildString {
            append(Locale("", countryCode).displayCountry)

            if (state != null) {
                append(", $state")
            }
        }
}