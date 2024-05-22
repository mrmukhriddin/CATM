package ru.metasharks.catm.feature.notifications.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TagsEnvelopeX(

    @SerialName("user_id")
    val userId: Long? = null,

    @SerialName("briefing_id")
    val briefingId: Long? = null,

    @SerialName("work_permit_id")
    val workPermitId: Long? = null,
)
