package ru.n857l.weatherapp.findcity.data

import ru.n857l.weatherapp.BuildConfig
import ru.n857l.weatherapp.core.safeCall
import javax.inject.Inject

interface FindCityCloudDataSource {

    suspend fun findCity(query: String): List<FoundCityCloud>

    class Base @Inject constructor(
        private val service: FindCityService
    ) : FindCityCloudDataSource {

        override suspend fun findCity(query: String): List<FoundCityCloud> =
            safeCall { service.findCity(query = query, apiKey = API_KEY) }
    }
}

const val API_KEY = BuildConfig.OWM_API_KEY