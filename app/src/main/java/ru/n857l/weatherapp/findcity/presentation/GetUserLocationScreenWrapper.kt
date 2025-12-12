package ru.n857l.weatherapp.findcity.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Task

@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
@Composable
fun GetUserLocationScreenWrapper(
    onLocationProvided: (Double, Double) -> Unit,
    onFailed: (String) -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    val locationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1_000)
            .setWaitForAccurateLocation(false)
            .build()
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                fusedLocationClient.removeLocationUpdates(this)
                onLocationProvided.invoke(location.latitude, location.longitude)
            }
        }
    }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (isGranted) getUserLocation(fusedLocationClient, { lat, lon ->
            onLocationProvided.invoke(lat.toDouble(), lon.toDouble())
        }) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
                .addOnSuccessListener {}
                .addOnFailureListener { e ->
                    onFailed.invoke(e.message ?: "failed to get location")
                }
        } else {
            onFailed.invoke("Permission denied")
        }
    }
    isLocationEnabled(locationRequest, context) {
        if (it) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                getUserLocation(fusedLocationClient, { lat, lon ->
                    onLocationProvided.invoke(lat.toDouble(), lon.toDouble())
                }) {
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )
                        .addOnSuccessListener {}
                        .addOnFailureListener { e ->
                            onFailed.invoke(e.message ?: "failed to get location!")
                        }
                }
            } else
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                    )
                )
        } else {
            onFailed.invoke("Location is not enabled!")
        }
    }
}

private fun isLocationEnabled(
    locationRequest: LocationRequest,
    context: Context,
    onResult: (Boolean) -> Unit
) {
    val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
    val client = LocationServices.getSettingsClient(context)
    val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

    task.addOnCompleteListener { taskResult ->
        try {
            taskResult.getResult(com.google.android.gms.common.api.ApiException::class.java)
            onResult(true)
        } catch (_: com.google.android.gms.common.api.ApiException) {
            onResult(false)
        }
    }
}

@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
private fun getUserLocation(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationReceived: (String, String) -> Unit,
    requestUpdates: () -> Unit
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location == null)
            requestUpdates.invoke()
        else
            onLocationReceived(location.latitude.toString(), location.longitude.toString())
    }
}