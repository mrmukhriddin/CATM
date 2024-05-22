package ru.metasharks.catm.feature.workpermit.db.converters

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.metasharks.catm.feature.workpermit.entities.certain.WorkPermitDetailsX

class WorkPermitConverter {

    @TypeConverter
    fun fromWorkPermitEnvelope(value: WorkPermitDetailsX): String = Json.encodeToString(value)

    @TypeConverter
    fun toWorkPermitEnvelope(encoded: String): WorkPermitDetailsX = Json.decodeFromString(encoded)
}
