package ru.metasharks.catm.feature.briefings.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "briefing")
@Serializable
class BriefingX(
    @PrimaryKey
    @SerialName("id")
    val id: Int,

    @SerialName("title")
    val title: String,

    @SerialName("type")
    val type: Int,

    @SerialName("category")
    val category: Int,

    @SerialName("content")
    val content: String,

    @SerialName("file")
    val file: String?,

    @SerialName("file_size")
    val fileSize: Int?,

    @SerialName("file_name")
    val fileName: String?,

    @SerialName("quiz")
    val quiz: Long?,

    @SerialName("is_quiz_passed")
    val isQuizPassed: Boolean,

    @SerialName("briefing_signed_at")
    val briefingSignedAt: String?,

    @SerialName("userbriefing_signed_at")
    val userBriefingSignedAt: String? = null,
)
