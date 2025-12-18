package ru.n857l.weatherapp.weather.domain

import ru.n857l.weatherapp.findcity.data.FindCityDao
import ru.n857l.weatherapp.findcity.domain.DomainException
import ru.n857l.weatherapp.findcity.domain.ServiceUnavailableException
import ru.n857l.weatherapp.weather.data.WeatherCloudDataSource
import ru.n857l.weatherapp.weather.data.WeatherDao
import ru.n857l.weatherapp.weather.data.WeatherEntity
import ru.n857l.weatherapp.weather.presentation.TimeWrapper
import javax.inject.Inject

interface WeatherRepository {

    suspend fun weather(): WeatherResult

    class Base @Inject constructor(
        private val findCityDao: FindCityDao,
        private val weatherDao: WeatherDao,
        private val cloudDataSource: WeatherCloudDataSource,
        private val timeWrapper: TimeWrapper
    ) : WeatherRepository {

        override suspend fun weather(): WeatherResult {
            try {
                val city = findCityDao.getCity()
                    ?: return WeatherResult.Failed(ServiceUnavailableException)

                val cached = weatherDao.getWeather()

                val needRefresh =
                    cached == null ||
                            cached.lat != city.lat ||
                            cached.lon != city.lon ||
                            timeWrapper.minutesDifference(cached.dateTime)

                if (needRefresh) {
                    val cloud = cloudDataSource.weather(city.lat, city.lon)

                    val entity = WeatherEntity(
                        cityName = cloud.cityName,
                        lat = cloud.coordinates.latitude,
                        lon = cloud.coordinates.longitude,
                        temperature = cloud.main.temperature,
                        feelsTemperature = cloud.main.feelsTemperature,
                        tempMin = cloud.main.tempMin,
                        tempMax = cloud.main.tempMax,
                        pressure = cloud.main.pressure,
                        humidity = cloud.main.humidity,
                        seaLevelPressure = cloud.main.seaLevelPressure,
                        groundLevelPressure = cloud.main.groundLevelPressure,
                        speed = cloud.wind.speed,
                        degree = cloud.wind.degree,
                        gust = cloud.wind.gust,
                        clouds = cloud.clouds.clouds,
                        visibility = cloud.visibility,
                        dateTime = cloud.dateTime * 1000L,
                        sunrise = cloud.sun.sunrise * 1000L,
                        sunset = cloud.sun.sunset * 1000L
                    )

                    weatherDao.saveWeather(entity)
                    return WeatherResult.Base(entity.toDomain())
                }

                return WeatherResult.Base(cached!!.toDomain())

            } catch (e: DomainException) {
                return WeatherResult.Failed(e)
            }
        }
    }
}