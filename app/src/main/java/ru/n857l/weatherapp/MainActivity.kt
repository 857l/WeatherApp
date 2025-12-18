package ru.n857l.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.n857l.weatherapp.findcity.data.FindCityDao
import ru.n857l.weatherapp.findcity.presentation.FindCityOrGetLocationScreen
import ru.n857l.weatherapp.findcity.presentation.FindCityViewModel
import ru.n857l.weatherapp.ui.theme.WeatherAppTheme
import ru.n857l.weatherapp.weather.presentation.WeatherScreen
import ru.n857l.weatherapp.weather.presentation.WeatherViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainContent(innerPadding)
                }
            }
        }
    }
}

@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
@Composable
private fun MainContent(innerPadding: PaddingValues) {
    val mainViewModel = hiltViewModel<MainViewModel>()
    val startDestination =
        if (mainViewModel.hasAlreadyChosenLocation()) "weatherScreen" else "findCityScreen"
    val navController: NavHostController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.padding(paddingValues = innerPadding)
    ) {
        composable("findCityScreen") {
            FindCityOrGetLocationScreen(
                viewModel = hiltViewModel<FindCityViewModel>(),
                navigate = {
                    navController.navigate("weatherScreen")
                }
            )
        }

        composable("weatherScreen") {
            WeatherScreen(
                viewModel = hiltViewModel<WeatherViewModel>(),
                navController = navController
            )
        }
    }
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val findCityDao: FindCityDao,
) : ViewModel() {

    fun hasAlreadyChosenLocation(): Boolean =
        findCityDao.getCity() != null
    //TODO
}