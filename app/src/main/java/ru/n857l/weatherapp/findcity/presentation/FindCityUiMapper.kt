package ru.n857l.weatherapp.findcity.presentation

import ru.n857l.weatherapp.findcity.domain.FindCityResult
import ru.n857l.weatherapp.findcity.domain.FoundCity
import javax.inject.Inject

class FindCityUiMapper @Inject constructor() : FindCityResult.Mapper<FoundCityUi> {

    override fun mapFoundCity(foundCities: List<FoundCity>): FoundCityUi {
        return FoundCityUi.Base(foundCities = foundCities)
    }

    override fun mapEmpty(): FoundCityUi {
        return FoundCityUi.Empty
    }

    override fun mapNoInternetError(): FoundCityUi {
        return FoundCityUi.NoConnectionError
    }
}