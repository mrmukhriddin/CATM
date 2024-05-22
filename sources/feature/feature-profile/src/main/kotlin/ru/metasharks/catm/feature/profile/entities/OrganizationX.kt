package ru.metasharks.catm.feature.profile.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrganizationX(

    @SerialName("id")
    val id: Long,

    @SerialName("name")
    val name: String,
)
