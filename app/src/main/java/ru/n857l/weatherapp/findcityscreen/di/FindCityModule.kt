package ru.n857l.weatherapp.findcityscreen.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit
import ru.n857l.weatherapp.findcityscreen.data.FindCityCacheDataSource
import ru.n857l.weatherapp.findcityscreen.data.FindCityCloudDataSource
import ru.n857l.weatherapp.findcityscreen.data.FindCityService
import ru.n857l.weatherapp.findcityscreen.domain.FindCityRepository

@Module
@InstallIn(ViewModelComponent::class)
class FindCityModule {

    @Provides
    fun provideService(retrofit: Retrofit): FindCityService =
        retrofit.create(FindCityService::class.java)
}

@Module
@InstallIn(ViewModelComponent::class)
abstract class FindCityBindModule {

    @Binds
    abstract fun bindFindCityCloudDataSource(dataSource: FindCityCloudDataSource.Base): FindCityCloudDataSource

    @Binds
    abstract fun bindFindCityCacheDataSource(dataSource: FindCityCacheDataSource.Base): FindCityCacheDataSource

    @Binds
    abstract fun bindFindCityRepository(repository: FindCityRepository.Base): FindCityRepository
}