package ru.n857l.weatherapp.weatherscreen.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import ru.n857l.weatherapp.core.RunAsync
import ru.n857l.weatherapp.weatherscreen.domain.WeatherRepository
import javax.inject.Inject

class WeatherViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: WeatherRepository,
    private val runAsync: RunAsync
) : ViewModel() {

    val state: StateFlow<WeatherUi> =
        savedStateHandle.getStateFlow(KEY, WeatherUi.Empty)

    init {
        runAsync.runAsync(viewModelScope, {
            val weatherInCity = repository.weather()
            WeatherUi.Base(weatherInCity)
        }) {
            savedStateHandle[KEY] = it
        }
    }

    companion object {
        private const val KEY = "WeatherScreenUiKey"
    }
}