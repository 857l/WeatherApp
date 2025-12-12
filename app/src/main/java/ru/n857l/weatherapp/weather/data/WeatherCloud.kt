package ru.n857l.weatherapp.weather.data

import com.google.gson.annotations.SerializedName

data class WeatherCloud(
    @SerializedName("main")
    val main: Main,
    @SerializedName("visibility")
    val visibility: Int,
    @SerializedName("wind")
    val wind: Wind,
    @SerializedName("clouds")
    val clouds: Clouds,
    @SerializedName("dt")
    val dateTime: Long,
    @SerializedName("sys")
    val sun: Sun
)

data class Main(
    @SerializedName("temp")
    val temperature: Float,
    @SerializedName("feels_like")
    val feelsTemperature: Float,
    @SerializedName("temp_min")
    val tempMin: Float,
    @SerializedName("temp_max")
    val tempMax: Float,
    @SerializedName("pressure")
    val pressure: Int,
    @SerializedName("humidity")
    val humidity: Int,
    @SerializedName("sea_level")
    val seaLevelPressure: Int,
    @SerializedName("grnd_level")
    val groundLevelPressure: Int
)

data class Wind(
    @SerializedName("speed")
    val speed: Float,
    @SerializedName("deg")
    val degree: Int,
    @SerializedName("gust")
    val gust: Float
)

data class Clouds(
    @SerializedName("all")
    val clouds: Int
)

data class Sun(
    @SerializedName("sunrise")
    val sunrise: Long,
    @SerializedName("sunset")
    val sunset: Long
)