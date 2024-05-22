package ru.metasharks.catm.feature.notifications.db.entities

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.metasharks.catm.feature.notifications.entities.NotificationContent

internal class NotificationConverter {

    private val json = Json

    @TypeConverter
    fun fromNotification(value: NotificationContent): String {
        val toSave = json.encodeToString(value)
//        val toSave = when (value) {
//            is NotificationContent.BriefingInvite ->
//                json.encodeToString(NotificationContent.BriefingInvite.serializer(), value)
//            is NotificationContent.BriefingWorkerInvite ->
//                json.encodeToString(NotificationContent.BriefingWorkerInvite.serializer(), value)
//            is NotificationContent.DocumentExpiredDirector ->
//                json.encodeToString(NotificationContent.DocumentExpiredDirector.serializer(), value)
//            is NotificationContent.DocumentExpiredUser ->
//                json.encodeToString(NotificationContent.DocumentExpiredUser.serializer(), value)
//            is NotificationContent.WorkPermitSignerInvite ->
//                json.encodeToString(NotificationContent.WorkPermitSignerInvite.serializer(), value)
//        }
        return toSave
    }

    @TypeConverter
    fun toNotification(encoded: String): NotificationContent {
        return json.decodeFromString(encoded)
    }
}
