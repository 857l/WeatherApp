package ru.n857l.weatherapp.weatherscreen.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.n857l.weatherapp.R
import javax.inject.Inject

interface WeatherCacheDataSource {

    fun cityParams(): Triple<Float, Float, String>

    class Base @Inject constructor(
        @ApplicationContext context: Context
    ) : WeatherCacheDataSource {

        private val sharedPreferences =
            context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

        override fun cityParams(): Triple<Float, Float, String> {
            val latitude = sharedPreferences.getFloat(LATITUDE, 0f)
            val longitude = sharedPreferences.getFloat(LONGITUDE, 0f)
            val cityName = sharedPreferences.getString(NAME, "") ?: ""
            return Triple(latitude, longitude, cityName)
        }

        companion object {
            private const val NAME = "cityNameKey"
            private const val LATITUDE = "latitudeKey"
            private const val LONGITUDE = "longitudeKey"
        }
    }
}