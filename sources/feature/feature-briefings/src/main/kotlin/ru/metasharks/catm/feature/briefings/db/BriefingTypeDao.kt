package ru.metasharks.catm.feature.briefings.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.briefings.entities.BriefingTypeX

@Dao
interface BriefingTypeDao {

    @Query("SELECT * FROM briefing_type")
    fun getBriefingTypes(): Single<List<BriefingTypeX>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTypes(types: List<BriefingTypeX>): Completable
}
