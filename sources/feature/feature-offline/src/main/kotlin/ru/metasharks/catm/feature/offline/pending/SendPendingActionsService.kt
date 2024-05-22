package ru.metasharks.catm.feature.offline.pending

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.metasharks.catm.feature.offline.R
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SendPendingActionsService : Service() {

    @Inject
    lateinit var pendingActionsRepository: PendingActionsRepository

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        pendingActionsRepository.areAnyPending()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { anyPending ->
                    if (anyPending) {
                        sendAll()
                    } else {
                        stopSelf()
                    }
                }, {
                    Timber.e(it)
                }
            )

        return START_STICKY
    }

    private fun sendAll() {
        Toast.makeText(this, R.string.pending_actions_started, Toast.LENGTH_SHORT).show()
        pendingActionsRepository.sendAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Toast.makeText(this, R.string.pending_actions_ended, Toast.LENGTH_LONG).show()
                    stopSelf()
                }, {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    Timber.e(it)
                }
            )
    }

    companion object {

        fun createIntent(context: Context): Intent {
            return Intent(context, SendPendingActionsService::class.java)
        }
    }
}
