package ru.metasharks.catm.feature.workpermit.entities.certain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExtensionX(

    @SerialName("id")
    val id: Long,

    @SerialName("signers")
    val signers: List<SignerX>,

    @SerialName("date_end")
    val dateEnd: String,

    @SerialName("time_end")
    val timeEnd: String
)
