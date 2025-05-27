package put.inf154030.frog.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import put.inf154030.frog.R
import put.inf154030.frog.local_notification_base.AppDatabase
import put.inf154030.frog.local_notification_base.model.PendingNotification
import put.inf154030.frog.views.activities.notifications.NotificationsActivity

class NotificationService(
    private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val notificationDao = AppDatabase.getDatabase(context).notificationDao()
    private val scope = CoroutineScope(Dispatchers.Default)

    companion object {
        const val CHANNEL_ID = "frog_channel"
    }

    fun showNotification(title: String, message: String) {
        val activityIntent = Intent(context, NotificationsActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            1,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.frog)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(activityPendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    fun scheduleNotifications(notifications: List<PendingNotification>) {
        scope.launch {
            notificationDao.insertNotifications(notifications)
            processScheduledNotifications()
        }
    }

    private suspend fun processScheduledNotifications() {
        val currentTime = System.currentTimeMillis()
        val readyNotifications = notificationDao.getReadyNotifications(currentTime)

        // Show notifications that are ready to be displayed
        readyNotifications.forEach { notification ->
            showNotification(notification.title, notification.message)
            notificationDao.markAsRead(notification.id)
        }

        // Calculate time until next notification
        val allFutureNotifications = notificationDao.getReadyNotifications(Long.MAX_VALUE)
            .filter { !it.isRead && it.executionTime > currentTime }
            .sortedBy { it.executionTime }

        if (allFutureNotifications.isNotEmpty()) {
            val nextNotification = allFutureNotifications.first()
            val delayTime = nextNotification.executionTime - currentTime

            if (delayTime > 0) {
                delay(delayTime)
                processScheduledNotifications()
            }
        }
    }

    // Clean up old notifications
    suspend fun cleanupOldNotifications() {
        notificationDao.deleteReadNotifications()
    }
}