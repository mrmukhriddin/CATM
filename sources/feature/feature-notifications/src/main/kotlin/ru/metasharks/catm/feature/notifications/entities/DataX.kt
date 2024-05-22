package ru.metasharks.catm.feature.notifications.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DataX(

    @SerialName("type")
    val type: String,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("message")
    val message: String,

    @SerialName("notification_id")
    val notificationId: Long,

    @SerialName("tags")
    val tags: TagsEnvelopeX? = null,
)
