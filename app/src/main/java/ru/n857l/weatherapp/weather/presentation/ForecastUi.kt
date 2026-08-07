package ru.n857l.weatherapp.weather.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
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

data class HourUi(
    val time: String,
    val iconUrl: String,
    val temp: String,
    val tempValue: Float
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
        private val hours: List<HourUi>,
        private val days: List<ForecastDayUi>
    ) : ForecastUi {

        @Composable
        override fun Show() {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (hours.isNotEmpty()) {
                    HourlyForecastStrip(hours)
                }

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
}

@Composable
private fun HourlyForecastStrip(hours: List<HourUi>) {
    val itemWidth = 56.dp
    val density = LocalDensity.current
    val itemWidthPx = with(density) { itemWidth.toPx() }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.25f)
        )
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 12.dp, horizontal = 8.dp)
        ) {
            Column {
                Row {
                    hours.forEach { hour ->
                        Text(
                            text = hour.time,
                            modifier = Modifier.width(itemWidth),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }

                Row {
                    hours.forEach { hour ->
                        AsyncImage(
                            model = hour.iconUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .width(itemWidth)
                                .size(36.dp)
                        )
                    }
                }

                Row {
                    hours.forEach { hour ->
                        Text(
                            text = hour.temp,
                            modifier = Modifier.width(itemWidth),
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                HourlyTempGraph(
                    temps = hours.map { it.tempValue },
                    itemWidth = itemWidth,
                    itemWidthPx = itemWidthPx,
                    height = 32.dp
                )
            }
        }
    }
}

@Composable
private fun HourlyTempGraph(
    temps: List<Float>,
    itemWidth: Dp,
    itemWidthPx: Float,
    height: Dp
) {
    val lineColor = Color(0xFFFFD54F)

    Canvas(
        modifier = Modifier
            .width(itemWidth * temps.size)
            .height(height)
    ) {
        if (temps.size < 2) return@Canvas

        val minTemp = temps.min()
        val maxTemp = temps.max()
        val range = (maxTemp - minTemp).takeIf { it > 0f } ?: 1f

        val points = temps.mapIndexed { index, temp ->
            val x = itemWidthPx * index + itemWidthPx / 2f
            val normalized = (temp - minTemp) / range
            val y = size.height * (1f - normalized)
            Offset(x, y)
        }

        for (i in 0 until points.size - 1) {
            drawLine(
                color = lineColor,
                start = points[i],
                end = points[i + 1],
                strokeWidth = 4f
            )
        }

        points.forEach { point ->
            drawCircle(color = lineColor, radius = 4f, center = point)
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