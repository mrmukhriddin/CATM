package ru.metasharks.catm.feature.workpermit.entities.certain.workers

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateWorkersRequestX(

    @SerialName("brigade")
    val brigade: List<BrigadeX>,
)

@Serializable
data class BrigadeX(

    @SerialName("new_worker")
    val newWorkerId: Long,

    @SerialName("old_user_briefing")
    val oldWorkerBriefing: Long? = null,
)
