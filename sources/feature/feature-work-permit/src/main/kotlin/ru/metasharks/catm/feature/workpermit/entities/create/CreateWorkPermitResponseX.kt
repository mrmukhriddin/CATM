package ru.metasharks.catm.feature.workpermit.entities.create

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateWorkPermitResponseX(

    @SerialName("id")
    val id: Long,

    @SerialName("another_factors")
    val anotherFactors: List<Int>?,

    @SerialName("dangerous_factors")
    val dangerousFactors: List<Int>?,

    @SerialName("place")
    val place: String,

    @SerialName("shift")
    val shift: String,

    @SerialName("expiration_time")
    val expirationTime: String?,

    @SerialName("start_time")
    val startTime: String?,

    @SerialName("save_equipment")
    val saveEquipment: List<Int>?,

    @SerialName("used_equipment")
    val usedEquipment: List<Int>?,

    @SerialName("work_scheme")
    val workScheme: List<Int>?,

    @SerialName("work_type")
    val workType: Int,

    @SerialName("workers")
    val workers: List<Int>,

    @SerialName("status")
    val status: String,
)
