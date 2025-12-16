package ru.n857l.weatherapp.weather.data

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.n857l.weatherapp.R
import ru.n857l.weatherapp.weather.domain.WeatherInCity
import javax.inject.Inject

interface WeatherCacheDataSource {

    fun cityParams(): Pair<Float, Float>

    fun saveWeather(params: WeatherInCity)

    fun savedWeather(): WeatherInCity

    class Base @Inject constructor(
        @ApplicationContext context: Context
    ) : WeatherCacheDataSource {

        private val sharedPreferences =
            context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

        override fun cityParams(): Pair<Float, Float> {
            val latitude = sharedPreferences.getFloat(LATITUDE, 0f)
            val longitude = sharedPreferences.getFloat(LONGITUDE, 0f)
            return Pair(latitude, longitude)
        }

        override fun saveWeather(params: WeatherInCity) {
            sharedPreferences.edit {
                putString(WEATHER_CITY, params.cityName)
                putFloat(WEATHER_TEMP, params.temperature)
                putFloat(WEATHER_FEELS_TEMP, params.feelsTemperature)
                putFloat(WEATHER_MIN_TEMP, params.tempMin)
                putFloat(WEATHER_MAX_TEMP, params.tempMax)
                putInt(WEATHER_PRESSURE, params.pressure)
                putInt(WEATHER_HUMIDITY, params.humidity)
                putInt(WEATHER_SEA_PRESSURE, params.seaLevelPressure)
                putInt(WEATHER_GROUND_PRESSURE, params.groundLevelPressure)
                putFloat(WEATHER_SPEED, params.speed)
                putInt(WEATHER_DEGREE, params.degree)
                putFloat(WEATHER_GUST, params.gust)
                putInt(WEATHER_CLOUDS, params.clouds)
                putLong(WEATHER_TIME, params.dateTime)
                putLong(WEATHER_SUNRISE, params.sunrise)
                putLong(WEATHER_SUNSET, params.sunset)
                putInt(WEATHER_VISIBILITY, params.visibility)
            }
        }

        override fun savedWeather(): WeatherInCity {
            val cityName = sharedPreferences.getString(WEATHER_CITY, "") ?: ""
            val temperature = sharedPreferences.getFloat(WEATHER_TEMP, 0f)
            val feelsTemperature = sharedPreferences.getFloat(WEATHER_FEELS_TEMP, 0f)
            val tempMin = sharedPreferences.getFloat(WEATHER_MIN_TEMP, 0f)
            val tempMax = sharedPreferences.getFloat(WEATHER_MAX_TEMP, 0f)
            val pressure = sharedPreferences.getInt(WEATHER_PRESSURE, 0)
            val humidity = sharedPreferences.getInt(WEATHER_HUMIDITY, 0)
            val seaLevelPressure = sharedPreferences.getInt(WEATHER_SEA_PRESSURE, 0)
            val groundLevelPressure = sharedPreferences.getInt(WEATHER_GROUND_PRESSURE, 0)
            val speed = sharedPreferences.getFloat(WEATHER_SPEED, 0f)
            val degree = sharedPreferences.getInt(WEATHER_DEGREE, 0)
            val gust = sharedPreferences.getFloat(WEATHER_GUST, 0f)
            val clouds = sharedPreferences.getInt(WEATHER_CLOUDS, 0)
            val dateTime = sharedPreferences.getLong(WEATHER_TIME, 0)
            val sunrise = sharedPreferences.getLong(WEATHER_SUNRISE, 0)
            val sunset = sharedPreferences.getLong(WEATHER_SUNSET, 0)
            val visibility = sharedPreferences.getInt(WEATHER_VISIBILITY, 0)
            return WeatherInCity(
                cityName, temperature, feelsTemperature, tempMin, tempMax,
                pressure, humidity, seaLevelPressure, groundLevelPressure,
                speed, degree, gust, clouds, dateTime, sunrise, sunset, visibility
            )
        }

        companion object {
            private const val LATITUDE = "latitudeKey"
            private const val LONGITUDE = "longitudeKey"
            private const val WEATHER_CITY = "cityNameKey"
            private const val WEATHER_TEMP = "temperatureKey"
            private const val WEATHER_FEELS_TEMP = "feelsTemperatureKey"
            private const val WEATHER_MIN_TEMP = "temperatureMinKey"
            private const val WEATHER_MAX_TEMP = "temperatureMaxKey"
            private const val WEATHER_PRESSURE = "pressureKey"
            private const val WEATHER_HUMIDITY = "humidityKey"
            private const val WEATHER_SEA_PRESSURE = "seaLevelPressureKey"
            private const val WEATHER_GROUND_PRESSURE = "groundLevelPressureKey"
            private const val WEATHER_SPEED = "speedKey"
            private const val WEATHER_DEGREE = "degreeKey"
            private const val WEATHER_GUST = "gustKey"
            private const val WEATHER_CLOUDS = "cloudsKey"
            private const val WEATHER_TIME = "dateTimeKey"
            private const val WEATHER_SUNRISE = "sunriseKey"
            private const val WEATHER_SUNSET = "sunsetKey"
            private const val WEATHER_VISIBILITY = "visibilityKey"
        }
    }
}