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
            iconUrl = weatherIconUrl(weatherInCity.icon, size = 4),
            description = weatherInCity.description.replaceFirstChar { it.uppercase() },
            temperature = "${weatherInCity.temperature.roundToInt()}°",
            feelsTemperature = "${weatherInCity.feelsTemperature.roundToInt()}°",
            minMaxTemperature = "↑${weatherInCity.tempMin.roundToInt()}° / ↓${weatherInCity.tempMax.roundToInt()}°",
            pressure = "${weatherInCity.pressure} мм рт. ст.",
            seaLevelPressure = "${weatherInCity.seaLevelPressure} мм рт. ст.",
            groundLevelPressure = "${weatherInCity.groundLevelPressure} мм рт. ст.",
            humidity = "${weatherInCity.humidity}%",
            speed = "${weatherInCity.speed} м/c",
            degree = "${weatherInCity.degree}°",
            gust = "${weatherInCity.gust} м/c",
            clouds = "${weatherInCity.clouds}%",
            visibility = "${weatherInCity.visibility} м",
            sunrise = timeWrapper.getShortTime(weatherInCity.sunrise),
            sunset = timeWrapper.getShortTime(weatherInCity.sunset),
            dateTime = weatherInCity.dateTime
        )
    }
}

interface TimeWrapper {

    fun getShortTime(timeMillis: Long): String

    fun minutesDifference(timeMillis: Long): Boolean

    fun getDayLabel(timeMillis: Long): String

    class Base @Inject constructor(
        private val minutes: Int
    ) : TimeWrapper {

        override fun getShortTime(timeMillis: Long): String {
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getDefault()
            return dateFormat.format(Date(timeMillis))
        }

        override fun minutesDifference(timeMillis: Long): Boolean {
            return System.currentTimeMillis() - timeMillis > minutes * 60 * 1000
        }

        override fun getDayLabel(timeMillis: Long): String {
            val dateFormat = SimpleDateFormat("EEE, d MMM", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getDefault()
            return dateFormat.format(Date(timeMillis))
        }
    }
}