package ru.n857l.weatherapp.weather.presentation

import ru.n857l.weatherapp.weather.domain.ForecastDay
import ru.n857l.weatherapp.weather.domain.ForecastResult
import javax.inject.Inject
import kotlin.math.roundToInt

class ForecastUiMapper @Inject constructor(
    private val timeWrapper: TimeWrapper
) : ForecastResult.Mapper<ForecastUi> {

    override fun mapEmpty(): ForecastUi = ForecastUi.Empty

    override fun mapNoInternetError(): ForecastUi = ForecastUi.NoConnectionError

    override fun mapServiceUnavailableError(): ForecastUi = ForecastUi.ServiceUnavailableError

    override fun mapForecast(days: List<ForecastDay>): ForecastUi {
        return ForecastUi.Base(
            days.map { day -> day.toForecastDayUi() }
        )
    }

    private fun ForecastDay.toForecastDayUi(): ForecastDayUi = ForecastDayUi(
        dayLabel = timeWrapper.getDayLabel(date),
        iconUrl = "https://openweathermap.org/img/wn/${icon}@2x.png",
        tempMin = "${tempMin.roundToInt()}°",
        tempMax = "${tempMax.roundToInt()}°"
    )
}