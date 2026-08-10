package ru.n857l.weatherapp.weather.domain

import ru.n857l.weatherapp.core.DomainException
import ru.n857l.weatherapp.core.NoInternetException
import java.io.Serializable

interface WeatherResult {

    fun <T : Serializable> map(mapper: Mapper<T>): T

    interface Mapper<T : Serializable> {

        fun mapEmpty(): T

        fun mapWeather(
            weatherInCity: WeatherInCity
        ): T

        fun mapNoInternetError(): T

        fun mapServiceUnavailableError(): T
    }

    data class Base(
        private val weatherInCity: WeatherInCity
    ) : WeatherResult {

        override fun <T : Serializable> map(mapper: Mapper<T>): T {
            return mapper.mapWeather(weatherInCity)
        }
    }

    data class Failed(private val error: DomainException) : WeatherResult {
        override fun <T : Serializable> map(mapper: Mapper<T>): T =
            if (error is NoInternetException)
                mapper.mapNoInternetError()
            else
                mapper.mapServiceUnavailableError()
    }
}