package ru.n857l.weatherapp.findcityscreen.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.n857l.weatherapp.R
import ru.n857l.weatherapp.findcityscreen.domain.FoundCity
import java.io.Serializable

@Composable
fun FindCityScreen(
    viewModel: FindCityViewModel,
    navigate: () -> Unit
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
        }
    )
}

@Composable
fun FindCityScreenUi(
    input: String,
    onInputChange:(String) -> Unit,
    foundCityUi: FoundCityUi,
    onFoundCityClick: (FoundCity) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        OutlinedTextField(
            label = { Text(text = stringResource(R.string.city_name)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("findCityInputField"),
            value = input,
            onValueChange = onInputChange,
        )
        foundCityUi.Show(onFoundCityClick)
    }
}

interface FoundCityUi : Serializable {

    @Composable
    fun Show(onFoundCityClick: (FoundCity) -> Unit) = Unit

    data object Empty : FoundCityUi {
        private fun readResolve(): Any = Empty
    }

    data class Base(
        private val foundCity: FoundCity
    ) : FoundCityUi {

        @Composable
        override fun Show(onFoundCityClick: (FoundCity) -> Unit) {
            Button(
                onClick = {
                    onFoundCityClick.invoke(foundCity)
                }
            ) {
                Text(
                    text = foundCity.name,
                    modifier = Modifier.testTag("foundCityUi")
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEmptyFindCityScreenUi() {
    FindCityScreenUi(input = "", onInputChange = {}, foundCityUi = FoundCityUi.Empty) {
    }
}