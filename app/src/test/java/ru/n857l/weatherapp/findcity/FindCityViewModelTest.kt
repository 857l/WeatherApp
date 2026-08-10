package ru.n857l.weatherapp.findcity

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ru.n857l.weatherapp.core.RunAsync
import ru.n857l.weatherapp.findcity.domain.FindCityRepository
import ru.n857l.weatherapp.findcity.domain.FindCityResult
import ru.n857l.weatherapp.findcity.domain.FoundCity
import ru.n857l.weatherapp.core.NoInternetException
import ru.n857l.weatherapp.findcity.presentation.FindCityUiMapper
import ru.n857l.weatherapp.findcity.presentation.FindCityViewModel
import ru.n857l.weatherapp.findcity.presentation.FoundCityUi
import ru.n857l.weatherapp.findcity.presentation.QueryEvent

class FindCityViewModelTest {

    private lateinit var repository: FakeFindCityRepository
    private lateinit var runAsync: FakeRunAsync
    private lateinit var viewModel: FindCityViewModel

    @Before
    fun setup() {
        repository = FakeFindCityRepository()
        runAsync = FakeRunAsync()
        viewModel = FindCityViewModel(
            mapper = FindCityUiMapper(),
            savedStateHandle = SavedStateHandle(),
            repository = repository,
            runAsync = runAsync
        )
    }

    @Test
    fun `blank query does not call the repository and keeps state Empty`() {
        viewModel.findCity(" ")

        assertEquals(FoundCityUi.Empty, viewModel.state.value)
        assertEquals(emptyList<String>(), repository.findCityCalledWith)
    }

    @Test
    fun `non-blank query shows Loading immediately, before the result arrives`() {
        repository.result = FindCityResult.Empty

        viewModel.findCity("Mo")

        assertEquals(FoundCityUi.Loading, viewModel.state.value)
        assertEquals(listOf("Mo"), repository.findCityCalledWith)
    }

    @Test
    fun `successful search delivers the found cities once the result arrives`() {
        val moscow = FoundCity(name = "Moscow", latitude = 55.75f, longitude = 37.61f, countryCode = "RU")
        repository.result = FindCityResult.Base(listOf(moscow))

        viewModel.findCity("Mos")
        runAsync.deliverDebouncedResult()

        assertEquals(FoundCityUi.Base(listOf(moscow)), viewModel.state.value)
    }

    @Test
    fun `network error is mapped to NoConnectionError`() {
        repository.result = FindCityResult.Failed(NoInternetException)

        viewModel.findCity("Mo")
        runAsync.deliverDebouncedResult()

        assertEquals(FoundCityUi.NoConnectionError, viewModel.state.value)
    }

    @Test
    fun `query is trimmed before being sent to the repository`() {
        repository.result = FindCityResult.Empty

        viewModel.findCity("  Mos  ")

        assertEquals(listOf("Mos"), repository.findCityCalledWith)
    }

    @Test
    fun `chooseCity saves the selected city`() {
        val moscow = FoundCity(name = "Moscow", latitude = 55.75f, longitude = 37.61f, countryCode = "RU")

        viewModel.chooseCity(moscow)

        assertEquals(moscow, repository.savedFoundCity)
    }

    @Test
    fun `chooseLocation saves raw coordinates`() {
        viewModel.chooseLocation(lat = 55.75, lon = 37.61)

        assertEquals(55.75, repository.savedLat)
        assertEquals(37.61, repository.savedLon)
    }
}

private class FakeFindCityRepository : FindCityRepository {

    var result: FindCityResult = FindCityResult.Empty
    val findCityCalledWith = mutableListOf<String>()
    var savedFoundCity: FoundCity? = null
    var savedLat: Double? = null
    var savedLon: Double? = null

    override suspend fun findCity(name: String): FindCityResult {
        findCityCalledWith.add(name)
        return result
    }

    override suspend fun save(foundCity: FoundCity) {
        savedFoundCity = foundCity
    }

    override suspend fun save(lat: Double, lon: Double) {
        savedLat = lat
        savedLon = lon
    }
}

@Suppress("UNCHECKED_CAST")
private class FakeRunAsync : RunAsync<QueryEvent> {

    private var debounceBackground: (suspend (QueryEvent) -> Any)? = null
    private var debounceUi: ((Any) -> Unit)? = null
    private var debouncedResult: Any? = null

    override fun <T : Any> runAsync(
        scope: CoroutineScope,
        background: suspend () -> T,
        ui: (T) -> Unit
    ) {
        val result = runBlocking { background() }
        ui(result)
    }

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

    override fun <T : Any> runFlow(
        scope: CoroutineScope,
        flow: Flow<T>,
        onEach: suspend (T) -> Unit
    ) {
        // FindCityViewModel этим не пользуется.
    }

    fun deliverDebouncedResult() {
        debouncedResult?.let {
            debounceUi?.invoke(it)
            debouncedResult = null
        }
    }
}