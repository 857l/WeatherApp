package ru.n857l.weatherapp.findcityscreen.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import ru.n857l.weatherapp.core.RunAsync
import ru.n857l.weatherapp.findcityscreen.domain.FindCityRepository
import ru.n857l.weatherapp.findcityscreen.domain.FoundCity
import javax.inject.Inject

@HiltViewModel
class FindCityViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: FindCityRepository,
    private val runAsync: RunAsync
) : ViewModel() {

    val state: StateFlow<FoundCityUi> = savedStateHandle.getStateFlow(KEY, FoundCityUi.Empty)

    fun findCity(cityName: String) {
        runAsync.runAsync(viewModelScope, background = {
            FoundCityUi.Base(foundCity = repository.findCity(cityName))
        }) {
            savedStateHandle[KEY] = it
        }
    }

    fun chooseCity(foundCity: FoundCity) {
        runAsync.runAsync(viewModelScope, background = {
            repository.save(foundCity)
        }) {
        }
    }

    companion object {
        private const val KEY = "FoundCityUiKey"
    }
}