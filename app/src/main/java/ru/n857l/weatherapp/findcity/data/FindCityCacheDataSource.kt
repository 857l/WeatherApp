package ru.n857l.weatherapp.findcity.data

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.n857l.weatherapp.R
import javax.inject.Inject

interface FindCityCacheDataSource {

    suspend fun save(cityName: String, latitude: Float, longitude: Float)

    class Base @Inject constructor(
        @ApplicationContext context: Context
    ) : FindCityCacheDataSource {

        private val sharedPreferences =
            context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

        override suspend fun save(
            cityName: String,
            latitude: Float,
            longitude: Float
        ) {
            sharedPreferences.edit {
                putString(NAME, cityName)
                    .putFloat(LATITUDE, latitude)
                    .putFloat(LONGITUDE, longitude)
            }
        }

        companion object {
            private const val NAME = "cityNameKey"
            private const val LATITUDE = "latitudeKey"
            private const val LONGITUDE = "longitudeKey"
        }
    }
}