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

        override suspend fun findCity(city: String): FindCityResult {
            try {
                val foundCityCloudList = cloudDataSource.findCity(city)
                if (foundCityCloudList.isEmpty()) return FindCityResult.Empty
                else {
                    val foundCityCloud = foundCityCloudList.first()
                    return FindCityResult.Base(
                        FoundCity(
                            name = foundCityCloud.name,
                            latitude = foundCityCloud.latitude,
                            longitude = foundCityCloud.longitude
                        )
                    )
                }
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