package ru.n857l.weatherapp.weather.data

import ru.n857l.weatherapp.findcity.data.API_KEY
import ru.n857l.weatherapp.findcity.domain.NoInternetException
import ru.n857l.weatherapp.findcity.domain.ServiceUnavailableException
import java.io.IOException
import javax.inject.Inject

interface WeatherCloudDataSource {

    suspend fun weather(latitude: Float, longitude: Float): WeatherCloud

    class Base @Inject constructor(
        private val service: WeatherService
    ) : WeatherCloudDataSource {

        override suspend fun weather(latitude: Float, longitude: Float): WeatherCloud {
            try {
                val result = service.weather(latitude, longitude, API_KEY)
                    .execute()
                return result.body()!!
            } catch (e: Exception) {
                if (e is IOException)
                    throw NoInternetException
                else
                    throw ServiceUnavailableException
            }
        }
    }
}