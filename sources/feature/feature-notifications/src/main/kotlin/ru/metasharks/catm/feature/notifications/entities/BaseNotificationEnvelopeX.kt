package ru.metasharks.catm.feature.notifications.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class BaseNotificationEnvelopeX {

    @Serializable
    @SerialName(NotificationTypes.NOTIFICATION)
    data class Notification(

        @SerialName("data")
        val content: NotificationContent
    ) : BaseNotificationEnvelopeX()

    @Serializable
    @SerialName(NotificationTypes.UNREAD_NOTIFICATIONS)
    data class UnreadNotifications(

        @SerialName("data")
        val notifications: List<NotificationContent>
    ) : BaseNotificationEnvelopeX()
}
