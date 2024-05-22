package ru.metasharks.catm.feature.offline

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import ru.metasharks.catm.core.navigation.ApplicationRouter
import ru.metasharks.catm.core.navigation.screens.OfflineSwitcherScreen
import ru.metasharks.catm.core.network.internet.InternetListener
import ru.metasharks.catm.core.network.offline.OfflineModeProvider
import javax.inject.Inject

@AndroidEntryPoint
class OfflineListenerService : Service() {

    @Inject
    lateinit var appRouter: ApplicationRouter

    @Inject
    lateinit var offlineSwitcherScreen: OfflineSwitcherScreen

    @Inject
    lateinit var offlineModeProvider: OfflineModeProvider

    private val internetListenerCallback = object : InternetListener.Callback {

        override fun onInternetAcquired() {
            if (offlineModeProvider.isInOfflineMode) {
                appRouter.navigateTo(offlineSwitcherScreen(fromOnline = false, force = true))
            }
        }

        override fun onInternetLost() {
            if (offlineModeProvider.isInOfflineMode.not()) {
                appRouter.navigateTo(offlineSwitcherScreen(fromOnline = true, force = true))
            }
        }
    }
    private val internetListener = InternetListener(internetListenerCallback)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        internetListener.setup(this)
        return START_STICKY
    }

    companion object {

        fun createIntent(context: Context): Intent {
            return Intent(context, OfflineListenerService::class.java)
        }
    }
}
