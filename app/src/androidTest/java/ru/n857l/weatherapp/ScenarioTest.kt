package ru.n857l.weatherapp

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.n857l.weatherapp.core.NoInternetException
import ru.n857l.weatherapp.core.ResourceProvider
import ru.n857l.weatherapp.core.ServiceUnavailableException
import ru.n857l.weatherapp.findcity.domain.FindCityRepository
import ru.n857l.weatherapp.findcity.domain.FindCityResult
import ru.n857l.weatherapp.findcity.domain.FoundCity
import ru.n857l.weatherapp.findcity.presentation.FindCityScreen
import ru.n857l.weatherapp.findcity.presentation.FindCityUiMapper
import ru.n857l.weatherapp.findcity.presentation.FindCityViewModel
import ru.n857l.weatherapp.weather.domain.ForecastData
import ru.n857l.weatherapp.weather.domain.ForecastResult
import ru.n857l.weatherapp.weather.domain.WeatherInCity
import ru.n857l.weatherapp.weather.domain.WeatherRepository
import ru.n857l.weatherapp.weather.domain.WeatherResult
import ru.n857l.weatherapp.weather.presentation.ForecastUiMapper
import ru.n857l.weatherapp.weather.presentation.TimeWrapper
import ru.n857l.weatherapp.weather.presentation.WeatherScreen
import ru.n857l.weatherapp.weather.presentation.WeatherUiMapper
import ru.n857l.weatherapp.weather.presentation.WeatherViewModel

@RunWith(AndroidJUnit4::class)
class ScenarioTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var runAsync: FakeRunAsync
    private lateinit var findCityRepository: FakeFindCityRepository
    private lateinit var weatherRepository: FakeWeatherRepository

    @Test
    fun findCityAndShowWeather() {
        runAsync = FakeRunAsync()
        findCityRepository = FakeFindCityRepository()
        weatherRepository = FakeWeatherRepository()

        weatherRepository.weatherResult = WeatherResult.Failed(NoInternetException)

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val timeWrapper = TimeWrapper.Base(minutes = 15)
        val resourceProvider = ResourceProvider.Base(context)

        val findCityViewModel = FindCityViewModel(
            mapper = FindCityUiMapper(),
            savedStateHandle = SavedStateHandle(),
            repository = findCityRepository,
            runAsync = runAsync
        )
        val weatherViewModel = WeatherViewModel(
            mapper = WeatherUiMapper(timeWrapper, resourceProvider),
            forecastMapper = ForecastUiMapper(timeWrapper, resourceProvider),
            savedStateHandle = SavedStateHandle(),
            repository = weatherRepository,
            runAsync = runAsync
        )

        composeTestRule.setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "findCityScreen") {
                composable("findCityScreen") {
                    FindCityScreen(
                        viewModel = findCityViewModel,
                        navigate = { navController.navigate("weatherScreen") },
                        onGetLocationClick = {
                            throw IllegalStateException("getting location is not tested here")
                        }
                    )
                }

                composable("weatherScreen") {
                    WeatherScreen(
                        viewModel = weatherViewModel,
                        navController = navController
                    )
                }
            }
        }

        val findCityPage = FindCityPage(composeTestRule)
        val weatherPage = WeatherPage(composeTestRule)

        findCityRepository.result = FindCityResult.Failed(NoInternetException)
        findCityPage.input("Mo")
        runAsync.deliverDebouncedResult()
        findCityPage.assertNoConnectionIsDisplayed()

        findCityRepository.result = FindCityResult.Empty
        findCityPage.clickRetry()
        runAsync.deliverDebouncedResult()
        findCityPage.assertEmptyResult()

        val moscow = FoundCity(name = "Moscow", latitude = 55.75f, longitude = 37.61f, countryCode = "RU")
        findCityRepository.result = FindCityResult.Base(listOf(moscow))
        findCityPage.input("Mos")
        findCityPage.assertLoading()
        runAsync.deliverDebouncedResult()
        findCityPage.assertCityFound("Moscow")

        findCityPage.clickFoundCity("Moscow")
        runAsync.completeAllPending()
        weatherPage.assertNoConnectionIsDisplayed()

        weatherRepository.weatherResult = WeatherResult.Base(sampleWeatherInCity())
        weatherPage.clickRetry()
        weatherPage.assertLoading()
        runAsync.completeAllPending()
        weatherPage.assertCityName("Moscow")
        weatherPage.assertWeatherDisplayed("10°")

        weatherRepository.weatherResult = WeatherResult.Base(sampleWeatherInCity().copy(temperature = 15f))
        runAsync.tick()
        runAsync.completeAllPending()
        weatherPage.assertWeatherDisplayed("15°")
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

private class FakeFindCityRepository : FindCityRepository {

    var result: FindCityResult = FindCityResult.Empty
    var savedFoundCity: FoundCity? = null

    override suspend fun findCity(name: String): FindCityResult = result

    override suspend fun save(foundCity: FoundCity) {
        savedFoundCity = foundCity
    }

    override suspend fun save(lat: Double, lon: Double) {
        throw IllegalStateException("choosing by coordinates is not tested here")
    }
}

private class FakeWeatherRepository : WeatherRepository {

    var weatherResult: WeatherResult = WeatherResult.Failed(ServiceUnavailableException)
    var forecastResult: ForecastResult = ForecastResult.Base(ForecastData(hours = emptyList(), days = emptyList()))

    override suspend fun weather(): WeatherResult = weatherResult

    override suspend fun forecast(): ForecastResult = forecastResult
}
