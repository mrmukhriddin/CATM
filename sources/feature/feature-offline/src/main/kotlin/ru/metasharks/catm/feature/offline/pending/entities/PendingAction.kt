package ru.metasharks.catm.feature.offline.pending.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.serialization.Serializable
import ru.metasharks.catm.feature.offline.pending.converters.PendingActionConverter

@Serializable
@TypeConverters(PendingActionConverter::class)
@Entity(tableName = "pending_actions")
data class PendingAction(

    val userId: Long,

    val payload: PendingActionPayload,

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
)
