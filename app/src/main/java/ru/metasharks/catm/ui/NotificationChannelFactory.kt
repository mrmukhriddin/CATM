package ru.metasharks.catm.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import ru.metasharks.catm.App

internal object NotificationChannelFactory {

    fun createNotificationChannels(app: App) {
        val channelForIncomingNotifications = app.createNotificationChannel(
            NotificationChannelCreationInfo.Types.INCOMING
        )
        val channelForOutgoingNotifications = app.createNotificationChannel(
            NotificationChannelCreationInfo.Types.OUTGOING
        )
        // Register the channel with the system
        val notificationManager: NotificationManager =
            app.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channelForIncomingNotifications)
        notificationManager.createNotificationChannel(channelForOutgoingNotifications)
    }

    private fun App.createNotificationChannel(
        type: NotificationChannelCreationInfo.Types
    ): NotificationChannel {
        val creationInfo = getCreationInfo(type)
        return NotificationChannel(
            creationInfo.channelId, creationInfo.channelName, creationInfo.importance,
        ).apply {
            description = creationInfo.description
        }
    }

    private fun Context.getCreationInfo(type: NotificationChannelCreationInfo.Types): NotificationChannelCreationInfo {
        return when (type) {
            NotificationChannelCreationInfo.Types.INCOMING -> {
                NotificationChannelCreationInfo.Incoming(
                    channelId = getString(ru.metasharks.catm.core.network.R.string.notifications_incoming_id),
                    importance = NotificationManager.IMPORTANCE_DEFAULT,
                    channelName = getString(ru.metasharks.catm.core.network.R.string.notifications_incoming_name),
                    description = getString(ru.metasharks.catm.core.network.R.string.notifications_incoming_description)
                )
            }
            NotificationChannelCreationInfo.Types.OUTGOING -> {
                NotificationChannelCreationInfo.Outgoing(
                    channelId = getString(ru.metasharks.catm.core.network.R.string.notifications_outgoing_id),
                    importance = NotificationManager.IMPORTANCE_LOW,
                    channelName = getString(ru.metasharks.catm.core.network.R.string.notifications_outgoing_name),
                    description = getString(ru.metasharks.catm.core.network.R.string.notifications_outgoing_description)
                )
            }
        }
    }

    sealed class NotificationChannelCreationInfo {

        abstract val importance: Int
        abstract val channelName: String
        abstract val channelId: String
        abstract val description: String

        class Incoming(
            override val importance: Int,
            override val channelName: String,
            override val channelId: String,
            override val description: String,
        ) : NotificationChannelCreationInfo()

        class Outgoing(
            override val importance: Int,
            override val channelId: String,
            override val channelName: String,
            override val description: String,
        ) : NotificationChannelCreationInfo()

        enum class Types {
            INCOMING, OUTGOING
        }
    }
}
