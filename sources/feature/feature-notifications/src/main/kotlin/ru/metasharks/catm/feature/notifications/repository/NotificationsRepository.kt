package ru.metasharks.catm.feature.notifications.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.metasharks.catm.core.network.offline.CurrentUserIdProvider
import ru.metasharks.catm.core.storage.prefs.PreferencesDelegate
import ru.metasharks.catm.core.storage.prefs.PreferencesProvider
import ru.metasharks.catm.feature.notifications.NotificationsService
import ru.metasharks.catm.feature.notifications.db.NotificationsDao
import ru.metasharks.catm.feature.notifications.db.entities.NotificationDb
import ru.metasharks.catm.feature.notifications.entities.NotificationContent
import ru.metasharks.catm.feature.notifications.entities.request.ReadNotificationsIdsEnvelope
import ru.metasharks.catm.feature.notifications.entities.request.ReadNotificationsRequest
import ru.metasharks.catm.feature.profile.db.entities.UserDb
import ru.metasharks.catm.feature.profile.usecase.GetCurrentUserUseCase
import ru.metasharks.catm.utils.date.LocalDateUtils
import javax.inject.Inject

interface NotificationsRepository {

    fun getNotifications(): Observable<List<NotificationDb>>

    fun getNotificationsSingle(): Single<List<NotificationDb>>

    fun saveIncomingNotifications(notifications: List<NotificationContent>): Single<Unit>

    fun saveNotification(notification: NotificationContent): Single<Unit>

    fun readNotification(notificationId: Long): Completable

    fun readAllNotifications(): Completable
}

internal class NotificationsRepositoryImpl @Inject constructor(
    private val notificationsDao: NotificationsDao,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val preferencesProvider: PreferencesProvider,
    private val currentUserIdProvider: CurrentUserIdProvider,
    @ApplicationContext private val context: Context
) : NotificationsRepository {

    private val json = Json { encodeDefaults = true }

    private var hasNotificationsUnread by PreferencesDelegate(
        { preferencesProvider.applicationPreferences },
        context.getString(ru.metasharks.catm.core.storage.R.string.preferences_key_has_unread_notifications),
        false
    )

    override fun saveIncomingNotifications(notifications: List<NotificationContent>): Single<Unit> {
        return Single.fromCallable { hasNotificationsUnread = true }
            .flatMap { getCurrentUserUseCase(initialLoad = false) }
            .flatMap { user ->
                notificationsDao.insert(
                    notifications.map { content -> parseNotification(user, content) }
                )
            }.flatMap {
                Single.just(Unit)
            }
    }

    override fun saveNotification(notification: NotificationContent): Single<Unit> {
        return Single.fromCallable { hasNotificationsUnread = true }
            .flatMap { getCurrentUserUseCase(initialLoad = false) }
            .flatMap { user ->
                notificationsDao.insert(parseNotification(user, notification))
            }.flatMap {
                Single.just(Unit)
            }
    }

    override fun readNotification(notificationId: Long): Completable {
        return readUnreadNotifications(listOf(notificationId))
            .flatMap {
                notificationsDao.delete(notificationId).toSingle { }
            }.flatMap {
                notificationsDao.isEmptyTableForUser(currentUserIdProvider.currentUserId)
            }.flatMapCompletable { isTableEmpty ->
                if (isTableEmpty) {
                    Completable.fromAction { hasNotificationsUnread = false }
                } else {
                    Completable.complete()
                }
            }
    }

    override fun readAllNotifications(): Completable {
        return getCurrentUserUseCase(initialLoad = false)
            .flatMap { user -> notificationsDao.getNotificationsForUserSingle(user.id) }
            .flatMap { notificationsInDb ->
                readUnreadNotifications(notificationsInDb.map { it.notificationId })
            }.flatMapCompletable {
                hasNotificationsUnread = false
                notificationsDao.nukeTable()
            }
    }

    private fun readUnreadNotifications(
        notificationsFromDp: List<Long>
    ): Single<Unit> {
        return Single.fromCallable {
            val request =
                ReadNotificationsRequest(
                    data = ReadNotificationsIdsEnvelope(
                        ids = notificationsFromDp
                    )
                )
            val jsonRequest = json.encodeToString(request)
            val webSocket = NotificationsService.requireWebSocket()
            webSocket.sendText(jsonRequest)
        }
    }

    private fun parseNotification(user: UserDb, content: NotificationContent): NotificationDb {
        return NotificationDb(
            notificationId = content.notificationId,
            userId = user.id,
            content = content,
            isRead = false,
            timestamp = LocalDateUtils.parseISO8601toLocalDateTime(content.createdAt)
                .toDateTime().millis
        )
    }

    override fun getNotifications(): Observable<List<NotificationDb>> {
        return getCurrentUserUseCase(initialLoad = false)
            .flatMapObservable { user ->
                notificationsDao.getNotificationsForUser(user.id)
            }
    }

    override fun getNotificationsSingle(): Single<List<NotificationDb>> {
        return getCurrentUserUseCase(initialLoad = false)
            .flatMap { notificationsDao.getNotificationsForUserSingle(it.id) }
    }
}
