package ru.n857l.weatherapp.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import ru.n857l.weatherapp.MainActivity
import ru.n857l.weatherapp.weather.data.ForecastDao
import ru.n857l.weatherapp.weather.data.WeatherDao
import ru.n857l.weatherapp.weather.domain.ForecastHour
import ru.n857l.weatherapp.weather.domain.WeatherInCity
import ru.n857l.weatherapp.weather.presentation.TimeWrapper
import ru.n857l.weatherapp.weather.presentation.capitalizedWeatherDescription
import ru.n857l.weatherapp.weather.presentation.weatherIconUrl
import java.util.Locale
import kotlin.math.roundToInt

class WeatherWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WeatherWidget()
}

class WeatherWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            WidgetEntryPoint::class.java
        )
        val weather = entryPoint.weatherDao().getWeather()?.toDomain()
        val timeWrapper = entryPoint.timeWrapper()
        val hours = entryPoint.forecastDao()
            .getForecast()
            ?.toDomain()
            ?.hours
            .orEmpty()
            .take(HOURS_COUNT)
        val icons = loadIcons(
            context = context,
            codes = buildList {
                weather?.icon?.let(::add)
                addAll(hours.map(ForecastHour::icon))
            }
        )
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        provideContent {
            WeatherWidgetContent(
                weather = weather,
                hours = hours,
                icons = icons,
                timeWrapper = timeWrapper,
                launchIntent = launchIntent
            )
        }
    }

    private suspend fun loadIcons(
        context: Context,
        codes: List<String>
    ): Map<String, Bitmap?> = coroutineScope {
        val imageLoader = ImageLoader(context)
        codes.distinct().map { code ->
            async { code to loadIcon(context, imageLoader, code) }
        }.awaitAll().toMap()
    }

    private suspend fun loadIcon(
        context: Context,
        imageLoader: ImageLoader,
        code: String
    ): Bitmap? {
        val request = ImageRequest.Builder(context)
            .data(weatherIconUrl(code))
            .allowHardware(false)
            .build()
        return (imageLoader.execute(request) as? SuccessResult)
            ?.drawable
            ?.toBitmap()
    }

    private companion object {
        const val HOURS_COUNT = 6
    }
}

@Composable
private fun WeatherWidgetContent(
    weather: WeatherInCity?,
    hours: List<ForecastHour>,
    icons: Map<String, Bitmap?>,
    timeWrapper: TimeWrapper,
    launchIntent: Intent
) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .cornerRadius(28.dp)
            .background(WidgetBlue)
            .clickable(actionStartActivity(launchIntent))
            .padding(horizontal = 16.dp, vertical = 13.dp)
    ) {
        if (weather == null) {
            EmptyContent()
            return@Column
        }

        CityHeader(weather.cityName)
        Spacer(GlanceModifier.height(5.dp))
        CurrentWeather(
            weather = weather,
            icon = icons[weather.icon],
            validUntil = hours.weatherChangeTime(weather, timeWrapper)
        )
        Spacer(GlanceModifier.height(7.dp))
        HourlyForecast(hours, icons, timeWrapper)
    }
}

@Composable
private fun CityHeader(city: String) {
    Text(
        text = city,
        style = TextStyle(
            color = PrimaryText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        ),
        maxLines = 1
    )
}

@Composable
private fun CurrentWeather(
    weather: WeatherInCity,
    icon: Bitmap?,
    validUntil: String?
) {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${weather.temperature.roundToInt().temperature()}°",
            style = TextStyle(
                color = PrimaryText,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(GlanceModifier.width(12.dp))
        WeatherImage(
            bitmap = icon,
            description = weather.description,
            size = 50
        )
        Spacer(GlanceModifier.width(12.dp))
        Column {
            Text(
                text = weather.description.capitalizedWeatherDescription(),
                style = TextStyle(
                    color = PrimaryText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 2
            )
            if (validUntil != null) {
                Text(
                    text = "до $validUntil",
                    style = TextStyle(color = SecondaryText, fontSize = 14.sp)
                )
            }
        }
    }
}

@Composable
private fun HourlyForecast(
    hours: List<ForecastHour>,
    icons: Map<String, Bitmap?>,
    timeWrapper: TimeWrapper
) {
    if (hours.isEmpty()) {
        Text(
            text = "Почасовой прогноз загружается",
            style = TextStyle(color = SecondaryText, fontSize = 13.sp)
        )
        return
    }

    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        hours.forEach { hour ->
            HourForecastItem(
                hour = hour,
                icon = icons[hour.icon],
                time = timeWrapper.getShortTime(hour.dateTime),
                modifier = GlanceModifier.defaultWeight()
            )
        }
    }
}

@Composable
private fun HourForecastItem(
    hour: ForecastHour,
    icon: Bitmap?,
    time: String,
    modifier: GlanceModifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = time,
            style = TextStyle(
                color = SecondaryText,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            ),
            maxLines = 1
        )
        Spacer(GlanceModifier.height(2.dp))
        WeatherImage(
            bitmap = icon,
            description = hour.description,
            size = 29
        )
        Spacer(GlanceModifier.height(1.dp))
        Text(
            text = "${hour.temperature.roundToInt().temperature()}°",
            style = TextStyle(
                color = PrimaryText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
private fun WeatherImage(
    bitmap: Bitmap?,
    description: String,
    size: Int
) {
    if (bitmap != null) {
        Image(
            provider = ImageProvider(bitmap),
            contentDescription = description,
            modifier = GlanceModifier.size(size.dp)
        )
    } else {
        Spacer(GlanceModifier.size(size.dp))
    }
}

@Composable
private fun EmptyContent() {
    Text(
        text = "ПОГОДА",
        style = TextStyle(
            color = SecondaryText,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    )
    Spacer(GlanceModifier.height(18.dp))
    Text(
        text = "Выберите город",
        style = TextStyle(
            color = PrimaryText,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
    )
    Spacer(GlanceModifier.height(6.dp))
    Text(
        text = "Нажмите, чтобы открыть приложение",
        style = TextStyle(color = SecondaryText, fontSize = 14.sp)
    )
}

private fun List<ForecastHour>.weatherChangeTime(
    weather: WeatherInCity,
    timeWrapper: TimeWrapper
): String? {
    val currentDescription = weather.description.trim().lowercase(Locale.getDefault())
    return firstOrNull { hour ->
        hour.description.trim().lowercase(Locale.getDefault()) != currentDescription
    }?.dateTime?.let(timeWrapper::getShortTime)
}

private fun Int.temperature(): String = if (this > 0) "+$this" else toString()

private val WidgetBlue = Color(0xFF5795EA)
private val PrimaryText = ColorProvider(
    day = Color(0xFFFFFFFF),
    night = Color(0xFFFFFFFF)
)
private val SecondaryText = ColorProvider(
    day = Color(0xFFD9E9FF),
    night = Color(0xFFD9E9FF)
)

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun weatherDao(): WeatherDao
    fun forecastDao(): ForecastDao
    fun timeWrapper(): TimeWrapper
}
