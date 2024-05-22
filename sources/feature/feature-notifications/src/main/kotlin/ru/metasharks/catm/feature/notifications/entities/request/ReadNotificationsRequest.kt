package ru.metasharks.catm.feature.notifications.entities.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.metasharks.catm.feature.notifications.entities.NotificationTypes

@Serializable
data class ReadNotificationsRequest(

    @SerialName("data")
    val data: ReadNotificationsIdsEnvelope,

    @SerialName("type")
    val type: String = NotificationTypes.READ_NOTIFICATIONS,
)
