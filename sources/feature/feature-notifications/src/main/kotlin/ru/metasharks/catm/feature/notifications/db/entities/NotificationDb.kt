package ru.metasharks.catm.feature.notifications.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.serialization.Serializable
import ru.metasharks.catm.feature.notifications.entities.NotificationContent

@Entity(tableName = "notifications")
@TypeConverters(NotificationConverter::class)
@Serializable
data class NotificationDb(

    @PrimaryKey
    val notificationId: Long,

    val userId: Long,

    val timestamp: Long,

    val isRead: Boolean,

    val content: NotificationContent
)
