package ru.metasharks.catm.feature.notifications

import ru.metasharks.catm.feature.notifications.entities.BaseNotificationEnvelopeX

sealed class NotificationEnvelope {

    class RawNotification(val text: String) : NotificationEnvelope()

    class Notification(val envelopeX: BaseNotificationEnvelopeX) : NotificationEnvelope()

    class Error(val throwable: Throwable) : NotificationEnvelope()
}
