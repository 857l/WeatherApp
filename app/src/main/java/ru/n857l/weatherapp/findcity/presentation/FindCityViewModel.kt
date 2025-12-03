package ru.n857l.weatherapp.findcity.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import ru.n857l.weatherapp.core.RunAsync
import ru.n857l.weatherapp.findcity.domain.FindCityRepository
import ru.n857l.weatherapp.findcity.domain.FindCityResult
import ru.n857l.weatherapp.findcity.domain.FoundCity
import javax.inject.Inject

@HiltViewModel
class FindCityViewModel @Inject constructor(
    private val mapper: FindCityResult.Mapper<FoundCityUi>,
    private val savedStateHandle: SavedStateHandle,
    private val repository: FindCityRepository,
    private val runAsync: RunAsync<QueryEvent>
) : ViewModel() {

    val state: StateFlow<FoundCityUi> =
        savedStateHandle.getStateFlow(KEY, mapper.mapEmpty())

    init {
        runAsync.debounce(viewModelScope, background = { latestQuery ->
            val query = latestQuery.value
            if (query.isEmpty())
                mapper.mapEmpty()
            else {
                savedStateHandle[KEY] = FoundCityUi.Loading
                repository.findCity(query).map(mapper)
            }
        }) {
            savedStateHandle[KEY] = it
        }
    }

    fun findCity(cityName: String) {
        runAsync.emit(value = QueryEvent(cityName.trim()))
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

class QueryEvent(val value: String)