package ru.metasharks.catm.di.module

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.metasharks.catm.db.CatmDatabase
import ru.metasharks.catm.feature.briefings.db.BriefingTypeDao
import ru.metasharks.catm.feature.briefings.db.BriefingsDao
import ru.metasharks.catm.feature.notifications.db.NotificationsDao
import ru.metasharks.catm.feature.offline.db.BriefingOfflineDao
import ru.metasharks.catm.feature.offline.db.OfflineSaveDao
import ru.metasharks.catm.feature.offline.db.PendingActionDao
import ru.metasharks.catm.feature.offline.db.WorkPermitCreateOfflineToolsDao
import ru.metasharks.catm.feature.profile.db.UserDao
import ru.metasharks.catm.feature.workpermit.db.WorkPermitDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RoomModule private constructor() {

    companion object {
        @Provides
        @Singleton
        fun provideDatabase(
            @ApplicationContext context: Context
        ): CatmDatabase = Room.databaseBuilder(
            context,
            CatmDatabase::class.java, "database"
        ).fallbackToDestructiveMigration().build()

        @Provides
        fun provideUserDao(db: CatmDatabase): UserDao = db.userDao()

        @Provides
        fun provideWorkPermitDao(db: CatmDatabase): WorkPermitDao = db.workPermitDao()

        @Provides
        fun provideBriefingsDao(db: CatmDatabase): BriefingsDao = db.briefingsDao()

        @Provides
        fun provideBriefingTypeDao(db: CatmDatabase): BriefingTypeDao = db.briefingTypeDao()

        @Provides
        fun provideNotificationsDao(db: CatmDatabase): NotificationsDao = db.notificationsDao()

        @Provides
        fun provideOfflineSavesDao(db: CatmDatabase): OfflineSaveDao = db.offlineSavesDao()

        @Provides
        fun provideBriefingOfflineDao(db: CatmDatabase): BriefingOfflineDao = db.briefingOfflineDao()

        @Provides
        fun provideWorkPermitCreateOfflineToolsDao(db: CatmDatabase): WorkPermitCreateOfflineToolsDao = db.workPermitCreateOfflineToolsDao()

        @Provides
        fun providePendingActionDao(db: CatmDatabase): PendingActionDao = db.pendingActionDao()
    }
}
