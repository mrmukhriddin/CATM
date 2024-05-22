package ru.metasharks.catm.feature.workpermit.db.converters

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.metasharks.catm.feature.workpermit.entities.statuses.StatusX

class StatusesEnvelopeConverter {

    @TypeConverter
    fun fromStatusList(value: List<StatusX>): String = Json.encodeToString(value)

    @TypeConverter
    fun toStatusList(encoded: String): List<StatusX> = Json.decodeFromString(encoded)
}
