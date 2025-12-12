package ru.n857l.weatherapp.findcity.presentation

import android.Manifest
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext

@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
@Composable
fun FindCityOrGetLocationScreen(
    viewModel: FindCityViewModel,
    navigate: () -> Unit,
) {
    val context = LocalContext.current
    val getLocation = rememberSaveable { mutableStateOf(false) }
    if (getLocation.value) GetUserLocationScreenWrapper({ lat, lon ->
        viewModel.chooseLocation(lat, lon)
        getLocation.value = false
        navigate.invoke()
    }) {
        Toast.makeText(context.applicationContext, it, Toast.LENGTH_LONG).show()
        getLocation.value = false
    }
    FindCityScreen(viewModel, navigate) {
        getLocation.value = true
    }
}