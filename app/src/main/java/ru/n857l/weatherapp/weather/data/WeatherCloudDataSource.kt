package ru.n857l.weatherapp.weather.data

import ru.n857l.weatherapp.core.safeCall
import ru.n857l.weatherapp.findcity.data.API_KEY
import javax.inject.Inject

interface WeatherCloudDataSource {

    suspend fun weather(latitude: Float, longitude: Float): WeatherCloud

    suspend fun forecast(latitude: Float, longitude: Float): ForecastCloud

    class Base @Inject constructor(
        private val service: WeatherService
    ) : WeatherCloudDataSource {

        override suspend fun weather(latitude: Float, longitude: Float): WeatherCloud =
            safeCall { service.weather(latitude, longitude, API_KEY) }

        override suspend fun forecast(latitude: Float, longitude: Float): ForecastCloud =
            safeCall { service.forecast(latitude, longitude, API_KEY) }
    }
}