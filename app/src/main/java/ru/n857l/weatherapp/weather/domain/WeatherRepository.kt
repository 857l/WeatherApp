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
                val (latitude, longitude) = cacheDataSource.cityParams()
                val weatherCloud = cloudDataSource.weather(latitude, longitude)

                val weatherInCity = WeatherInCity(
                    cityName = weatherCloud.cityName,
                    temperature = weatherCloud.main.temperature,
                    feelsTemperature = weatherCloud.main.feelsTemperature,
                    tempMin = weatherCloud.main.tempMin,
                    tempMax = weatherCloud.main.tempMax,
                    pressure = weatherCloud.main.pressure,
                    humidity = weatherCloud.main.humidity,
                    seaLevelPressure = weatherCloud.main.seaLevelPressure,
                    groundLevelPressure = weatherCloud.main.groundLevelPressure,
                    speed = weatherCloud.wind.speed,
                    degree = weatherCloud.wind.degree,
                    gust = weatherCloud.wind.gust,
                    clouds = weatherCloud.clouds.clouds,
                    dateTime = weatherCloud.dateTime,
                    sunrise = weatherCloud.sun.sunrise,
                    sunset = weatherCloud.sun.sunset,
                    visibility = weatherCloud.visibility
                )

                return WeatherResult.Base(weatherInCity)
            } catch (e: DomainException) {
                return WeatherResult.Failed(e)
            }
        }
    }
}