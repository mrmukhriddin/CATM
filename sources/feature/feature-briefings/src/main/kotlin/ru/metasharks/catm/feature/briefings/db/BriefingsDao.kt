package ru.metasharks.catm.feature.briefings.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.briefings.entities.BriefingX

@Dao
interface BriefingsDao {

    @Query("SELECT * FROM briefing")
    fun getBriefings(): Single<List<BriefingX>>

    @Query("SELECT * FROM briefing WHERE id=:id")
    fun getBriefingById(id: Int): Maybe<BriefingX>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllBriefings(briefings: List<BriefingX>): Completable
}
