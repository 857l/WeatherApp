package ru.n857l.weatherapp.findcity.data

import retrofit2.HttpException
import ru.n857l.weatherapp.findcity.domain.NoInternetException
import ru.n857l.weatherapp.findcity.domain.ServiceUnavailableException
import java.io.IOException
import javax.inject.Inject
import ru.n857l.weatherapp.BuildConfig
import ru.n857l.weatherapp.findcity.domain.TooManyRequestsException
import ru.n857l.weatherapp.findcity.domain.UnauthorizedException

interface FindCityCloudDataSource {

    suspend fun findCity(query: String): List<FoundCityCloud>

    class Base @Inject constructor(
        private val service: FindCityService
    ) : FindCityCloudDataSource {

        override suspend fun findCity(query: String): List<FoundCityCloud> {
            try {
                return service.findCity(query = query, apiKey = API_KEY)
            } catch (e: IOException) {
                throw NoInternetException
            } catch (e: HttpException) {
                throw when (e.code()) {
                    401 -> UnauthorizedException
                    429 -> TooManyRequestsException
                    else -> ServiceUnavailableException
                }
            }
        }
    }
}

const val API_KEY = BuildConfig.OWM_API_KEY