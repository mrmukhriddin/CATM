package ru.metasharks.catm.feature.offline.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.offline.entities.OfflineSave

@Dao
interface OfflineSaveDao {

    @Query("SELECT EXISTS(SELECT * FROM offline_saves WHERE typeCode=:typeCode)")
    fun isTypeSaved(typeCode: String): Single<Boolean>

    @Query("SELECT * FROM offline_saves WHERE userId=:userId")
    fun offlineSaves(userId: Long): Observable<List<OfflineSave>>

    @Query("DELETE FROM offline_saves WHERE userId=:userId")
    fun deleteForUser(userId: Long): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(offlineSave: OfflineSave): Completable
}
