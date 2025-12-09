package ru.n857l.weatherapp.findcity.data

import ru.n857l.weatherapp.findcity.domain.NoInternetException
import java.io.IOException
import javax.inject.Inject

interface FindCityCloudDataSource {

    suspend fun findCity(query: String): List<FoundCityCloud>

    class Base @Inject constructor(
        private val service: FindCityService
    ) : FindCityCloudDataSource {

        override suspend fun findCity(query: String): List<FoundCityCloud> {
            try {
                val result = service.findCity(query, API_KEY).execute()
                val list: List<FoundCityCloud> = result.body()!!
                return list
            } catch (e: Exception) {
                if (e is IOException)
                    throw NoInternetException
                //todo else throw generic error
                throw e
            }
        }
    }
}

const val API_KEY = "a769a571ff80f294c7dcc54dbe3783a3"