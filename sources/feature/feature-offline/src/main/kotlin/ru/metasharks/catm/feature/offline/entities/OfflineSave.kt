package ru.metasharks.catm.feature.offline.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "offline_saves")
@Serializable
data class OfflineSave(

    @PrimaryKey
    val typeCode: String,

    val userId: Long,

    val lastTimeSaved: Long,
)
