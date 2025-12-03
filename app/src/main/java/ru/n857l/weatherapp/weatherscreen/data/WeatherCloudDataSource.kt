package ru.n857l.weatherapp.weatherscreen.data

import ru.n857l.weatherapp.findcityscreen.data.API_KEY
import javax.inject.Inject

interface WeatherCloudDataSource {

    suspend fun temperature(latitude: Float, longitude: Float): Float

    class Base @Inject constructor(
        private val service: WeatherService
    ) : WeatherCloudDataSource {

        override suspend fun temperature(latitude: Float, longitude: Float): Float {
            val weather = service.weather(latitude, longitude, API_KEY)
                .execute()
            val temperature = weather.body()!!.main.temperature
            return temperature
        }
    }
}