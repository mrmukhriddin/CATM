package ru.metasharks.catm.feature.workpermit.entities.certain.dailypermit

import kotlinx.serialization.Serializable

@Serializable
data class CreateDailyPermitDataX(
    val permitterId: Long,
    val date: String,
)
