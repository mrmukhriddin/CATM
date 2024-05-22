package ru.metasharks.catm.feature.offline.pending.converters

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.metasharks.catm.feature.offline.pending.entities.PendingActionPayload

class PendingActionConverter {

    @TypeConverter
    fun fromPendingActionPayload(value: PendingActionPayload): String = Json.encodeToString(value)

    @TypeConverter
    fun toPendingActionPayload(encoded: String): PendingActionPayload =
        Json.decodeFromString(encoded)
}
