package ru.n857l.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.n857l.weatherapp.findcityscreen.presentation.FindCityScreen
import ru.n857l.weatherapp.ui.theme.WeatherAppTheme
import ru.n857l.weatherapp.weatherscreen.presentation.WeatherScreen

class MainActivity : ComponentActivity() {
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

@Composable
private fun MainContent(innerPadding: PaddingValues) {
    val navController: NavHostController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "findCityScreen",
        modifier = Modifier.padding(paddingValues = innerPadding)
    ) {
        composable("findCityScreen") {
            FindCityScreen(
                viewModel = hiltViewModel<FindCityViewModel>(),
                navigate = {
                    navController.navigate("weatherScreen")
                }
            )
        }

        composable("weatherScreen") {
            WeatherScreen(viewModel = hiltViewModel<WeatherViewModel>())
        }
    }
}