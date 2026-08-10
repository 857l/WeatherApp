package ru.n857l.weatherapp

import androidx.lifecycle.SavedStateHandle
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ru.n857l.weatherapp.findcity.FakeFindCityDao
import ru.n857l.weatherapp.findcity.data.FindCityEntity

class MainViewModelTest {

    private lateinit var findCityDao: FakeFindCityDao
    private lateinit var runAsync: FakeRunAsync

    @Before
    fun setup() {
        findCityDao = FakeFindCityDao()
        runAsync = FakeRunAsync()
    }

    private fun createViewModel(): MainViewModel = MainViewModel(
        savedStateHandle = SavedStateHandle(),
        findCityDao = findCityDao,
        runAsync = runAsync
    )

    @Test
    fun `hasLocation is false right after creation, before the check completes`() {
        findCityDao.cityToReturn = FindCityEntity(lat = 55.75f, lon = 37.61f)

        val viewModel = createViewModel()

        assertFalse(viewModel.hasLocation.value)
    }

    @Test
    fun `hasLocation becomes true once the check finds a saved city`() {
        findCityDao.cityToReturn = FindCityEntity(lat = 55.75f, lon = 37.61f)

        val viewModel = createViewModel()
        runAsync.completeAllPending()

        assertTrue(viewModel.hasLocation.value)
    }

    @Test
    fun `hasLocation stays false when no city is saved`() {
        findCityDao.cityToReturn = null

        val viewModel = createViewModel()
        runAsync.completeAllPending()

        assertFalse(viewModel.hasLocation.value)
    }
}