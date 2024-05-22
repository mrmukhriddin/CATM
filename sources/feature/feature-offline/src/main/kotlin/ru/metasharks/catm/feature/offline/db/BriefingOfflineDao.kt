package ru.metasharks.catm.feature.offline.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import ru.metasharks.catm.feature.offline.save.briefings.BriefingOfflineData

@Dao
interface BriefingOfflineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(briefingOfflineData: BriefingOfflineData): Completable

    @Query("SELECT * FROM briefing_offline_data WHERE userId=:userId LIMIT 1")
    fun getBriefingsForUser(userId: Long): Maybe<BriefingOfflineData>
}
