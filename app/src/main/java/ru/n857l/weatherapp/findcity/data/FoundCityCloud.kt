package ru.n857l.weatherapp.findcity.data

import com.google.gson.annotations.SerializedName

data class FoundCityCloud(
    @SerializedName("name")
    val name: String,
    @SerializedName("lat")
    val latitude: Float,
    @SerializedName("lon")
    val longitude: Float,
    @SerializedName("country")
    val countryName: String,
    @SerializedName("state")
    val state: String?
)