package ru.n857l.weatherapp.core

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.n857l.weatherapp.findcity.data.FindCityDao
import ru.n857l.weatherapp.findcity.data.FindCityEntity
import ru.n857l.weatherapp.weather.data.ForecastCacheEntity
import ru.n857l.weatherapp.weather.data.ForecastDao
import ru.n857l.weatherapp.weather.data.WeatherDao
import ru.n857l.weatherapp.weather.data.WeatherEntity

@Database(
    entities = [FindCityEntity::class, WeatherEntity::class, ForecastCacheEntity::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun findCityDao(): FindCityDao
    abstract fun weatherDao(): WeatherDao
    abstract fun forecastDao(): ForecastDao
}