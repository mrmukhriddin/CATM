package ru.metasharks.catm.feature.workpermit.entities.responsiblemanager

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkPermitUserX(

    @SerialName("id")
    val id: Long,

    @SerialName("last_name")
    val lastName: String,

    @SerialName("first_name")
    val firstName: String,

    @SerialName("middle_name")
    val middleName: String,

    @SerialName("is_ready")
    val isReady: Boolean,

    @SerialName("position")
    val position: String? = null,

    @SerialName("avatar")
    val avatar: String? = null
)
