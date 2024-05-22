package ru.metasharks.catm.feature.workpermit.entities.certain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.metasharks.catm.feature.workpermit.entities.worktype.WorkTypeX

@Serializable
data class WorkPermitInnerX(

    @SerialName("created")
    val created: String,

    @SerialName("id")
    val id: Long,

    @SerialName("start_date")
    val startDate: String,

    @SerialName("start_time")
    val startTime: String,

    @SerialName("expiration_date")
    val endDate: String,

    @SerialName("expiration_time")
    val endTime: String,

    // TODO: later make new model
    @SerialName("organization_delete-this_")
    val organization: String = "PLACEHOLDER",

    @SerialName("place")
    val place: String,

    @SerialName("shift")
    val shift: String,

    @SerialName("status")
    val status: String,

    @SerialName("status_name")
    val statusName: String,

    @SerialName("work_type")
    val workType: WorkTypeX,

    @SerialName("document")
    val document: DocumentX?,

    @SerialName("briefing_id")
    val briefingId: Long,
)
