package ru.n857l.weatherapp.weather.data

import com.google.gson.annotations.SerializedName

data class WeatherCloud(
    @SerializedName("main") val main: MainCloud
)

data class MainCloud(
    @SerializedName("temp") val temperature: Float
)