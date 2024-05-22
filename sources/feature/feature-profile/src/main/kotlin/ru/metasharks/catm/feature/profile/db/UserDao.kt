package ru.metasharks.catm.feature.profile.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.profile.db.entities.UserDb
import ru.metasharks.catm.feature.profile.entities.UserProfileX

@Dao
interface UserDao {

    @Query("SELECT * FROM user_db LIMIT 1")
    fun getUser(): Maybe<UserDb>

    @Query("SELECT * FROM user_db WHERE id=:id")
    fun getUserById(id: Int): Maybe<UserDb>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: UserDb): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userProfiles: List<UserProfileX>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userProfile: UserProfileX): Completable

    @Update
    fun update(user: UserDb): Single<Int>

    @Query("DELETE FROM user_db")
    fun deleteAll()

    @Query("SELECT * FROM user_profile WHERE id=:userId LIMIT 1")
    fun getUserProfile(userId: Int): Maybe<UserProfileX>

    @Query("SELECT * FROM user_profile")
    fun getUserProfiles(): Single<List<UserProfileX>>
}
