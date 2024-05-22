package ru.metasharks.catm.feature.offline.save.briefings.converters

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.metasharks.catm.feature.briefings.entities.BriefingCategoryX
import ru.metasharks.catm.feature.briefings.entities.BriefingTypeX
import ru.metasharks.catm.feature.offline.save.briefings.BriefingQuizX

class BriefingOfflineDataConverter {

    @TypeConverter
    fun fromBriefingCategoryXList(value: List<BriefingCategoryX>): String =
        Json.encodeToString(value)

    @TypeConverter
    fun toBriefingCategoryXList(encoded: String): List<BriefingCategoryX> =
        Json.decodeFromString(encoded)

    @TypeConverter
    fun fromBriefingTypeXList(value: List<BriefingTypeX>): String = Json.encodeToString(value)

    @TypeConverter
    fun toBriefingTypeXList(encoded: String): List<BriefingTypeX> = Json.decodeFromString(encoded)

    @TypeConverter
    fun fromBriefingQuizXList(value: List<BriefingQuizX>): String = Json.encodeToString(value)

    @TypeConverter
    fun toBriefingQuizXList(encoded: String): List<BriefingQuizX> = Json.decodeFromString(encoded)
}
