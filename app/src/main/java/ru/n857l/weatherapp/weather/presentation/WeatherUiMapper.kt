package ru.n857l.weatherapp.weather.presentation

import ru.n857l.weatherapp.weather.domain.WeatherInCity
import ru.n857l.weatherapp.weather.domain.WeatherResult
import javax.inject.Inject
import kotlin.math.roundToInt

class WeatherUiMapper @Inject constructor() : WeatherResult.Mapper<WeatherUi> {

    override fun mapEmpty(): WeatherUi {
        return WeatherUi.Empty
    }

    override fun mapNoInternetError(): WeatherUi {
        return WeatherUi.NoConnectionError
    }

    override fun mapWeather(
        weatherInCity: WeatherInCity
    ): WeatherUi {
        return WeatherUi.Base(
            cityName = weatherInCity.cityName,
            temperature = "${weatherInCity.temperature.roundToInt()}°C",
            feelsTemperature = "${weatherInCity.feelsTemperature.roundToInt()}°C",
            pressure = "${weatherInCity.pressure} мм рт. ст.",
            seaLevelPressure = "${weatherInCity.seaLevelPressure} мм рт. ст.",
            groundLevelPressure = "${weatherInCity.groundLevelPressure} мм рт. ст.",
            humidity = "${weatherInCity.humidity}%",
            speed = "${weatherInCity.speed} м/c",
            degree = "${weatherInCity.degree}°",
            gust = "${weatherInCity.gust}",
            clouds = "${weatherInCity.clouds}",
            visibility = "${weatherInCity.visibility} м"
        )
    }
}