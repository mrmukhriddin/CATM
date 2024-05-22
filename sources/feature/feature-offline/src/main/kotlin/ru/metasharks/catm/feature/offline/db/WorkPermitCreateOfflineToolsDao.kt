package ru.metasharks.catm.feature.offline.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import ru.metasharks.catm.feature.offline.save.workpermit.WorkPermitCreateOfflineTools

@Dao
interface WorkPermitCreateOfflineToolsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(briefingOfflineData: WorkPermitCreateOfflineTools): Completable

    @Query("SELECT * FROM create_work_permit_offline_tools WHERE userId=:userId LIMIT 1")
    fun getTools(userId: Long): Maybe<WorkPermitCreateOfflineTools>
}
