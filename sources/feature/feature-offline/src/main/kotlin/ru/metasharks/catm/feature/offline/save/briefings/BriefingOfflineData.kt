package ru.metasharks.catm.feature.offline.save.briefings

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.metasharks.catm.feature.briefings.entities.BriefingCategoryX
import ru.metasharks.catm.feature.briefings.entities.BriefingTypeX
import ru.metasharks.catm.feature.offline.save.briefings.converters.BriefingOfflineDataConverter

@Serializable
@TypeConverters(BriefingOfflineDataConverter::class)
@Entity(tableName = "briefing_offline_data")
data class BriefingOfflineData(

    @SerialName("user_id")
    @PrimaryKey
    val userId: Long,

    val categories: List<BriefingCategoryX>,

    val types: List<BriefingTypeX>,

    val briefings: List<BriefingQuizX>,
)
