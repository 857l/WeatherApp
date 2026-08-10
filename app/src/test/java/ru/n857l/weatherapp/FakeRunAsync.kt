package ru.n857l.weatherapp

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import ru.n857l.weatherapp.core.RunAsync
import ru.n857l.weatherapp.findcity.presentation.QueryEvent

@Suppress("UNCHECKED_CAST")
class FakeRunAsync : RunAsync<QueryEvent> {

    private val pendingRunAsync = mutableListOf<Pair<Any, (Any) -> Unit>>()

    override fun <T : Any> runAsync(
        scope: CoroutineScope,
        background: suspend () -> T,
        ui: (T) -> Unit
    ) {
        val result = runBlocking { background() }
        pendingRunAsync.add((result as Any) to (ui as (Any) -> Unit))
    }

    private var debounceBackground: (suspend (QueryEvent) -> Any)? = null
    private var debounceUi: ((Any) -> Unit)? = null
    private var debouncedResult: Any? = null

    override fun <T : Any> debounce(
        scope: CoroutineScope,
        background: suspend (QueryEvent) -> T,
        ui: (T) -> Unit
    ) {
        debounceBackground = background as suspend (QueryEvent) -> Any
        debounceUi = ui as (Any) -> Unit
    }

    override fun emit(value: QueryEvent) {
        val background = debounceBackground ?: return
        debouncedResult = runBlocking { background(value) }
    }

    private var tickerCallback: (suspend (Any) -> Unit)? = null

    override fun <T : Any> runFlow(
        scope: CoroutineScope,
        flow: Flow<T>,
        onEach: suspend (T) -> Unit
    ) {
        tickerCallback = onEach as suspend (Any) -> Unit
    }

    fun completeAllPending() {
        while (pendingRunAsync.isNotEmpty()) {
            val (result, ui) = pendingRunAsync.removeAt(0)
            ui(result)
        }
    }

    fun deliverDebouncedResult() {
        debouncedResult?.let {
            debounceUi?.invoke(it)
            debouncedResult = null
        }
    }

    fun tick() = runBlocking {
        tickerCallback?.invoke(Unit)
    }
}