package ru.n857l.weatherapp.weatherscreen.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit
import ru.n857l.weatherapp.weatherscreen.data.WeatherCacheDataSource
import ru.n857l.weatherapp.weatherscreen.data.WeatherCloudDataSource
import ru.n857l.weatherapp.weatherscreen.data.WeatherService
import ru.n857l.weatherapp.weatherscreen.domain.WeatherRepository

@Module
@InstallIn(ViewModelComponent::class)
class WeatherModule {

    @Provides
    fun bindWeatherService(retrofit: Retrofit): WeatherService =
        retrofit.create(WeatherService::class.java)
}

@Module
@InstallIn(ViewModelComponent::class)
abstract class WeatherBindModule {

    @Binds
    abstract fun bindWeatherCacheDataSource(dataSource: WeatherCacheDataSource.Base): WeatherCacheDataSource

    @Binds
    abstract fun bindWeatherCloudDataSource(cloudDataSource: WeatherCloudDataSource.Base): WeatherCloudDataSource

    @Binds
    abstract fun bindWeatherRepository(repository: WeatherRepository.Base): WeatherRepository
}