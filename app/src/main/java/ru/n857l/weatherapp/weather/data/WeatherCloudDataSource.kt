package ru.n857l.weatherapp.weather.data

import ru.n857l.weatherapp.findcity.data.API_KEY
import ru.n857l.weatherapp.findcity.domain.NoInternetException
import java.io.IOException
import javax.inject.Inject

interface WeatherCloudDataSource {

    suspend fun temperature(latitude: Float, longitude: Float): Float

    class Base @Inject constructor(
        private val service: WeatherService
    ) : WeatherCloudDataSource {

        override suspend fun temperature(latitude: Float, longitude: Float): Float {
            try {
                val weather = service.weather(latitude, longitude, API_KEY)
                    .execute()
                val temperature = weather.body()!!.main.temperature
                return temperature
            } catch (e: Exception) {
                if (e is IOException)
                    throw NoInternetException
                else
                    throw e //todo generic domain exception
            }
        }
    }
}