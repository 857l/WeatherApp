package ru.n857l.weatherapp.weatherscreen.domain

import ru.n857l.weatherapp.weatherscreen.data.WeatherCacheDataSource
import ru.n857l.weatherapp.weatherscreen.data.WeatherCloudDataSource
import javax.inject.Inject

interface WeatherRepository {

    suspend fun weather(): WeatherInCity

    class Base @Inject constructor(
        private val cacheDataSource: WeatherCacheDataSource,
        private val cloudDataSource: WeatherCloudDataSource
    ) : WeatherRepository {

        override suspend fun weather(): WeatherInCity {
            val (latitude, longitude, cityName) = cacheDataSource.cityParams()
            val temperature = cloudDataSource.temperature(latitude, longitude)
            return WeatherInCity(cityName, temperature)
        }
    }
}