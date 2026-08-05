package ru.n857l.weatherapp.weather.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.io.Serializable

data class ForecastDayUi(
    val dayLabel: String,
    val iconUrl: String,
    val tempMin: String,
    val tempMax: String
) : Serializable

interface ForecastUi : Serializable {

    @Composable
    fun Show() = Unit

    data object Empty : ForecastUi {
        private fun readResolve(): Any = Empty
    }

    data object Loading : ForecastUi {
        private fun readResolve(): Any = Loading
    }

    data object NoConnectionError : ForecastUi {
        private fun readResolve(): Any = NoConnectionError
    }

    data object ServiceUnavailableError : ForecastUi {
        private fun readResolve(): Any = ServiceUnavailableError
    }

    data class Base(
        private val days: List<ForecastDayUi>
    ) : ForecastUi {

        @Composable
        override fun Show() {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(days) { day ->
                    ForecastDayCard(day)
                }
            }
        }
    }
}

@Composable
private fun ForecastDayCard(day: ForecastDayUi) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = day.dayLabel,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            AsyncImage(
                model = day.iconUrl,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = day.tempMax,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = day.tempMin,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}