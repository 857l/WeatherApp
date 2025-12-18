package ru.n857l.weatherapp.weather.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit
import ru.n857l.weatherapp.weather.data.WeatherCloudDataSource
import ru.n857l.weatherapp.weather.data.WeatherService
import ru.n857l.weatherapp.weather.domain.WeatherRepository
import ru.n857l.weatherapp.weather.domain.WeatherResult
import ru.n857l.weatherapp.weather.presentation.TimeWrapper
import ru.n857l.weatherapp.weather.presentation.WeatherUi
import ru.n857l.weatherapp.weather.presentation.WeatherUiMapper

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
    abstract fun bindWeatherCloudDataSource(cloudDataSource: WeatherCloudDataSource.Base): WeatherCloudDataSource

    @Binds
    abstract fun bindWeatherRepository(repository: WeatherRepository.Base): WeatherRepository

    @Binds
    abstract fun bindWeatherUiMapper(mapper: WeatherUiMapper): WeatherResult.Mapper<WeatherUi>

    @Binds
    abstract fun bindTimeWrapper(wrapper: TimeWrapper.Base): TimeWrapper
}