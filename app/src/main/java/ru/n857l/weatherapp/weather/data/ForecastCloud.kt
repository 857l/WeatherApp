package ru.n857l.weatherapp.weather.data

import com.google.gson.annotations.SerializedName

data class ForecastCloud(
    @SerializedName("list")
    val list: List<ForecastItemCloud>,
    @SerializedName("city")
    val city: ForecastCity
)

data class ForecastItemCloud(
    @SerializedName("dt")
    val dateTime: Long,
    @SerializedName("main")
    val main: Main,
    @SerializedName("weather")
    val weather: List<WeatherDescription>,
    @SerializedName("dt_txt")
    val dateTimeText: String
)

data class ForecastCity(
    @SerializedName("name")
    val name: String
)