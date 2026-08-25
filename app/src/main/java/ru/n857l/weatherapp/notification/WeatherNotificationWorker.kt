package ru.n857l.weatherapp.notification

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.n857l.weatherapp.weather.domain.WeatherRepository
import ru.n857l.weatherapp.weather.domain.WeatherResult
import ru.n857l.weatherapp.weather.presentation.WeatherUi
import ru.n857l.weatherapp.widget.WeatherWidget

@HiltWorker
class WeatherNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: WeatherRepository,
    private val mapper: WeatherResult.Mapper<WeatherUi>,
    private val notifier: WeatherNotifier
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val weatherUi = repository.weather().map(mapper)
        repository.forecast()

        if (weatherUi is WeatherUi.Base) {
            notifier.show(
                cityName = weatherUi.cityName,
                temperature = weatherUi.temperature,
                description = weatherUi.description
            )
        }
        WeatherWidget().updateAll(applicationContext)
        return Result.success()
    }
}
