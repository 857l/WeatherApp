package ru.n857l.weatherapp.findcity.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit
import ru.n857l.weatherapp.findcity.data.FindCityCacheDataSource
import ru.n857l.weatherapp.findcity.data.FindCityCloudDataSource
import ru.n857l.weatherapp.findcity.data.FindCityService
import ru.n857l.weatherapp.findcity.domain.FindCityRepository
import ru.n857l.weatherapp.findcity.domain.FindCityResult
import ru.n857l.weatherapp.findcity.presentation.FindCityUiMapper
import ru.n857l.weatherapp.findcity.presentation.FoundCityUi

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

    @Binds
    abstract fun bindFindCityUiMapper(mapper: FindCityUiMapper): FindCityResult.Mapper<FoundCityUi>
}