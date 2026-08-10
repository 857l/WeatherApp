package ru.n857l.weatherapp.weather

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ru.n857l.weatherapp.findcity.FakeFindCityDao
import ru.n857l.weatherapp.findcity.data.FindCityEntity
import ru.n857l.weatherapp.core.DomainException
import ru.n857l.weatherapp.core.NoInternetException
import ru.n857l.weatherapp.weather.data.Clouds
import ru.n857l.weatherapp.weather.data.Coordinates
import ru.n857l.weatherapp.weather.data.ForecastCacheEntity
import ru.n857l.weatherapp.weather.data.ForecastCity
import ru.n857l.weatherapp.weather.data.ForecastCloud
import ru.n857l.weatherapp.weather.data.ForecastDao
import ru.n857l.weatherapp.weather.data.ForecastItemCloud
import ru.n857l.weatherapp.weather.data.Main
import ru.n857l.weatherapp.weather.data.Sun
import ru.n857l.weatherapp.weather.data.WeatherCloud
import ru.n857l.weatherapp.weather.data.WeatherCloudDataSource
import ru.n857l.weatherapp.weather.data.WeatherDao
import ru.n857l.weatherapp.weather.data.WeatherDescription
import ru.n857l.weatherapp.weather.data.WeatherEntity
import ru.n857l.weatherapp.weather.data.Wind
import ru.n857l.weatherapp.weather.domain.ForecastData
import ru.n857l.weatherapp.weather.domain.ForecastResult
import ru.n857l.weatherapp.weather.domain.WeatherRepository
import ru.n857l.weatherapp.weather.domain.WeatherResult
import ru.n857l.weatherapp.weather.presentation.TimeWrapper
import java.io.Serializable
import java.util.Calendar

class WeatherRepositoryTest {

    private lateinit var findCityDao: FakeFindCityDao
    private lateinit var weatherDao: FakeWeatherDao
    private lateinit var forecastDao: FakeForecastDao
    private lateinit var cloudDataSource: FakeWeatherCloudDataSource
    private lateinit var timeWrapper: FakeTimeWrapper
    private lateinit var repository: WeatherRepository

    private val savedCity = FindCityEntity(lat = 55.75f, lon = 37.61f)

    @Before
    fun setup() {
        findCityDao = FakeFindCityDao()
        weatherDao = FakeWeatherDao()
        forecastDao = FakeForecastDao()
        cloudDataSource = FakeWeatherCloudDataSource()
        timeWrapper = FakeTimeWrapper()
        repository = WeatherRepository.Base(
            findCityDao = findCityDao,
            weatherDao = weatherDao,
            forecastDao = forecastDao,
            cloudDataSource = cloudDataSource,
            timeWrapper = timeWrapper
        )
    }

    @Test
    fun `no saved city returns ServiceUnavailable without touching the network`() = runBlocking {
        val result = repository.weather()

        assertTrue(result is WeatherResult.Failed)
        assertEquals(0, cloudDataSource.weatherCalledCount)
    }

    @Test
    fun `no cache fetches from network and saves the result`() = runBlocking {
        findCityDao.cityToReturn = savedCity
        cloudDataSource.weatherToReturn = sampleCloud()

        val result = repository.weather()

        assertTrue(result is WeatherResult.Base)
        assertEquals(1, cloudDataSource.weatherCalledCount)
        assertTrue(weatherDao.saved != null)
    }

    @Test
    fun `cache for a different location triggers a refresh`() = runBlocking {
        findCityDao.cityToReturn = savedCity
        weatherDao.cached = sampleEntity(lat = 10f, lon = 10f)
        cloudDataSource.weatherToReturn = sampleCloud()

        repository.weather()

        assertEquals(1, cloudDataSource.weatherCalledCount)
    }

    @Test
    fun `stale cache triggers a refresh`() = runBlocking {
        findCityDao.cityToReturn = savedCity
        weatherDao.cached = sampleEntity(lat = savedCity.lat, lon = savedCity.lon)
        timeWrapper.isStale = true
        cloudDataSource.weatherToReturn = sampleCloud()

        repository.weather()

        assertEquals(1, cloudDataSource.weatherCalledCount)
    }

    @Test
    fun `fresh cache is returned without a network call`() = runBlocking {
        findCityDao.cityToReturn = savedCity
        weatherDao.cached = sampleEntity(lat = savedCity.lat, lon = savedCity.lon)
        timeWrapper.isStale = false

        val result = repository.weather()

        assertTrue(result is WeatherResult.Base)
        assertEquals(0, cloudDataSource.weatherCalledCount)
        assertNull(weatherDao.saved)
    }

    @Test
    fun `network failure returns Failed with the original exception`() = runBlocking {
        findCityDao.cityToReturn = savedCity
        cloudDataSource.exceptionToThrow = NoInternetException

        val result = repository.weather()

        assertEquals(WeatherResult.Failed(NoInternetException), result)
    }

    @Test
    fun `hourly strip keeps only items from 06 00 today to 06 00 tomorrow`() = runBlocking {
        findCityDao.cityToReturn = savedCity
        val stillAheadToday = anIncludedTodayHour()
        cloudDataSource.forecastToReturn = ForecastCloud(
            list = listOf(
                forecastItem(todayAt(hour = 5)),      // сегодня до 6 утра — не должно попасть
                forecastItem(stillAheadToday),        // сегодня, ещё впереди — должно попасть
                forecastItem(tomorrowAt(hour = 6)),   // завтра ровно в 6 — граница, должно попасть
                forecastItem(tomorrowAt(hour = 7))    // завтра позже 6 утра — не должно попасть
            ),
            city = ForecastCity(name = "Moscow")
        )

        val result = repository.forecast()
        val data = result.map(ForecastDataMapper).value

        val includedHours = data.hours.map { it.dateTime }
        assertEquals(2, includedHours.size)
        assertTrue(includedHours.contains(stillAheadToday * 1000L))
        assertTrue(includedHours.contains(tomorrowAt(hour = 6) * 1000L))
    }
    
    @Test
    fun `hourly strip excludes hours that have already passed`() = runBlocking {
        findCityDao.cityToReturn = savedCity
        val alreadyPassedToday = todayAt(hour = 6)
        val stillAheadToday = anIncludedTodayHour()

        cloudDataSource.forecastToReturn = ForecastCloud(
            list = listOf(
                forecastItem(alreadyPassedToday),
                forecastItem(stillAheadToday)
            ),
            city = ForecastCity(name = "Moscow")
        )

        val result = repository.forecast()
        val data = result.map(ForecastDataMapper).value

        assertEquals(1, data.hours.size)
        assertEquals(stillAheadToday * 1000L, data.hours.first().dateTime)
    }

    private fun nowPlusHours(hours: Int): Long =
        (System.currentTimeMillis() + hours * 60 * 60 * 1000L) / 1000L

    private fun anIncludedTodayHour(): Long {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return if (currentHour < 6) todayAt(hour = 12) else nowPlusHours(1)
    }

    private fun forecastItem(epochSeconds: Long) = ForecastItemCloud(
        dateTime = epochSeconds,
        main = Main(
            temperature = 10f,
            feelsTemperature = 10f,
            tempMin = 10f,
            tempMax = 10f,
            pressure = 1013,
            humidity = 60,
            seaLevelPressure = 1013,
            groundLevelPressure = 1000
        ),
        weather = listOf(WeatherDescription(description = "clear sky", icon = "01d")),
        dateTimeText = ""
    )

    // Возвращает "сегодня в hour:00" в секундах — так же, как это отдаёт OpenWeatherMap.
    private fun todayAt(hour: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis / 1000
    }

    // То же самое, но на день вперёд.
    private fun tomorrowAt(hour: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis / 1000
    }

    private fun sampleEntity(lat: Float, lon: Float) = WeatherEntity(
        cityName = "Moscow",
        icon = "01d",
        description = "clear sky",
        lat = lat,
        lon = lon,
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
        visibility = 10000,
        dateTime = System.currentTimeMillis(),
        sunrise = System.currentTimeMillis(),
        sunset = System.currentTimeMillis()
    )

    private fun sampleCloud() = WeatherCloud(
        coordinates = Coordinates(longitude = savedCity.lon, latitude = savedCity.lat),
        weather = listOf(WeatherDescription(description = "clear sky", icon = "01d")),
        main = Main(
            temperature = 10f,
            feelsTemperature = 9f,
            tempMin = 8f,
            tempMax = 12f,
            pressure = 1013,
            humidity = 60,
            seaLevelPressure = 1013,
            groundLevelPressure = 1000
        ),
        visibility = 10000,
        wind = Wind(speed = 3f, degree = 180, gust = 5f),
        clouds = Clouds(clouds = 10),
        dateTime = System.currentTimeMillis() / 1000,
        sun = Sun(
            sunrise = System.currentTimeMillis() / 1000,
            sunset = System.currentTimeMillis() / 1000
        ),
        cityName = "Moscow"
    )
}

private class FakeWeatherDao : WeatherDao {

    var cached: WeatherEntity? = null
    var saved: WeatherEntity? = null

    override suspend fun getWeather(): WeatherEntity? = cached

    override suspend fun saveWeather(weather: WeatherEntity) {
        saved = weather
    }

    override suspend fun clear() {
        cached = null
    }
}

private class FakeForecastDao : ForecastDao {

    var cached: ForecastCacheEntity? = null
    var saved: ForecastCacheEntity? = null

    override suspend fun getForecast(): ForecastCacheEntity? = cached

    override suspend fun saveForecast(forecast: ForecastCacheEntity) {
        saved = forecast
    }
}

private class FakeWeatherCloudDataSource : WeatherCloudDataSource {

    var weatherToReturn: WeatherCloud? = null
    var forecastToReturn: ForecastCloud? = null
    var exceptionToThrow: DomainException? = null
    var weatherCalledCount = 0
    var forecastCalledCount = 0

    override suspend fun weather(latitude: Float, longitude: Float): WeatherCloud {
        weatherCalledCount++
        exceptionToThrow?.let { throw it }
        return weatherToReturn ?: error("weatherToReturn is not set for this test")
    }

    override suspend fun forecast(latitude: Float, longitude: Float): ForecastCloud {
        forecastCalledCount++
        exceptionToThrow?.let { throw it }
        return forecastToReturn ?: error("forecastToReturn is not set for this test")
    }
}

private class FakeTimeWrapper : TimeWrapper {

    var isStale = false

    override fun getShortTime(timeMillis: Long): String = ""

    override fun minutesDifference(timeMillis: Long): Boolean = isStale

    override fun getDayLabel(timeMillis: Long): String = ""
}

private class Captured<T>(val value: T) : Serializable

private object ForecastDataMapper : ForecastResult.Mapper<Captured<ForecastData>> {
    override fun mapEmpty() = error("unexpected: empty")
    override fun mapForecast(data: ForecastData) = Captured(data)
    override fun mapNoInternetError() = error("unexpected: no internet")
    override fun mapServiceUnavailableError() = error("unexpected: service unavailable")
}