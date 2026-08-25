package ru.n857l.weatherapp.weather.presentation

import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import ru.n857l.weatherapp.R
import ru.n857l.weatherapp.core.ResourceProvider
import ru.n857l.weatherapp.weather.domain.WeatherInCity
import ru.n857l.weatherapp.weather.domain.WeatherResult
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt

private const val HPA_TO_MMHG = 0.750062

private fun Int.hPaToMmHg(): Int = (this * HPA_TO_MMHG).roundToInt()

class WeatherUiMapper @Inject constructor(
    private val timeWrapper: TimeWrapper,
    private val resourceProvider: ResourceProvider
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
            description = weatherInCity.description.capitalizedWeatherDescription(),
            temperature = resourceProvider.getString(
                R.string.unit_temperature, weatherInCity.temperature.roundToInt()
            ),
            feelsTemperature = resourceProvider.getString(
                R.string.unit_temperature, weatherInCity.feelsTemperature.roundToInt()
            ),
            minMaxTemperature = resourceProvider.getString(
                R.string.unit_min_max_temperature,
                weatherInCity.tempMin.roundToInt(),
                weatherInCity.tempMax.roundToInt()
            ),
            pressure = resourceProvider.getString(
                R.string.unit_pressure, weatherInCity.pressure.hPaToMmHg()
            ),
            seaLevelPressure = resourceProvider.getString(
                R.string.unit_pressure, weatherInCity.seaLevelPressure.hPaToMmHg()
            ),
            groundLevelPressure = resourceProvider.getString(
                R.string.unit_pressure, weatherInCity.groundLevelPressure.hPaToMmHg()
            ),
            humidity = resourceProvider.getString(R.string.unit_percent, weatherInCity.humidity),
            speed = resourceProvider.getString(R.string.unit_speed, weatherInCity.speed),
            degree = resourceProvider.getString(R.string.unit_degree, weatherInCity.degree),
            gust = resourceProvider.getString(R.string.unit_speed, weatherInCity.gust),
            clouds = resourceProvider.getString(R.string.unit_percent, weatherInCity.clouds),
            visibility = resourceProvider.getString(R.string.unit_visibility, weatherInCity.visibility),
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
            val dateFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
            dateFormat.timeZone = TimeZone.getDefault()
            return dateFormat.format(Date(timeMillis))
        }

        override fun minutesDifference(timeMillis: Long): Boolean {
            return System.currentTimeMillis() - timeMillis > minutes * 60 * 1000
        }

        override fun getDayLabel(timeMillis: Long): String {
            val dateFormat = SimpleDateFormat("EEE, d MMM", Locale.ENGLISH)
            dateFormat.timeZone = TimeZone.getDefault()
            return dateFormat.format(Date(timeMillis))
        }
    }
}
