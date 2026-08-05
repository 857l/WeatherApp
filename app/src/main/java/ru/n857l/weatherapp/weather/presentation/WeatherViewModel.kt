package ru.n857l.weatherapp.weather.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import ru.n857l.weatherapp.core.RunAsync
import ru.n857l.weatherapp.findcity.presentation.QueryEvent
import ru.n857l.weatherapp.weather.domain.ForecastResult
import ru.n857l.weatherapp.weather.domain.WeatherRepository
import ru.n857l.weatherapp.weather.domain.WeatherResult
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val mapper: WeatherResult.Mapper<WeatherUi>,
    private val forecastMapper: ForecastResult.Mapper<ForecastUi>,
    private val savedStateHandle: SavedStateHandle,
    private val repository: WeatherRepository,
    private val runAsync: RunAsync<QueryEvent>
) : ViewModel() {

    val state: StateFlow<WeatherUi> =
        savedStateHandle.getStateFlow(KEY, mapper.mapEmpty())

    val forecastState: StateFlow<ForecastUi> =
        savedStateHandle.getStateFlow(FORECAST_KEY, forecastMapper.mapEmpty())

    init {
        loadWeather()
        loadForecast()
    }

    fun loadWeather() {
        savedStateHandle[KEY] = WeatherUi.Loading
        runAsync.runAsync(viewModelScope, {
            val result = repository.weather()
            result.map(mapper)
        }) {
            savedStateHandle[KEY] = it
        }
    }

    fun loadForecast() {
        savedStateHandle[FORECAST_KEY] = ForecastUi.Loading
        runAsync.runAsync(viewModelScope, {
            val result = repository.forecast()
            result.map(forecastMapper)
        }) {
            savedStateHandle[FORECAST_KEY] = it
        }
    }

    companion object {
        private const val KEY = "WeatherScreenUiKey"
        private const val FORECAST_KEY = "ForecastScreenUiKey"
    }
}