package ru.n857l.weatherapp.weather.domain

import ru.n857l.weatherapp.findcity.data.FindCityDao
import ru.n857l.weatherapp.findcity.domain.DomainException
import ru.n857l.weatherapp.findcity.domain.ServiceUnavailableException
import ru.n857l.weatherapp.weather.data.ForecastItemCloud
import ru.n857l.weatherapp.weather.data.WeatherCloudDataSource
import ru.n857l.weatherapp.weather.data.WeatherDao
import ru.n857l.weatherapp.weather.data.WeatherEntity
import ru.n857l.weatherapp.weather.presentation.TimeWrapper
import javax.inject.Inject
import kotlin.math.abs

interface WeatherRepository {

    suspend fun weather(): WeatherResult

    suspend fun forecast(): ForecastResult

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
                    val weatherDescription = cloud.weather.firstOrNull()

                    val entity = WeatherEntity(
                        cityName = cloud.cityName,
                        icon = weatherDescription?.icon ?: "01d",
                        description = weatherDescription?.description ?: "",
                        lat = city.lat,
                        lon = city.lon,
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
                        dateTime = System.currentTimeMillis(),
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

        override suspend fun forecast(): ForecastResult {
            try {
                val city = findCityDao.getCity()
                    ?: return ForecastResult.Failed(ServiceUnavailableException)

                val cloud = cloudDataSource.forecast(city.lat, city.lon)

                val days = cloud.list
                    .groupBy { item -> item.dateTimeText.substring(0, 10) }
                    .values
                    .map { itemsForDay -> itemsForDay.toForecastDay() }

                return ForecastResult.Base(days)
            } catch (e: DomainException) {
                return ForecastResult.Failed(e)
            }
        }

        private fun List<ForecastItemCloud>.toForecastDay(): ForecastDay {
            val noonItem = minByOrNull { item ->
                val hour = item.dateTimeText.substring(11, 13).toInt()
                abs(hour - 12)
            } ?: first()

            val weatherDescription = noonItem.weather.firstOrNull()

            return ForecastDay(
                date = noonItem.dateTime * 1000L,
                icon = weatherDescription?.icon ?: "01d",
                description = weatherDescription?.description ?: "",
                tempMin = minOf { item -> item.main.tempMin },
                tempMax = maxOf { item -> item.main.tempMax }
            )
        }
    }
}