package ru.n857l.weatherapp.weather.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.n857l.weatherapp.R
import javax.inject.Inject

interface WeatherCacheDataSource {

    fun cityParams(): Pair<Float, Float>

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

        companion object {
            private const val LATITUDE = "latitudeKey"
            private const val LONGITUDE = "longitudeKey"
        }
    }
}