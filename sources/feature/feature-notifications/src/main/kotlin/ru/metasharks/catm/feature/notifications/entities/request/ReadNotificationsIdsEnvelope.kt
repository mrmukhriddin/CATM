package ru.metasharks.catm.feature.notifications.entities.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReadNotificationsIdsEnvelope(

    @SerialName("ids")
    val ids: List<Long>
)
