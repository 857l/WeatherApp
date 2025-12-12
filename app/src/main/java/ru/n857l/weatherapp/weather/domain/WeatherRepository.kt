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
                val cloud = cloudDataSource.temperature(latitude, longitude)

                val weatherInCity = WeatherInCity(
                    cityName = cityName,
                    temperature = cloud.main.temperature,
                    feelsTemperature = cloud.main.feelsTemperature,
                    pressure = cloud.main.pressure,
                    humidity = cloud.main.humidity,
                    seaLevelPressure = cloud.main.seaLevelPressure,
                    groundLevelPressure = cloud.main.groundLevelPressure,
                    speed = cloud.wind.speed,
                    degree = cloud.wind.degree,
                    gust = cloud.wind.gust,
                    clouds = cloud.clouds.clouds,
                    dateTime = cloud.dateTime,
                    sunrise = cloud.sun.sunrise,
                    sunset = cloud.sun.sunset,
                    visibility = cloud.visibility
                )

                return WeatherResult.Base(weatherInCity)
            } catch (e: DomainException) {
                return WeatherResult.Failed(e)
            }
        }
    }
}