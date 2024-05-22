package ru.metasharks.catm.feature.workpermit.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.serialization.Serializable
import ru.metasharks.catm.feature.workpermit.db.converters.WorkPermitConverter
import ru.metasharks.catm.feature.workpermit.entities.certain.WorkPermitDetailsX

@Entity(tableName = "work_permits_details")
@TypeConverters(WorkPermitConverter::class)
@Serializable
data class WorkPermitDb(

    @PrimaryKey
    val id: Long,

    val data: WorkPermitDetailsX
)
