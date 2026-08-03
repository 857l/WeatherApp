package ru.n857l.weatherapp.weather.presentation

import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import ru.n857l.weatherapp.weather.domain.WeatherInCity
import ru.n857l.weatherapp.weather.domain.WeatherResult
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt

class WeatherUiMapper @Inject constructor(
    private val timeWrapper: TimeWrapper
) : WeatherResult.Mapper<WeatherUi> {

    override fun mapEmpty(): WeatherUi = WeatherUi.Empty

    override fun mapNoInternetError(): WeatherUi = WeatherUi.NoConnectionError

    override fun mapServiceUnavailableError(): WeatherUi = WeatherUi.ServiceUnavailableError

    override fun mapWeather(
        weatherInCity: WeatherInCity
    ): WeatherUi {
        return WeatherUi.Base(
            cityName = weatherInCity.cityName,
            iconUrl = "https://openweathermap.org/img/wn/${weatherInCity.icon}@4x.png",
            description = weatherInCity.description.replaceFirstChar { it.uppercase() },
            temperature = "${weatherInCity.temperature.roundToInt()}°",
            feelsTemperature = "Feels like ${weatherInCity.feelsTemperature.roundToInt()}°",
            minMaxTemperature = "↑${weatherInCity.tempMin.roundToInt()}° / ↓${weatherInCity.tempMax.roundToInt()}°",
            pressure = "${weatherInCity.pressure} мм рт. ст.",
            seaLevelPressure = "${weatherInCity.seaLevelPressure} мм рт. ст.",
            groundLevelPressure = "${weatherInCity.groundLevelPressure} мм рт. ст.",
            humidity = "${weatherInCity.humidity}%",
            speed = "${weatherInCity.speed} м/c",
            degree = "${weatherInCity.degree}°",
            gust = "${weatherInCity.gust}",
            clouds = "${weatherInCity.clouds}%",
            visibility = "${weatherInCity.visibility} м",
            sunrise = timeWrapper.getShortTime(weatherInCity.sunrise),
            sunset = timeWrapper.getShortTime(weatherInCity.sunset),
            time = timeWrapper.getHumanReadableTime(weatherInCity.dateTime)
        )
    }
}

interface TimeWrapper {

    fun getHumanReadableTime(timeMillis: Long): String

    fun getShortTime(timeMillis: Long): String

    fun minutesDifference(timeMillis: Long): Boolean

    class Base @Inject constructor(
        private val minutes: Int
    ) : TimeWrapper {

        override fun getHumanReadableTime(timeMillis: Long): String {
            val dateFormat = SimpleDateFormat("HH:mm dd MMM yyyy", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getDefault()
            val time = dateFormat.format(Date(timeMillis))
            return time
        }

        override fun getShortTime(timeMillis: Long): String {
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getDefault()
            return dateFormat.format(Date(timeMillis))
        }

        override fun minutesDifference(timeMillis: Long): Boolean {
            return System.currentTimeMillis() - timeMillis > minutes * 60 * 1000
        }
    }
}