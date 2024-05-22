package ru.metasharks.catm.feature.workpermit.entities.certain.dailypermit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class CreateDailyPermitResponseX(

    @SerialName("daily_id")
    private val dailyId: Long,
)
