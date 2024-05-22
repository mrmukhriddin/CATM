package ru.metasharks.catm.feature.offline.save.workpermit.converters

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.metasharks.catm.feature.profile.entities.OrganizationX
import ru.metasharks.catm.feature.workpermit.entities.options.OptionsEnvelopeX
import ru.metasharks.catm.feature.workpermit.entities.responsiblemanager.WorkPermitUserX

class WorkPermitCreateOfflineToolsConverter {

    @TypeConverter
    fun fromWorkPermitUserXList(value: List<WorkPermitUserX>): String = Json.encodeToString(value)

    @TypeConverter
    fun toWorkPermitUserXList(encoded: String): List<WorkPermitUserX> =
        Json.decodeFromString(encoded)

    @TypeConverter
    fun fromOptionsEnvelopeX(value: OptionsEnvelopeX): String = Json.encodeToString(value)

    @TypeConverter
    fun toOptionsEnvelopeX(encoded: String): OptionsEnvelopeX = Json.decodeFromString(encoded)

    @TypeConverter
    fun fromOrganizationX(value: OrganizationX): String = Json.encodeToString(value)

    @TypeConverter
    fun toOrganizationX(encoded: String): OrganizationX = Json.decodeFromString(encoded)
}
