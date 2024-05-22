package ru.metasharks.catm.feature.workpermit.entities.certain.gasairanalysis

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddGasAirAnalysisEnvelopeX(

    @SerialName("work_permit")
    val workPermitId: Long,

    @SerialName("user")
    val userId: Long,

    @SerialName("date")
    val date: String,

    @SerialName("place")
    val place: String,

    @SerialName("result")
    val result: String,

    @SerialName("components")
    val components: String,

    @SerialName("concentration")
    val concentration: String,

    @SerialName("date_next")
    val dateNext: String,

    @SerialName("device_model")
    val deviceModel: String,

    @SerialName("device_number")
    val deviceNumber: String,
)
