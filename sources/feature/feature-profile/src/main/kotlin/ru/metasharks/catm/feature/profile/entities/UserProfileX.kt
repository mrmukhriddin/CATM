package ru.metasharks.catm.feature.profile.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.metasharks.catm.feature.profile.db.converters.UserConverter

@Entity(tableName = "user_profile")
@TypeConverters(UserConverter::class)
@Serializable
data class UserProfileX(

    @PrimaryKey
    @SerialName("id")
    val id: Long,

    @SerialName("avatar")
    val avatar: String?,

    @SerialName("last_name")
    val lastName: String,

    @SerialName("first_name")
    val firstName: String,

    @SerialName("middle_name")
    val middleName: String?,

    @SerialName("email")
    val email: String,

    @SerialName("phone_number")
    val phoneNumber: String? = null,

    @SerialName("position")
    val position: String? = null,

    @SerialName("permission")
    val permission: String? = null,

    @SerialName("protective_equipment_card")
    val protectiveEquipmentCard: ProtectiveEquipmentCardX? = null,

    @SerialName("medical_exam")
    val medicalExam: MedicalExamX? = null,

    @SerialName("common_document")
    val commonDoc: CommonDocumentX? = null,

    @SerialName("unit")
    val unit: UnitX? = null,

    @SerialName("tools")
    val tools: List<ToolX>? = null,

    @SerialName("trainings")
    val trainings: List<TrainingX>? = null,

    @SerialName("briefings")
    val briefings: List<UserBriefingX>? = null,
)
