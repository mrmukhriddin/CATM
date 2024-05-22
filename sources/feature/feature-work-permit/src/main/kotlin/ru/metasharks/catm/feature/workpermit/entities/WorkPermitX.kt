package ru.metasharks.catm.feature.workpermit.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.metasharks.catm.feature.workpermit.entities.worktype.WorkTypeX

@Serializable
data class WorkPermitX(
    @SerialName("created")
    val created: String,
    @SerialName("id")
    val id: Long,
    @SerialName("place")
    val place: String,
    @SerialName("responsible_manager")
    val responsibleManager: String,
    @SerialName("status")
    val status: String,
    @SerialName("status_name")
    val statusName: String,
    @SerialName("work_type")
    val workType: WorkTypeX,
    @SerialName("workers_count")
    val workersCount: Int
)
