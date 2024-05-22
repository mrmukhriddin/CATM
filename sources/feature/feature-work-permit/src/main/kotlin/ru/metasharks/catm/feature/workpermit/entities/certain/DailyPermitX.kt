package ru.metasharks.catm.feature.workpermit.entities.certain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DailyPermitX(

    @SerialName("id")
    val id: Long,

    @SerialName("date_start")
    val dateStart: String,

    @SerialName("date_end")
    val dateEnd: String?,

    @SerialName("signers")
    val signers: List<SignerX>
)
