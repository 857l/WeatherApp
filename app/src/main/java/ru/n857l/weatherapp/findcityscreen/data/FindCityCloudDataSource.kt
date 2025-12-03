package ru.n857l.weatherapp.findcityscreen.data

import javax.inject.Inject

interface FindCityCloudDataSource {

    suspend fun findCity(query: String): FoundCityCloud

    class Base @Inject constructor(
        private val service: FindCityService
    ) : FindCityCloudDataSource {

        override suspend fun findCity(query: String): FoundCityCloud {
            val result = service.findCity(query, API_KEY).execute()
            val list: List<FoundCityCloud> = result.body()!!
            return if (list.isEmpty())
                FoundCityCloud("", 0f, 0f)
            else
                list.first()
        }
    }
}

const val API_KEY = "2162167f0ff42eadb8f9492979a90b52"