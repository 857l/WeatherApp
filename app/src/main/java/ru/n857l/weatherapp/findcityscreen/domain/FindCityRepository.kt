package ru.n857l.weatherapp.findcityscreen.domain

import ru.n857l.weatherapp.findcityscreen.data.FindCityCacheDataSource
import ru.n857l.weatherapp.findcityscreen.data.FindCityCloudDataSource
import javax.inject.Inject

interface FindCityRepository {

    suspend fun findCity(name: String): FoundCity

    suspend fun save(foundCity: FoundCity)

    class Base @Inject constructor(
        private val cloudDataSource: FindCityCloudDataSource,
        private val cacheDataSource: FindCityCacheDataSource
    ) : FindCityRepository {

        override suspend fun findCity(name: String): FoundCity {
            val foundCityCloud = cloudDataSource.findCity(name)
            return FoundCity(
                name = foundCityCloud.name,
                latitude = foundCityCloud.latitude,
                longitude = foundCityCloud.longitude
            )
        }

        override suspend fun save(foundCity: FoundCity) {
            cacheDataSource.save(
                foundCity.name,
                foundCity.latitude,
                foundCity.longitude,
            )
        }
    }
}