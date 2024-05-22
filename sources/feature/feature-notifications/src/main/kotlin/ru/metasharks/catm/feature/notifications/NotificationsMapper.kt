package ru.metasharks.catm.feature.notifications

import kotlinx.serialization.json.Json
import ru.metasharks.catm.feature.notifications.entities.serializers.BaseNotificationEnvelopeSerializer
import javax.inject.Inject

class NotificationsMapper @Inject constructor() {

    private val json = Json {
        coerceInputValues = true
        ignoreUnknownKeys = true
    }

    fun mapNotificationsEnvelope(message: NotificationEnvelope): NotificationEnvelope {
        return when (message) {
            is NotificationEnvelope.RawNotification -> NotificationEnvelope.Notification(
                json.decodeFromString(BaseNotificationEnvelopeSerializer, message.text)
            )
            is NotificationEnvelope.Error -> message
            else -> throw IllegalArgumentException("another sealed type is not supported here")
        }
    }
}
