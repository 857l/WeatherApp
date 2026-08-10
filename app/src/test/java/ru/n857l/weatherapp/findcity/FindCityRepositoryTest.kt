package ru.n857l.weatherapp.weather

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ru.n857l.weatherapp.findcity.FakeFindCityDao
import ru.n857l.weatherapp.findcity.data.FindCityCloudDataSource
import ru.n857l.weatherapp.findcity.data.FindCityEntity
import ru.n857l.weatherapp.findcity.data.FoundCityCloud
import ru.n857l.weatherapp.findcity.domain.DomainException
import ru.n857l.weatherapp.findcity.domain.FindCityRepository
import ru.n857l.weatherapp.findcity.domain.FindCityResult
import ru.n857l.weatherapp.findcity.domain.FoundCity
import ru.n857l.weatherapp.findcity.domain.NoInternetException
import ru.n857l.weatherapp.findcity.domain.ServiceUnavailableException

class FindCityRepositoryTest {

    private lateinit var cloudDataSource: FakeFindCityCloudDataSource
    private lateinit var findCityDao: FakeFindCityDao
    private lateinit var repository: FindCityRepository

    @Before
    fun setup() {
        cloudDataSource = FakeFindCityCloudDataSource()
        findCityDao = FakeFindCityDao()
        repository = FindCityRepository.Base(
            cloudDataSource = cloudDataSource,
            findCityDao = findCityDao
        )
    }

    @Test
    fun `empty cloud response returns Empty`() = runBlocking {
        cloudDataSource.foundCities = emptyList()

        val result = repository.findCity("Zzz")

        assertEquals(FindCityResult.Empty, result)
    }

    @Test
    fun `non-empty cloud response maps every field into FoundCity`() = runBlocking {
        cloudDataSource.foundCities = listOf(
            FoundCityCloud(name = "Moscow", latitude = 55.75f, longitude = 37.61f, countryName = "RU", state = null),
            FoundCityCloud(name = "Portland", latitude = 45.52f, longitude = -122.68f, countryName = "US", state = "Oregon")
        )

        val result = repository.findCity("Mo")

        val expected = FindCityResult.Base(
            listOf(
                FoundCity(
                    name = "Moscow",
                    latitude = 55.75f,
                    longitude = 37.61f,
                    countryCode = "RU",
                    state = null
                ),
                FoundCity(name = "Portland", latitude = 45.52f, longitude = -122.68f, countryCode = "US", state = "Oregon")
            )
        )
        assertEquals(expected, result)
    }

    @Test
    fun `no internet exception is wrapped into Failed`() = runBlocking {
        cloudDataSource.exceptionToThrow = NoInternetException

        val result = repository.findCity("Mo")

        assertEquals(FindCityResult.Failed(NoInternetException), result)
    }

    @Test
    fun `any DomainException is caught, not just NoInternetException`() = runBlocking {
        cloudDataSource.exceptionToThrow = ServiceUnavailableException

        val result = repository.findCity("Mo")

        assertEquals(FindCityResult.Failed(ServiceUnavailableException), result)
    }

    @Test
    fun `save FoundCity stores its coordinates`() = runBlocking {
        val foundCity = FoundCity(name = "Moscow", latitude = 55.75f, longitude = 37.61f, countryCode = "RU")

        repository.save(foundCity)

        assertEquals(FindCityEntity(lat = 55.75f, lon = 37.61f), findCityDao.saved)
    }

    @Test
    fun `save lat lon converts Double to Float`() = runBlocking {
        repository.save(lat = 55.75, lon = 37.61)

        assertEquals(FindCityEntity(lat = 55.75f, lon = 37.61f), findCityDao.saved)
    }
}

private class FakeFindCityCloudDataSource : FindCityCloudDataSource {

    var foundCities: List<FoundCityCloud> = emptyList()
    var exceptionToThrow: DomainException? = null

    override suspend fun findCity(query: String): List<FoundCityCloud> {
        exceptionToThrow?.let { throw it }
        return foundCities
    }
}