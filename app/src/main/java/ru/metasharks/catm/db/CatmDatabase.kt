package ru.metasharks.catm.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.metasharks.catm.db.CatmDatabase.Companion.DB_VERSION
import ru.metasharks.catm.feature.briefings.db.BriefingTypeDao
import ru.metasharks.catm.feature.briefings.db.BriefingsDao
import ru.metasharks.catm.feature.briefings.entities.BriefingTypeX
import ru.metasharks.catm.feature.briefings.entities.BriefingX
import ru.metasharks.catm.feature.notifications.db.NotificationsDao
import ru.metasharks.catm.feature.notifications.db.entities.NotificationDb
import ru.metasharks.catm.feature.offline.db.BriefingOfflineDao
import ru.metasharks.catm.feature.offline.db.OfflineSaveDao
import ru.metasharks.catm.feature.offline.db.PendingActionDao
import ru.metasharks.catm.feature.offline.db.WorkPermitCreateOfflineToolsDao
import ru.metasharks.catm.feature.offline.entities.OfflineSave
import ru.metasharks.catm.feature.offline.pending.entities.PendingAction
import ru.metasharks.catm.feature.offline.save.briefings.BriefingOfflineData
import ru.metasharks.catm.feature.offline.save.workpermit.WorkPermitCreateOfflineTools
import ru.metasharks.catm.feature.profile.db.UserDao
import ru.metasharks.catm.feature.profile.db.entities.UserDb
import ru.metasharks.catm.feature.profile.entities.UserProfileX
import ru.metasharks.catm.feature.workpermit.db.WorkPermitDao
import ru.metasharks.catm.feature.workpermit.db.entities.WorkPermitDb
import ru.metasharks.catm.feature.workpermit.db.entities.WorkPermitMiniDb
import ru.metasharks.catm.feature.workpermit.entities.statuses.StatusesEnvelopeX

@Database(
    entities = [
        UserDb::class,
        UserProfileX::class,
        WorkPermitDb::class,
        WorkPermitMiniDb::class,
        BriefingX::class,
        BriefingTypeX::class,
        BriefingOfflineData::class,
        NotificationDb::class,
        OfflineSave::class,
        WorkPermitCreateOfflineTools::class,
        PendingAction::class,
        StatusesEnvelopeX::class,
    ], version = DB_VERSION
)
internal abstract class CatmDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun workPermitDao(): WorkPermitDao

    abstract fun briefingsDao(): BriefingsDao

    abstract fun briefingTypeDao(): BriefingTypeDao

    abstract fun notificationsDao(): NotificationsDao

    abstract fun offlineSavesDao(): OfflineSaveDao

    abstract fun briefingOfflineDao(): BriefingOfflineDao

    abstract fun workPermitCreateOfflineToolsDao(): WorkPermitCreateOfflineToolsDao

    abstract fun pendingActionDao(): PendingActionDao

    internal companion object {

        const val DB_VERSION = 1
    }
}
