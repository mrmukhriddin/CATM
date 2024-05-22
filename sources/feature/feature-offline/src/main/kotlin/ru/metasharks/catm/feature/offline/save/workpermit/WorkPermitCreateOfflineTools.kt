package ru.metasharks.catm.feature.offline.save.workpermit

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.metasharks.catm.feature.offline.save.workpermit.converters.WorkPermitCreateOfflineToolsConverter
import ru.metasharks.catm.feature.profile.entities.OrganizationX
import ru.metasharks.catm.feature.workpermit.entities.options.OptionsEnvelopeX
import ru.metasharks.catm.feature.workpermit.entities.responsiblemanager.WorkPermitUserX

@Serializable
@TypeConverters(WorkPermitCreateOfflineToolsConverter::class)
@Entity(tableName = "create_work_permit_offline_tools")
data class WorkPermitCreateOfflineTools(

    @PrimaryKey
    @SerialName("user_id")
    val userId: Long,

    @SerialName("organization")
    val organization: OrganizationX,

    @SerialName("responsible_employees")
    val responsibleEmployees: List<WorkPermitUserX>,

    @SerialName("except_responsible_employees")
    val exceptResponsibleEmployees: List<WorkPermitUserX>,

    @SerialName("options")
    val allOptions: OptionsEnvelopeX,
)
