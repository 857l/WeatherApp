package ru.n857l.weatherapp.findcity.domain

import ru.n857l.weatherapp.findcity.data.FindCityCacheDataSource
import ru.n857l.weatherapp.findcity.data.FindCityCloudDataSource
import javax.inject.Inject

interface FindCityRepository {

    suspend fun findCity(name: String): FindCityResult

    suspend fun save(foundCity: FoundCity)

    class Base @Inject constructor(
        private val cloudDataSource: FindCityCloudDataSource,
        private val cacheDataSource: FindCityCacheDataSource
    ) : FindCityRepository {

        override suspend fun findCity(name: String): FindCityResult {
            try {
                val foundCityCloudList = cloudDataSource.findCity(name)
                if (foundCityCloudList.isEmpty()) return FindCityResult.Empty

                val foundCities = foundCityCloudList.map {
                    FoundCity(
                        name = it.name,
                        latitude = it.latitude,
                        longitude = it.longitude,
                        countryCode = it.countryName
                    )
                }
                return FindCityResult.Base(foundCities)
            } catch (e: DomainException) {
                return FindCityResult.Failed(e)
            }
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