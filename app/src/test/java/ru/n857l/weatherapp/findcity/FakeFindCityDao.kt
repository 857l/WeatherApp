package ru.n857l.weatherapp.findcity

import ru.n857l.weatherapp.findcity.data.FindCityDao
import ru.n857l.weatherapp.findcity.data.FindCityEntity

class FakeFindCityDao : FindCityDao {

    var cityToReturn: FindCityEntity? = null
    var saved: FindCityEntity? = null

    override fun getCity(): FindCityEntity? = cityToReturn

    override suspend fun saveCity(city: FindCityEntity) {
        saved = city
    }
}