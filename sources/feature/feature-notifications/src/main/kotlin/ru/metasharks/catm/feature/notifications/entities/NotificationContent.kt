package ru.metasharks.catm.feature.notifications.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable()
sealed class NotificationContent {

    @SerialName("created_at")
    abstract val createdAt: String

    @SerialName("message")
    abstract val message: String

    @SerialName("notification_id")
    abstract val notificationId: Long

    @Serializable
    @SerialName(NotificationContentTypes.BRIEFING_INVITE)
    data class BriefingInvite(
        @SerialName("created_at")
        override val createdAt: String,
        @SerialName("message")
        override val message: String,
        @SerialName("notification_id")
        override val notificationId: Long,
        @SerialName("briefing_id")
        val briefingId: Long,
    ) : NotificationContent()

    @Serializable
    @SerialName(NotificationContentTypes.BRIEFING_WORKER_INVITE)
    data class BriefingWorkerInvite(
        @SerialName("created_at")
        override val createdAt: String,
        @SerialName("message")
        override val message: String,
        @SerialName("notification_id")
        override val notificationId: Long,
        @SerialName("user_id")
        val userId: Long,
        @SerialName("briefing_id")
        val briefingId: Long,
    ) : NotificationContent()

    @Serializable
    @SerialName(NotificationContentTypes.DOCUMENT_EXPIRED_USER)
    data class DocumentExpiredUser(
        @SerialName("created_at")
        override val createdAt: String,
        @SerialName("message")
        override val message: String,
        @SerialName("notification_id")
        override val notificationId: Long,
    ) : NotificationContent()

    @Serializable
    @SerialName(NotificationContentTypes.DOCUMENT_EXPIRED_DIRECTOR)
    data class DocumentExpiredDirector(
        @SerialName("created_at")
        override val createdAt: String,
        @SerialName("message")
        override val message: String,
        @SerialName("notification_id")
        override val notificationId: Long,
        @SerialName("user_id")
        val userId: Long,
    ) : NotificationContent()

    @Serializable
    @SerialName(NotificationContentTypes.WORK_PERMIT_SIGNER_INVITE)
    data class WorkPermitSignerInvite(
        @SerialName("created_at")
        override val createdAt: String,
        @SerialName("message")
        override val message: String,
        @SerialName("notification_id")
        override val notificationId: Long,
        @SerialName("work_permit_id")
        val workPermitId: Long,
    ) : NotificationContent()
}
