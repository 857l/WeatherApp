package ru.n857l.weatherapp.weather.data

import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Locale

interface WeatherService {

    @GET("data/2.5/weather")
    suspend fun weather(
        @Query("lat") lat: Float,
        @Query("lon") long: Float,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = Locale.ENGLISH.language
    ): WeatherCloud

    @GET("data/2.5/forecast")
    suspend fun forecast(
        @Query("lat") lat: Float,
        @Query("lon") long: Float,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = Locale.ENGLISH.language
    ): ForecastCloud
}
