package ru.metasharks.catm.feature.profile.entities.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.metasharks.catm.feature.profile.entities.MedicalExamX
import ru.metasharks.catm.feature.profile.entities.UnitX

@Serializable
data class WorkerX(
    @SerialName("id")
    val id: Int,
    @SerialName("avatar")
    val avatar: String?,
    @SerialName("last_name")
    val lastName: String,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("position")
    val position: String?,
    @SerialName("briefing")
    val briefingLight: BriefingLightX?,
    @SerialName("unit")
    val unit: UnitX?,
    @SerialName("medical_exam")
    val medicalExamLightX: MedicalExamX? = null
)
