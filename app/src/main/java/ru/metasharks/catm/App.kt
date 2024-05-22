package ru.metasharks.catm

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ru.metasharks.catm.ui.NotificationChannelFactory
import timber.log.Timber

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        NotificationChannelFactory.createNotificationChannels(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
