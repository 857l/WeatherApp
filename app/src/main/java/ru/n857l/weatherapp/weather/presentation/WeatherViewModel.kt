package ru.n857l.weatherapp.weather.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import ru.n857l.weatherapp.core.RunAsync
import ru.n857l.weatherapp.findcity.presentation.QueryEvent
import ru.n857l.weatherapp.weather.domain.WeatherRepository
import ru.n857l.weatherapp.weather.domain.WeatherResult
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val mapper: WeatherResult.Mapper<WeatherUi>,
    private val savedStateHandle: SavedStateHandle,
    private val repository: WeatherRepository,
    private val runAsync: RunAsync<QueryEvent>
) : ViewModel() {

    val state: StateFlow<WeatherUi> =
        savedStateHandle.getStateFlow(KEY, mapper.mapEmpty())

    init {
        loadWeather()
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

    companion object {
        private const val KEY = "WeatherScreenUiKey"
    }
}