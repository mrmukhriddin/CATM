package ru.metasharks.catm.feature.workpermit.entities.certain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GasAirAnalysisX(

    @SerialName("id")
    val id: Long,

    @SerialName("work_permit_id")
    val workPermitId: Long,

    @SerialName("user")
    val user: SignerUserX,

    @SerialName("components")
    val components: String,

    @SerialName("concentration")
    val concentration: String,

    @SerialName("date")
    val date: String,

    @SerialName("date_next")
    val dateNext: String,

    @SerialName("device_model")
    val deviceModel: String,

    @SerialName("device_number")
    val deviceNumber: String,

    @SerialName("place")
    val place: String,

    @SerialName("result")
    val result: String,

    @SerialName("time")
    val time: String,
)
