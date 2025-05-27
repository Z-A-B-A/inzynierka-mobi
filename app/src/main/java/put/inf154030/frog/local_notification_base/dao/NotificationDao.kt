package put.inf154030.frog.local_notification_base.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import put.inf154030.frog.local_notification_base.model.PendingNotification

@Dao
interface NotificationDao {
    @Query("SELECT * FROM pending_notifications WHERE executionTime <= :currentTime AND isRead = 0")
    suspend fun getReadyNotifications(currentTime: Long): List<PendingNotification>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<PendingNotification>)

    @Query("UPDATE pending_notifications SET isRead = 1 WHERE id = :notificationId")
    suspend fun markAsRead(notificationId: String)

    @Query("DELETE FROM pending_notifications WHERE isRead = 1")
    suspend fun deleteReadNotifications()
}