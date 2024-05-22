package ru.metasharks.catm.feature.workpermit.entities.statuses

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.metasharks.catm.feature.workpermit.db.converters.StatusesEnvelopeConverter

@Serializable
@TypeConverters(StatusesEnvelopeConverter::class)
@Entity(tableName = "statuses_envelope")
data class StatusesEnvelopeX(

    @SerialName("statuses")
    val statuses: List<StatusX>,

    @SerialName("gen_id")
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
)
