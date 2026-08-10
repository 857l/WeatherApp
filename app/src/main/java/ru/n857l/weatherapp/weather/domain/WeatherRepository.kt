package ru.n857l.weatherapp.weather.domain

import ru.n857l.weatherapp.findcity.data.FindCityDao
import ru.n857l.weatherapp.findcity.data.FindCityEntity
import ru.n857l.weatherapp.core.DomainException
import ru.n857l.weatherapp.core.ServiceUnavailableException
import ru.n857l.weatherapp.weather.data.ForecastCacheEntity
import ru.n857l.weatherapp.weather.data.ForecastDao
import ru.n857l.weatherapp.weather.data.ForecastItemCloud
import ru.n857l.weatherapp.weather.data.LocationCache
import ru.n857l.weatherapp.weather.data.WeatherCloudDataSource
import ru.n857l.weatherapp.weather.data.WeatherDao
import ru.n857l.weatherapp.weather.data.WeatherEntity
import ru.n857l.weatherapp.weather.presentation.TimeWrapper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import kotlin.math.abs

private val localDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
    timeZone = TimeZone.getDefault()
}
private val localHourFormat = SimpleDateFormat("HH", Locale.getDefault()).apply {
    timeZone = TimeZone.getDefault()
}

private fun ForecastItemCloud.localDateKey(): String =
    localDateFormat.format(Date(dateTime * 1000L))

private fun ForecastItemCloud.localHour(): Int =
    localHourFormat.format(Date(dateTime * 1000L)).toInt()

interface WeatherRepository {

    suspend fun weather(): WeatherResult

    suspend fun forecast(): ForecastResult

    class Base @Inject constructor(
        private val findCityDao: FindCityDao,
        private val weatherDao: WeatherDao,
        private val forecastDao: ForecastDao,
        private val cloudDataSource: WeatherCloudDataSource,
        private val timeWrapper: TimeWrapper
    ) : WeatherRepository {

        private fun needsRefresh(cached: LocationCache?, city: FindCityEntity): Boolean =
            cached == null ||
                    cached.lat != city.lat ||
                    cached.lon != city.lon ||
                    timeWrapper.minutesDifference(cached.dateTime)

        override suspend fun weather(): WeatherResult {
            try {
                val city = findCityDao.getCity()
                    ?: return WeatherResult.Failed(ServiceUnavailableException)

                val cached = weatherDao.getWeather()

                val needRefresh = needsRefresh(cached, city)

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

                val cached = forecastDao.getForecast()

                val needRefresh = needsRefresh(cached, city)

                if (needRefresh) {
                    val cloud = cloudDataSource.forecast(city.lat, city.lon)

                    val days = cloud.list
                        .groupBy { item -> item.localDateKey() }
                        .values
                        .map { itemsForDay -> itemsForDay.toForecastDay() }

                    val hours = cloud.list.toTodayHours()

                    val data = ForecastData(hours = hours, days = days)

                    forecastDao.saveForecast(
                        ForecastCacheEntity.from(
                            lat = city.lat,
                            lon = city.lon,
                            dateTime = System.currentTimeMillis(),
                            data = data
                        )
                    )

                    return ForecastResult.Base(data)
                }

                return ForecastResult.Base(cached!!.toDomain())
            } catch (e: DomainException) {
                return ForecastResult.Failed(e)
            }
        }

        private fun List<ForecastItemCloud>.toForecastDay(): ForecastDay {
            val noonItem = minByOrNull { item -> abs(item.localHour() - 12) } ?: first()

            val weatherDescription = noonItem.weather.firstOrNull()

            return ForecastDay(
                date = noonItem.dateTime * 1000L,
                icon = weatherDescription?.icon ?: "01d",
                description = weatherDescription?.description ?: "",
                tempMin = minOf { item -> item.main.tempMin },
                tempMax = maxOf { item -> item.main.tempMax }
            )
        }

        private fun List<ForecastItemCloud>.toTodayHours(): List<ForecastHour> {
            val now = System.currentTimeMillis()
            val todayKey = localDateFormat.format(Date(now))
            val tomorrowKey = localDateFormat.format(Date(now + 24 * 60 * 60 * 1000L))

            return this
                .filter { item ->
                    val itemMillis = item.dateTime * 1000L
                    itemMillis >= now &&
                            ((item.localDateKey() == todayKey && item.localHour() >= 6) ||
                                    (item.localDateKey() == tomorrowKey && item.localHour() <= 6))
                }
                .map { item ->
                    val weatherDescription = item.weather.firstOrNull()
                    ForecastHour(
                        dateTime = item.dateTime * 1000L,
                        icon = weatherDescription?.icon ?: "01d",
                        description = weatherDescription?.description ?: "",
                        temperature = item.main.temperature
                    )
                }
        }
    }
}