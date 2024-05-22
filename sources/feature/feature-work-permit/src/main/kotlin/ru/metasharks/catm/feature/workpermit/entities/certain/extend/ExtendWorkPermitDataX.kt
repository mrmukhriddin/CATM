package ru.metasharks.catm.feature.workpermit.entities.certain.extend

import kotlinx.serialization.Serializable

@Serializable
data class ExtendWorkPermitDataX(

    val permitIssuerId: Long,

    val dateEnd: String,
)
