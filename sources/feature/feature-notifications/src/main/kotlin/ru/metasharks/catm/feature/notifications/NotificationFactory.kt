package ru.metasharks.catm.feature.notifications

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ru.metasharks.catm.feature.notifications.entities.BaseNotificationEnvelopeX
import ru.metasharks.catm.feature.notifications.entities.NotificationContent

internal object NotificationFactory {

    private fun baseBuilder(context: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_ID_INCOMING)
            .setSmallIcon(R.drawable.ic_catm_mini_logo)
            .setSilent(false)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
    }

    fun createNotification(
        context: Context,
        envelope: BaseNotificationEnvelopeX
    ) {
        when (envelope) {
            is BaseNotificationEnvelopeX.UnreadNotifications -> {
                val builder = baseBuilder(context)
                    .setContentTitle(context.getString(R.string.notifications_count_title))
                    .setContentText(
                        context.resources.getQuantityString(
                            R.plurals.notifications_count_plural,
                            envelope.notifications.size,
                            envelope.notifications.size
                        )
                    )
                val manager = NotificationManagerCompat.from(context)
                manager.notify(
                    NotificationsService.MESSAGES_COUNT_ID,
                    builder.build()
                )
            }
            is BaseNotificationEnvelopeX.Notification -> {
                val builder = baseBuilder(context)
                    .setContentTitle(context.getString(R.string.notifications_count_title))
                    .setContentText(envelope.content.message)
                val manager = NotificationManagerCompat.from(context)
                manager.notify(
                    envelope.content.notificationId.toInt(),
                    builder.build()
                )
            }
        }
    }

    fun createNotification(
        context: Context,
        content: NotificationContent
    ) {
        val builder = baseBuilder(context)
            .setContentTitle(context.getString(R.string.notifications_count_title))
            .setContentText(content.message)
        val manager = NotificationManagerCompat.from(context)
        manager.notify(
            content.notificationId.toInt(),
            builder.build()
        )
    }

    fun createForegroundNotification(
        context: Context,
        initialStart: Boolean = false
    ): Notification {
        val title = if (initialStart) {
            context.getString(R.string.notifications_warning_awaiting_connection)
        } else {
            context.getString(R.string.notifications_warning_receiving)
        }
        return NotificationCompat.Builder(context, CHANNEL_ID_OUTGOING)
            .setSmallIcon(R.drawable.ic_catm_mini_logo)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setSilent(true)
            .setContentTitle(title)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    fun createErrorNotification(context: Context, error: Throwable) {
        val manager = NotificationManagerCompat.from(context)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID_OUTGOING)
            .setSmallIcon(R.drawable.ic_catm_mini_logo)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentTitle(context.getString(R.string.notifications_warning_not_receiving_error))
            .setContentText(error.message)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
        manager.notify(
            NotificationsService.ONGOING_NOTIFICATION_ID,
            builder.build()
        )
    }

    private const val CHANNEL_ID_INCOMING = "incoming_notifications"
    private const val CHANNEL_ID_OUTGOING = "outgoing_notifications"
}
