package ru.metasharks.catm.feature.notifications

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter
import com.neovisionaries.ws.client.WebSocketError
import com.neovisionaries.ws.client.WebSocketException
import com.neovisionaries.ws.client.WebSocketFactory
import com.neovisionaries.ws.client.WebSocketFrame
import com.neovisionaries.ws.client.WebSocketListener
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.functions.Functions
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import ru.metasharks.catm.core.network.internet.InternetListener
import ru.metasharks.catm.core.network.request.auth.AuthTokenStorage
import ru.metasharks.catm.core.network.socket.SocketUrlProvider
import ru.metasharks.catm.feature.notifications.entities.BaseNotificationEnvelopeX
import ru.metasharks.catm.feature.notifications.repository.NotificationsRepository
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class NotificationsService : Service() {

    @Inject
    lateinit var authTokenStorage: AuthTokenStorage

    @Inject
    lateinit var notificationsMapper: NotificationsMapper

    @Inject
    lateinit var notificationsRepository: NotificationsRepository

    @Inject
    lateinit var socketUrlProvider: SocketUrlProvider

    private val publishSubject: PublishSubject<NotificationEnvelope> = PublishSubject.create()

    private var isSettingUp = false
    private var isStarted = false

    private val internetListenerCallback = object : InternetListener.Callback {

        override fun onInternetAcquired() {
            Timber.d("previouslyAvailable.not() && currentlyAvailable -> connect")
            setupNotifications()
        }

        override fun onInternetLost() {
            Timber.d("previouslyAvailable && currentlyAvailable.not() -> disconnect")
            publishSubject.onNext(
                NotificationEnvelope.Error(
                    WebSocketException(
                        WebSocketError.SOCKET_CONNECT_ERROR,
                        "Lost Internet connection"
                    )
                )
            )
        }
    }

    private val internetListener = InternetListener(internetListenerCallback)

    private var _webSocket: WebSocket? = null
    private val webSocket: WebSocket
        get() = requireNotNull(_webSocket)

    private val webSocketListener: WebSocketListener = object : WebSocketAdapter() {

        override fun onTextMessage(websocket: WebSocket, text: String) {
            super.onTextMessage(websocket, text)
            publishSubject.onNext(NotificationEnvelope.RawNotification(text))
        }

        override fun onFrameSent(websocket: WebSocket, frame: WebSocketFrame) {
            Timber.d("onFrameSent -> ${frame.payloadText}")
            super.onFrameSent(websocket, frame)
        }

        override fun onFrameError(
            websocket: WebSocket?,
            cause: WebSocketException,
            frame: WebSocketFrame
        ) {
            Timber.e(cause)
            super.onFrameError(websocket, cause, frame)
        }

        override fun onCloseFrame(websocket: WebSocket, frame: WebSocketFrame) {
            Timber.d("onCloseFrame -> ${frame.payloadText}")
            super.onCloseFrame(websocket, frame)
        }
    }

    private fun createWebSocket(): Single<WebSocket> {
        return Single.fromCallable { _webSocket?.sendClose() ?: Unit }
            .flatMap {
                Single.fromCallable {
                    _webSocket =
                        WebSocketFactory()
                            .setConnectionTimeout(CONNECT_TIMEOUT)
                            .createSocket(socketUrlProvider(requireNotNull(authTokenStorage.authToken)))
                }
            }
            .flatMap { Single.fromCallable(webSocket::connect) }
            .map { it.addListener(webSocketListener) }
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.getBooleanExtra(EXTRA_STOP_FLAG, false) == true) {
            stopForeground(true)
            stopSelf()
            return START_STICKY
        }
        if (isStarted.not()) {
            startForeground(
                ONGOING_NOTIFICATION_ID,
                NotificationFactory.createForegroundNotification(this, initialStart = true)
            )
        }
        setupNetworkCallback()
        return START_STICKY
    }

    private fun setupNetworkCallback() {
        internetListener.setup(this)
    }

    private fun setupNotifications() {
        if (isSettingUp) {
            return
        }
        isSettingUp = true
        val isValid = authTokenStorage.isValid
        if (isValid.not()) {
            stopForeground(true)
            stopSelf()
            return
        }
        if (setupWebSocket()) {
            startForeground(
                ONGOING_NOTIFICATION_ID,
                NotificationFactory.createForegroundNotification(this)
            )
        }
    }

    private fun save(envelope: BaseNotificationEnvelopeX) {
        processIncomingNotification(envelope)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    private fun setupWebSocket(): Boolean {
        if (_webSocket?.isOpen == true) {
            return false
        }
        publishSubject
            .map(notificationsMapper::mapNotificationsEnvelope)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { envelope: NotificationEnvelope ->
                when (envelope) {
                    is NotificationEnvelope.Notification -> {
                        if (envelope.envelopeX is BaseNotificationEnvelopeX.UnreadNotifications) {
                            if (isStarted.not()) {
                                NotificationFactory.createNotification(this, envelope.envelopeX)
                                save(envelope.envelopeX)
                                isStarted = true
                            } else {
                                saveAndShowNotExisting(envelope.envelopeX)
                            }
                        } else {
                            save(envelope.envelopeX)
                        }
                    }
                    is NotificationEnvelope.Error -> {
                        val error = envelope.throwable

                        _webSocket = null
                        webSocketProvider = null

                        NotificationFactory.createErrorNotification(this, error)
                        Timber.e(error)
                    }
                    else -> Unit
                }
            }
        createWebSocket()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                webSocketProvider = it
                isSettingUp = false
            }
            .subscribe(Functions.emptyConsumer()) { error ->
                webSocketProvider = null
                Timber.e(error)
            }
        return true
    }

    private fun saveAndShowNotExisting(envelopeX: BaseNotificationEnvelopeX.UnreadNotifications): Single<Unit> {
        return notificationsRepository.getNotificationsSingle()
            .map { notificationsFromDb ->
                envelopeX.notifications.filterNot { serverNotification ->
                    notificationsFromDb.any { serverNotification.notificationId == it.notificationId }
                }.onEach { NotificationFactory.createNotification(this, it) }
            }
            .flatMap(notificationsRepository::saveIncomingNotifications)
    }

    private fun processIncomingNotification(envelope: BaseNotificationEnvelopeX): Single<Unit> {
        return when (envelope) {
            is BaseNotificationEnvelopeX.Notification -> {
                notificationsRepository.saveNotification(envelope.content)
            }
            is BaseNotificationEnvelopeX.UnreadNotifications -> {
                notificationsRepository.saveIncomingNotifications(envelope.notifications)
            }
        }
    }

    companion object {

        private const val CONNECT_TIMEOUT = 30000

        var webSocketProvider: WebSocket? = null

        fun requireWebSocket(): WebSocket {
            return requireNotNull(webSocketProvider)
        }

        private const val EXTRA_STOP_FLAG = "stop"

        const val MESSAGES_COUNT_ID = -2
        const val ONGOING_NOTIFICATION_ID = -1
        const val CODE_READ_NOTIFICATIONS = 1

        fun createStartIntent(context: Context): Intent {
            return Intent(context, NotificationsService::class.java)
        }

        fun createStopIntent(context: Context): Intent {
            return Intent(context, NotificationsService::class.java)
                .putExtra(EXTRA_STOP_FLAG, true)
        }
    }
}
