package put.inf154030.frog.local_notification_base.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_notifications")
data class PendingNotification(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val executionTime: Long,
    val isRead: Boolean = false
)
