package ru.n857l.weatherapp.weather.presentation

import ru.n857l.weatherapp.weather.domain.WeatherResult
import javax.inject.Inject

class WeatherUiMapper @Inject constructor() : WeatherResult.Mapper<WeatherUi> {

    override fun mapEmpty(): WeatherUi {
        return WeatherUi.Empty
    }

    override fun mapWeather(
        cityName: String,
        temperature: Float
    ): WeatherUi {
        return WeatherUi.Base(cityName, "$temperature°C")
    }

    override fun mapNoInternetError(): WeatherUi {
        return WeatherUi.NoConnectionError
    }
}