package ru.n857l.weatherapp.findcity.domain

import ru.n857l.weatherapp.findcity.data.FindCityCloudDataSource
import ru.n857l.weatherapp.findcity.data.FindCityDao
import ru.n857l.weatherapp.findcity.data.FindCityEntity
import javax.inject.Inject

interface FindCityRepository {

    suspend fun findCity(name: String): FindCityResult

    suspend fun save(foundCity: FoundCity)

    suspend fun save(lat: Double, lon: Double)

    class Base @Inject constructor(
        private val cloudDataSource: FindCityCloudDataSource,
        private val findCityDao: FindCityDao
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
                        countryCode = it.countryName,
                        state = it.state
                    )
                }
                return FindCityResult.Base(foundCities)
            } catch (e: DomainException) {
                return FindCityResult.Failed(e)
            }
        }

        override suspend fun save(foundCity: FoundCity) {
            findCityDao.saveCity(
                FindCityEntity(
                    lat = foundCity.latitude,
                    lon = foundCity.longitude
                )
            )
        }

        override suspend fun save(lat: Double, lon: Double) {
            findCityDao.saveCity(
                FindCityEntity(
                    lat = lat.toFloat(),
                    lon = lon.toFloat()
                )
            )
        }
    }
}