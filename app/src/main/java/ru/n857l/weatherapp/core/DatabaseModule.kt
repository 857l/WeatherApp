package ru.n857l.weatherapp.core

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.n857l.weatherapp.findcity.data.FindCityDao
import ru.n857l.weatherapp.weather.data.WeatherDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "weather.db"
        ).build()

    @Provides
    fun provideFindCityDao(db: AppDatabase): FindCityDao = db.findCityDao()
    @Provides
    fun provideWeatherDao(db: AppDatabase): WeatherDao = db.weatherDao()
}