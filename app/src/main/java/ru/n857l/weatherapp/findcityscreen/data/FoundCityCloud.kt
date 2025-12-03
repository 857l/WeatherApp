package ru.n857l.weatherapp.findcityscreen.data

import com.google.gson.annotations.SerializedName

data class FoundCityCloud(
    @SerializedName("name")
    val name: String,
    @SerializedName("lat")
    val latitude: Float,
    @SerializedName("lon")
    val longitude: Float
)