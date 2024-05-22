package ru.metasharks.catm.feature.workpermit.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.serialization.Serializable
import ru.metasharks.catm.feature.workpermit.db.converters.WorkPermitMiniConverter
import ru.metasharks.catm.feature.workpermit.entities.WorkPermitX

@Entity(tableName = "work_permits_mini")
@TypeConverters(WorkPermitMiniConverter::class)
@Serializable
data class WorkPermitMiniDb(

    @PrimaryKey
    val id: Long,

    val userId: Long,

    val data: WorkPermitX,
)
