package ru.n857l.weatherapp.weather.presentation

import ru.n857l.weatherapp.weather.domain.ForecastData
import ru.n857l.weatherapp.weather.domain.ForecastDay
import ru.n857l.weatherapp.weather.domain.ForecastHour
import ru.n857l.weatherapp.weather.domain.ForecastResult
import javax.inject.Inject
import kotlin.math.roundToInt

class ForecastUiMapper @Inject constructor(
    private val timeWrapper: TimeWrapper
) : ForecastResult.Mapper<ForecastUi> {

    override fun mapEmpty(): ForecastUi = ForecastUi.Empty

    override fun mapNoInternetError(): ForecastUi = ForecastUi.NoConnectionError

    override fun mapServiceUnavailableError(): ForecastUi = ForecastUi.ServiceUnavailableError

    override fun mapForecast(data: ForecastData): ForecastUi {
        return ForecastUi.Base(
            hours = data.hours.map { hour -> hour.toHourUi() },
            days = data.days.map { day -> day.toForecastDayUi() }
        )
    }

    private fun ForecastDay.toForecastDayUi(): ForecastDayUi = ForecastDayUi(
        dayLabel = timeWrapper.getDayLabel(date),
        iconUrl = weatherIconUrl(icon),
        tempMin = "${tempMin.roundToInt()}°",
        tempMax = "${tempMax.roundToInt()}°"
    )

    private fun ForecastHour.toHourUi(): HourUi = HourUi(
        time = timeWrapper.getShortTime(dateTime),
        iconUrl = weatherIconUrl(icon),
        temp = "${temperature.roundToInt()}°",
        tempValue = temperature
    )
}