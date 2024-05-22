package ru.metasharks.catm.feature.workpermit.entities.statuses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatusX(

    @SerialName("status")
    val status: String,

    @SerialName("status_name")
    val statusName: String
) {

    companion object {

        const val ALL = "all"
    }
}
