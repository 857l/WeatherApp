package ru.n857l.weatherapp.findcity.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.n857l.weatherapp.R
import ru.n857l.weatherapp.findcity.domain.FoundCity
import java.io.Serializable

@Composable
fun FindCityScreen(
    viewModel: FindCityViewModel,
    navigate: () -> Unit,
    onGetLocationClick: () -> Unit,
) {
    val input = rememberSaveable { mutableStateOf("") }
    val foundCityUi = viewModel.state.collectAsStateWithLifecycle()
    FindCityScreenUi(
        input = input.value,
        onInputChange = { text ->
            viewModel.findCity(cityName = text)
            input.value = text
        },
        foundCityUi = foundCityUi.value,
        onFoundCityClick = { foundCity: FoundCity ->
            viewModel.chooseCity(foundCity = foundCity)
            navigate.invoke()
        },
        onRetryClick = {
            viewModel.findCity(cityName = input.value)
        },
        onGetLocationClick = onGetLocationClick
    )
}

@Composable
fun FindCityScreenUi(
    input: String,
    onInputChange: (String) -> Unit,
    foundCityUi: FoundCityUi,
    onFoundCityClick: (FoundCity) -> Unit,
    onRetryClick: () -> Unit,
    onGetLocationClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            onClick = onGetLocationClick, modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.get_location))
        }
        OutlinedTextField(
            label = { Text(text = stringResource(R.string.city_name)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("findCityInputField"),
            maxLines = 1,
            value = input,
            onValueChange = onInputChange,
        )
        foundCityUi.Show(onFoundCityClick, onRetryClick)

    }
}

interface FoundCityUi : Serializable {

    @Composable
    fun Show(onFoundCityClick: (FoundCity) -> Unit, onRetryClick: () -> Unit) = Unit

    data object Empty : FoundCityUi {
        private fun readResolve(): Any = Empty
    }

    data class Base(
        private val foundCities: List<FoundCity>
    ) : FoundCityUi {

        @Composable
        override fun Show(onFoundCityClick: (FoundCity) -> Unit, onRetryClick: () -> Unit) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(foundCities) { city ->
                    CityItem(city, onClick = onFoundCityClick)
                }
            }
        }
    }

    data object NoConnectionError : FoundCityUi {
        private fun readResolve(): Any = NoConnectionError

        @Composable
        override fun Show(onFoundCityClick: (FoundCity) -> Unit, onRetryClick: () -> Unit) {
            NoConnectionErrorUi(onRetryClick)
        }
    }

    data object Loading : FoundCityUi {
        private fun readResolve(): Any = Loading

        @Composable
        override fun Show(
            onFoundCityClick: (FoundCity) -> Unit,
            onRetryClick: () -> Unit
        ) {
            LoadingUi()
        }
    }
}

@Composable
fun CityItem(
    city: FoundCity,
    onClick: (FoundCity) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable { onClick(city) }
    ) {
        Text(
            text = city.name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = city.fullCountryName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        HorizontalDivider()
    }
}

@Composable
fun LoadingUi() {
    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Text(text = stringResource(R.string.loading), Modifier.testTag("CircleLoading"))
        }
    }
}

@Composable
fun NoConnectionErrorUi(onRetryClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.no_internet_connection),
            modifier = Modifier.testTag("noInternetConnection")
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetryClick, modifier = Modifier.testTag("retryButton")) {
            Text(text = stringResource(R.string.retry))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCityItem() {
    CityItem(
        city = FoundCity(
            name = "Moscow",
            latitude = 55.75f,
            longitude = 37.61f,
            countryCode = "RU"
        ),
        onClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewEmptyFindCityScreenUi() {
    FindCityScreenUi(input = "", onInputChange = {}, foundCityUi = FoundCityUi.Empty, {}, {}) {
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNotEmptyFindCityScreenUi() {
    FindCityScreenUi(
        input = "Mos", onInputChange = {}, foundCityUi = FoundCityUi.Base(
            foundCities = listOf(
                FoundCity(
                    name = "Moscow",
                    latitude = 55.75f,
                    longitude = 37.61f,
                    countryCode = "RU"
                ),
                FoundCity(
                    name = "Moscow",
                    latitude = 55.75f,
                    longitude = 37.61f,
                    countryCode = "RU"
                )
            )
        ),
        {},
        {}
    ) {
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNoInternetConnection() {
    FindCityScreenUi(
        input = "mos",
        onInputChange = {},
        foundCityUi = FoundCityUi.NoConnectionError,
        {}, {}) {
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoading() {
    FindCityScreenUi(
        input = "Mos", onInputChange = {}, foundCityUi = FoundCityUi.Loading, {}, {}, {}
    )
}