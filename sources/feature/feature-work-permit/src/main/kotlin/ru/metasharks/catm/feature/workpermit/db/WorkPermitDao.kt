package ru.metasharks.catm.feature.workpermit.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.workpermit.db.entities.WorkPermitDb
import ru.metasharks.catm.feature.workpermit.db.entities.WorkPermitMiniDb
import ru.metasharks.catm.feature.workpermit.entities.statuses.StatusesEnvelopeX

@Dao
interface WorkPermitDao {

    @Query("SELECT * FROM work_permits_details WHERE id=:id")
    fun getWorkPermitById(id: Long): Maybe<WorkPermitDb>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: WorkPermitDb): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: List<WorkPermitMiniDb>): Completable

    @Query("SELECT * FROM work_permits_mini WHERE userId=:userId")
    fun getWorkPermitsMini(userId: Long): Single<List<WorkPermitMiniDb>>

    @Query("SELECT * FROM statuses_envelope")
    fun getStatuses(): Single<StatusesEnvelopeX>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(statuses: StatusesEnvelopeX): Completable
}
