package ru.metasharks.catm.feature.offline.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.offline.pending.entities.PendingAction

@Dao
interface PendingActionDao {

    @Query("SELECT * FROM pending_actions WHERE userId=:userId")
    fun getPendingActionsForUser(userId: Long): Single<List<PendingAction>>

    @Query("DELETE FROM pending_actions WHERE userId=:userId")
    fun deleteForUser(userId: Long): Completable

    @Insert
    fun insert(pendingAction: PendingAction): Completable

    @Delete
    fun delete(pendingAction: PendingAction): Completable

    @Query("SELECT CASE WHEN EXISTS(SELECT 1 FROM pending_actions WHERE userId=:userId) THEN 1 ELSE 0 END")
    fun havePending(userId: Long): Single<Boolean>
}
