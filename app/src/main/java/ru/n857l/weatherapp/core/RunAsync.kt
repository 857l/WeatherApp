package ru.n857l.weatherapp.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.n857l.weatherapp.findcity.presentation.QueryEvent
import javax.inject.Inject
import javax.inject.Singleton

interface RunAsync<R : Any> {

    fun <T : Any> runAsync(
        scope: CoroutineScope,
        background: suspend () -> T,
        ui: (T) -> Unit
    )

    fun <T : Any> debounce(
        scope: CoroutineScope,
        background: suspend (R) -> T,
        ui: (T) -> Unit
    )

    fun emit(value: R)

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    @Singleton
    class Base @Inject constructor(
    ) : RunAsync<QueryEvent> {

        private val inputFlow = MutableStateFlow(QueryEvent(""))

        override fun <T : Any> runAsync(
            scope: CoroutineScope,
            background: suspend () -> T,
            ui: (T) -> Unit
        ) {
            scope.launch(Dispatchers.IO) {
                val result = background()
                withContext(Dispatchers.Main) {
                    ui(result)
                }
            }
        }

        override fun <T : Any> debounce(
            scope: CoroutineScope,
            background: suspend (QueryEvent) -> T,
            ui: (T) -> Unit
        ) {
            inputFlow.debounce(500)
                .flatMapLatest { latestQuery ->
                    flow {
                        emit(background.invoke(latestQuery))
                    }
                }
                .onEach(ui)
                .flowOn(Dispatchers.IO)
                .launchIn(scope)
        }

        override fun emit(value: QueryEvent) {
            inputFlow.value = value
        }
    }
}