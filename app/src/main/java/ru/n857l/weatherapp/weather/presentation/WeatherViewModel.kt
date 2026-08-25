package ru.n857l.weatherapp.weather.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
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
        scheduleAutoRefresh()
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

    fun onResume() {
        refreshWeatherSilently()
        refreshForecastSilently()
    }

    private fun scheduleAutoRefresh() {
        runAsync.runFlow(
            scope = viewModelScope,
            flow = tickerFlow(REFRESH_INTERVAL_MILLIS)
        ) {
            refreshWeatherSilently()
            refreshForecastSilently()
        }
    }

    private fun tickerFlow(intervalMillis: Long): Flow<Unit> = flow {
        while (true) {
            delay(intervalMillis)
            emit(Unit)
        }
    }

    private fun refreshWeatherSilently() {
        runAsync.runAsync(viewModelScope, {
            val result = repository.weather()
            result.map(mapper)
        }) { refreshedUi ->
            if (refreshedUi is WeatherUi.Base)
                savedStateHandle[KEY] = refreshedUi
        }
    }

    private fun refreshForecastSilently() {
        runAsync.runAsync(viewModelScope, {
            val result = repository.forecast()
            result.map(forecastMapper)
        }) { refreshedUi ->
            if (refreshedUi is ForecastUi.Base)
                savedStateHandle[FORECAST_KEY] = refreshedUi
        }
    }

    companion object {
        private const val KEY = "WeatherScreenUiKey"
        private const val FORECAST_KEY = "ForecastScreenUiKey"
        private const val REFRESH_INTERVAL_MILLIS = 1 * 60 * 1000L
    }
}
