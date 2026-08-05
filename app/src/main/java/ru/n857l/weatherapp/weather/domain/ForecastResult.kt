package ru.n857l.weatherapp.weather.domain

import ru.n857l.weatherapp.findcity.domain.DomainException
import ru.n857l.weatherapp.findcity.domain.NoInternetException
import java.io.Serializable

interface ForecastResult {

    fun <T : Serializable> map(mapper: Mapper<T>): T

    interface Mapper<T : Serializable> {

        fun mapEmpty(): T

        fun mapForecast(
            days: List<ForecastDay>
        ): T

        fun mapNoInternetError(): T

        fun mapServiceUnavailableError(): T
    }

    data class Base(
        private val days: List<ForecastDay>
    ) : ForecastResult {

        override fun <T : Serializable> map(mapper: Mapper<T>): T {
            return mapper.mapForecast(days)
        }
    }

    data class Failed(private val error: DomainException) : ForecastResult {
        override fun <T : Serializable> map(mapper: Mapper<T>): T =
            if (error is NoInternetException)
                mapper.mapNoInternetError()
            else
                mapper.mapServiceUnavailableError()
    }
}