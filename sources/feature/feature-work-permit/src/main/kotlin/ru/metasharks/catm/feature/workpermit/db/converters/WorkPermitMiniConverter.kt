package ru.metasharks.catm.feature.workpermit.db.converters

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.metasharks.catm.feature.workpermit.entities.WorkPermitX

class WorkPermitMiniConverter {

    @TypeConverter
    fun fromWorkPermitX(value: WorkPermitX): String = Json.encodeToString(value)

    @TypeConverter
    fun toWorkPermitX(encoded: String): WorkPermitX = Json.decodeFromString(encoded)
}
