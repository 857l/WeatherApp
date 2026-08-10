package ru.n857l.weatherapp.weather

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ru.n857l.weatherapp.core.RunAsync
import ru.n857l.weatherapp.findcity.domain.NoInternetException
import ru.n857l.weatherapp.findcity.domain.ServiceUnavailableException
import ru.n857l.weatherapp.findcity.presentation.QueryEvent
import ru.n857l.weatherapp.weather.domain.ForecastData
import ru.n857l.weatherapp.weather.domain.ForecastResult
import ru.n857l.weatherapp.weather.domain.WeatherInCity
import ru.n857l.weatherapp.weather.domain.WeatherRepository
import ru.n857l.weatherapp.weather.domain.WeatherResult
import ru.n857l.weatherapp.weather.presentation.ForecastUi
import ru.n857l.weatherapp.weather.presentation.WeatherUi
import ru.n857l.weatherapp.weather.presentation.WeatherViewModel

class WeatherViewModelTest {

    private lateinit var repository: FakeWeatherRepository
    private lateinit var runAsync: FakeRunAsync
    private lateinit var weatherMapper: FakeWeatherMapper
    private lateinit var forecastMapper: FakeForecastMapper
    private lateinit var viewModel: WeatherViewModel

    @Before
    fun setup() {
        repository = FakeWeatherRepository()
        runAsync = FakeRunAsync()
        weatherMapper = FakeWeatherMapper()
        forecastMapper = FakeForecastMapper()
    }

    private fun createViewModel(): WeatherViewModel = WeatherViewModel(
        mapper = weatherMapper,
        forecastMapper = forecastMapper,
        savedStateHandle = SavedStateHandle(),
        repository = repository,
        runAsync = runAsync
    )

    @Test
    fun `state is Loading right after creation, before async work completes`() {
        repository.weatherResult = WeatherResult.Base(sampleWeatherInCity())

        viewModel = createViewModel()

        assertEquals(WeatherUi.Loading, viewModel.state.value)
    }

    @Test
    fun `state becomes the mapped weather once loading completes`() {
        repository.weatherResult = WeatherResult.Base(sampleWeatherInCity())
        viewModel = createViewModel()

        runAsync.completeAll()

        assertEquals(weatherMapper.weatherUiToReturn, viewModel.state.value)
    }

    @Test
    fun `silent refresh does not overwrite good state with an error`() {
        repository.weatherResult = WeatherResult.Base(sampleWeatherInCity())
        viewModel = createViewModel()
        runAsync.completeAll()
        assertEquals(weatherMapper.weatherUiToReturn, viewModel.state.value)

        repository.weatherResult = WeatherResult.Failed(NoInternetException)
        runAsync.tick()
        runAsync.completeAll()

        assertEquals(weatherMapper.weatherUiToReturn, viewModel.state.value)
    }

    @Test
    fun `silent refresh updates state when the new result is also good`() {
        repository.weatherResult = WeatherResult.Base(sampleWeatherInCity())
        viewModel = createViewModel()
        runAsync.completeAll()

        val updatedUi = weatherMapper.weatherUiToReturn.copy(cityName = "Saint Petersburg")
        weatherMapper.weatherUiToReturn = updatedUi
        runAsync.tick()
        runAsync.completeAll()

        assertEquals(updatedUi, viewModel.state.value)
    }

    @Test
    fun `loadWeather sets Loading again and eventually reflects the new result`() {
        repository.weatherResult = WeatherResult.Base(sampleWeatherInCity())
        viewModel = createViewModel()
        runAsync.completeAll()

        repository.weatherResult = WeatherResult.Failed(ServiceUnavailableException)
        viewModel.loadWeather()
        assertEquals(WeatherUi.Loading, viewModel.state.value)

        runAsync.completeAll()
        assertEquals(WeatherUi.ServiceUnavailableError, viewModel.state.value)
    }

    private fun sampleWeatherInCity() = WeatherInCity(
        lat = 55.75f,
        lon = 37.61f,
        icon = "01d",
        description = "clear sky",
        cityName = "Moscow",
        temperature = 10f,
        feelsTemperature = 9f,
        tempMin = 8f,
        tempMax = 12f,
        pressure = 1013,
        humidity = 60,
        seaLevelPressure = 1013,
        groundLevelPressure = 1000,
        speed = 3f,
        degree = 180,
        gust = 5f,
        clouds = 10,
        dateTime = System.currentTimeMillis(),
        sunrise = System.currentTimeMillis(),
        sunset = System.currentTimeMillis(),
        visibility = 10000
    )
}

private class FakeWeatherRepository : WeatherRepository {

    var weatherResult: WeatherResult = WeatherResult.Failed(ServiceUnavailableException)
    var forecastResult: ForecastResult = ForecastResult.Failed(ServiceUnavailableException)

    override suspend fun weather(): WeatherResult = weatherResult

    override suspend fun forecast(): ForecastResult = forecastResult
}

private class FakeWeatherMapper : WeatherResult.Mapper<WeatherUi> {

    var weatherUiToReturn: WeatherUi.Base = WeatherUi.Base(
        cityName = "Moscow",
        iconUrl = "",
        description = "clear sky",
        temperature = "10°",
        feelsTemperature = "9°",
        minMaxTemperature = "",
        pressure = "",
        humidity = "",
        seaLevelPressure = "",
        groundLevelPressure = "",
        speed = "",
        degree = "",
        gust = "",
        clouds = "",
        visibility = "",
        sunrise = "",
        sunset = "",
        dateTime = 0L
    )

    override fun mapEmpty(): WeatherUi = WeatherUi.Empty

    override fun mapWeather(weatherInCity: WeatherInCity): WeatherUi = weatherUiToReturn

    override fun mapNoInternetError(): WeatherUi = WeatherUi.NoConnectionError

    override fun mapServiceUnavailableError(): WeatherUi = WeatherUi.ServiceUnavailableError
}

private class FakeForecastMapper : ForecastResult.Mapper<ForecastUi> {

    var forecastUiToReturn: ForecastUi.Base = ForecastUi.Base(hours = emptyList(), days = emptyList())

    override fun mapEmpty(): ForecastUi = ForecastUi.Empty

    override fun mapForecast(data: ForecastData): ForecastUi = forecastUiToReturn

    override fun mapNoInternetError(): ForecastUi = ForecastUi.NoConnectionError

    override fun mapServiceUnavailableError(): ForecastUi = ForecastUi.ServiceUnavailableError
}

@Suppress("UNCHECKED_CAST")
private class FakeRunAsync : RunAsync<QueryEvent> {

    private val pending = mutableListOf<Pair<Any, (Any) -> Unit>>()
    private var tickerCallback: (suspend (Unit) -> Unit)? = null

    override fun <T : Any> runAsync(
        scope: CoroutineScope,
        background: suspend () -> T,
        ui: (T) -> Unit
    ) {
        val result = runBlocking { background() }
        pending.add((result as Any) to (ui as (Any) -> Unit))
    }

    override fun <T : Any> debounce(
        scope: CoroutineScope,
        background: suspend (QueryEvent) -> T,
        ui: (T) -> Unit
    ) {
    }

    override fun emit(value: QueryEvent) {
    }

    override fun <T : Any> runFlow(
        scope: CoroutineScope,
        flow: Flow<T>,
        onEach: suspend (T) -> Unit
    ) {
        tickerCallback = onEach as suspend (Unit) -> Unit
    }

    fun completeAll() {
        while (pending.isNotEmpty()) {
            val (result, ui) = pending.removeAt(0)
            ui(result)
        }
    }

    fun tick() = runBlocking {
        tickerCallback?.invoke(Unit)
    }
}