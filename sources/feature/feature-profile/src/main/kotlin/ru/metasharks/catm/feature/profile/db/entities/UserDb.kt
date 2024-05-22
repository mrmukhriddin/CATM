package ru.metasharks.catm.feature.profile.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.serialization.Serializable
import ru.metasharks.catm.feature.profile.db.converters.UserConverter
import ru.metasharks.catm.feature.profile.entities.UserProfileX

@Entity(tableName = "user_db")
@TypeConverters(UserConverter::class)
@Serializable
data class UserDb(
    @PrimaryKey
    val id: Long,
    val userProfileX: UserProfileX,
) {

    val email: String
        get() = userProfileX.email
}
