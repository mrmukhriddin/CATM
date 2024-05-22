package ru.metasharks.catm.feature.workpermit.entities.certain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponsibleManagerX(

    @SerialName("first_name")
    val firstName: String,

    @SerialName("id")
    val id: Long,

    @SerialName("last_name")
    val lastName: String,

    @SerialName("middle_name")
    val middleName: String,

    @SerialName("position")
    val position: String?,

    @SerialName("avatar")
    val avatar: String?,

    @SerialName("unit")
    val unit: UnitX? = null
)
