package ru.n857l.weatherapp.weatherscreen.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("data/2.5/weather")
    fun weather(
        @Query("lat") lat: Float,
        @Query("lon") long: Float,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Call<WeatherCloud>
}