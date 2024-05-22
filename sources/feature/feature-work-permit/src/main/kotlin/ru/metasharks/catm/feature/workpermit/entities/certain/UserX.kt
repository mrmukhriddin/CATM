package ru.metasharks.catm.feature.workpermit.entities.certain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignerUserX(

    @SerialName("id")
    val id: Long,

    @SerialName("first_name")
    val firstName: String,

    @SerialName("last_name")
    val lastName: String,

    @SerialName("middle_name")
    val middleName: String,

    @SerialName("avatar")
    val avatar: String?,

    @SerialName("position")
    val position: String?,

    @SerialName("unit")
    val unitX: UnitX? = null
)

@Serializable
data class WorkerUserX(

    @SerialName("id")
    val id: Long,

    @SerialName("first_name")
    val firstName: String,

    @SerialName("last_name")
    val lastName: String,

    @SerialName("middle_name")
    val middleName: String,

    @SerialName("avatar")
    val avatar: String?,

    @SerialName("position")
    val position: String?,

    @SerialName("is_ready")
    val isReady: Boolean? = null,
)
