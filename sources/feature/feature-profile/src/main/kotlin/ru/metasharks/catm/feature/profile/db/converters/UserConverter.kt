package ru.metasharks.catm.feature.profile.db.converters

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.metasharks.catm.feature.profile.entities.CommonDocumentX
import ru.metasharks.catm.feature.profile.entities.MedicalExamX
import ru.metasharks.catm.feature.profile.entities.ProtectiveEquipmentCardX
import ru.metasharks.catm.feature.profile.entities.ToolX
import ru.metasharks.catm.feature.profile.entities.TrainingX
import ru.metasharks.catm.feature.profile.entities.UnitX
import ru.metasharks.catm.feature.profile.entities.UserBriefingX
import ru.metasharks.catm.feature.profile.entities.UserProfileX

class UserConverter {

    @TypeConverter
    fun fromUserProfile(value: UserProfileX): String = Json.encodeToString(value)

    @TypeConverter
    fun toUserProfile(encoded: String): UserProfileX = Json.decodeFromString(encoded)

    @TypeConverter
    fun fromUserBriefings(value: List<UserBriefingX>): String = Json.encodeToString(value)

    @TypeConverter
    fun toUserBriefings(encoded: String): List<UserBriefingX> = Json.decodeFromString(encoded)

    @TypeConverter
    fun fromTrainings(value: List<TrainingX>): String = Json.encodeToString(value)

    @TypeConverter
    fun toTrainings(encoded: String): List<TrainingX> = Json.decodeFromString(encoded)

    @TypeConverter
    fun fromMedicalExam(value: MedicalExamX?): String = Json.encodeToString(value)

    @TypeConverter
    fun toMedicalExam(encoded: String): MedicalExamX? = Json.decodeFromString(encoded)

    @TypeConverter
    fun fromProtectiveEquipmentCard(value: ProtectiveEquipmentCardX?): String =
        Json.encodeToString(value)

    @TypeConverter
    fun toProtectiveEquipmentCard(encoded: String): ProtectiveEquipmentCardX? =
        Json.decodeFromString(encoded)

    @TypeConverter
    fun fromTools(value: List<ToolX>): String = Json.encodeToString(value)

    @TypeConverter
    fun toTools(encoded: String): List<ToolX> = Json.decodeFromString(encoded)

    @TypeConverter
    fun fromUnit(value: UnitX?): String = Json.encodeToString(value)

    @TypeConverter
    fun toUnit(encoded: String): UnitX? = Json.decodeFromString(encoded)

    @TypeConverter
    fun fromCommonDocumentX(value: CommonDocumentX?): String = Json.encodeToString(value)

    @TypeConverter
    fun toCommonDocumentX(encoded: String): CommonDocumentX? = Json.decodeFromString(encoded)
}
