package ru.metasharks.catm.feature.briefings.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "briefing_type")
@Serializable
class BriefingTypeX(

    @PrimaryKey
    @SerialName("id")
    val id: Int,

    @SerialName("value")
    val value: String
)
