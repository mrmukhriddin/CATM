package ru.metasharks.catm.feature.notifications.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import ru.metasharks.catm.feature.notifications.db.entities.NotificationDb

@Dao
interface NotificationsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notification: NotificationDb): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notifications: List<NotificationDb>): Single<List<Long>>

    @Query("SELECT * FROM notifications WHERE userId=:userId ORDER BY timestamp DESC")
    fun getNotificationsForUser(userId: Long): Observable<List<NotificationDb>>

    @Query("SELECT * FROM notifications WHERE userId=:userId ORDER BY timestamp DESC")
    fun getNotificationsForUserSingle(userId: Long): Single<List<NotificationDb>>

    @Query("DELETE FROM notifications")
    fun nukeTable(): Completable

    @Query("DELETE FROM notifications WHERE notificationId=:notificationId")
    fun delete(notificationId: Long): Completable

    @Query("SELECT CASE WHEN EXISTS(SELECT 1 FROM notifications WHERE userId=:userId) THEN 0 ELSE 1 END")
    fun isEmptyTableForUser(userId: Long): Single<Boolean>
}
