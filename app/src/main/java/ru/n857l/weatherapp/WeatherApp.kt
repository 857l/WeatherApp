package ru.n857l.weatherapp

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import ru.n857l.weatherapp.notification.WeatherNotificationScheduler
import ru.n857l.weatherapp.notification.WeatherNotifier
import javax.inject.Inject

@HiltAndroidApp
class WeatherApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var notifier: WeatherNotifier

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        notifier.createChannel()
        WeatherNotificationScheduler.schedule(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onStop(owner: LifecycleOwner) {
                    WeatherNotificationScheduler.refreshNow(this@WeatherApp)
                }
            }
        )
    }
}