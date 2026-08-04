package ru.n857l.weatherapp.weather.data

import retrofit2.HttpException
import ru.n857l.weatherapp.findcity.data.API_KEY
import ru.n857l.weatherapp.findcity.domain.NoInternetException
import ru.n857l.weatherapp.findcity.domain.ServiceUnavailableException
import ru.n857l.weatherapp.findcity.domain.TooManyRequestsException
import ru.n857l.weatherapp.findcity.domain.UnauthorizedException
import java.io.IOException
import javax.inject.Inject

interface WeatherCloudDataSource {

    suspend fun weather(latitude: Float, longitude: Float): WeatherCloud

    class Base @Inject constructor(
        private val service: WeatherService
    ) : WeatherCloudDataSource {

        override suspend fun weather(latitude: Float, longitude: Float): WeatherCloud {
            try {
                return service.weather(latitude, longitude, API_KEY)
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