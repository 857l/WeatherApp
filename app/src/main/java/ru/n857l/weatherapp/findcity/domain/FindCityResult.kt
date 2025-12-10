package ru.n857l.weatherapp.findcity.domain

import java.io.Serializable

interface FindCityResult {

    fun <T : Serializable> map(mapper: Mapper<T>): T

    interface Mapper<T : Serializable> {

        fun mapFoundCity(foundCities: List<FoundCity>): T

        fun mapEmpty(): T

        fun mapNoInternetError(): T
    }

    data class Base(
        private val foundCities: List<FoundCity>
    ) : FindCityResult {

        override fun <T : Serializable> map(mapper: Mapper<T>): T {
            return mapper.mapFoundCity(foundCities)
        }
    }

    data class Failed(private val error: DomainException) : FindCityResult {
        override fun <T : Serializable> map(mapper: Mapper<T>): T {
            if (error is NoInternetException)
                return mapper.mapNoInternetError()
            else
                TODO("to be done next time")
            //return mapper.mapGenericError()
        }
    }

    data object Empty : FindCityResult {

        override fun <T : Serializable> map(mapper: Mapper<T>): T {
            return mapper.mapEmpty()
        }
    }
}