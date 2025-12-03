package ru.n857l.weatherapp.weather.domain

import ru.n857l.weatherapp.findcity.domain.DomainException
import ru.n857l.weatherapp.weather.data.WeatherCacheDataSource
import ru.n857l.weatherapp.weather.data.WeatherCloudDataSource
import javax.inject.Inject

interface WeatherRepository {

    suspend fun weather(): WeatherResult

    class Base @Inject constructor(
        private val cacheDataSource: WeatherCacheDataSource,
        private val cloudDataSource: WeatherCloudDataSource
    ) : WeatherRepository {

        override suspend fun weather(): WeatherResult {
            try {
                val (latitude, longitude, cityName) = cacheDataSource.cityParams()
                val temperature = cloudDataSource.temperature(latitude, longitude)
                return WeatherResult.Base(WeatherInCity(cityName, temperature))
            } catch (e: DomainException) {
                return WeatherResult.Failed(e)
            }
        }
    }
}