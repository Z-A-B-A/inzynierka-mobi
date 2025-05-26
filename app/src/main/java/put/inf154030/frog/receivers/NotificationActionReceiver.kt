package put.inf154030.frog.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.services.NotificationManager
import put.inf154030.frog.views.activities.notifications.NotificationsActivity

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == NotificationManager.ACTION_MARK_AS_READ) {
            val notificationId = intent.getIntExtra(
                NotificationManager.EXTRA_NOTIFICATION_ID, -1
            )

            if (notificationId != -1) {
                // Mark notification as read on server
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        ApiClient.apiService.markNotificationAsRead(notificationId)
                            .execute()

                        // Remove from local storage and cancel notification
                        val notificationManager = NotificationManager(context)
                        notificationManager.removeNotification(notificationId)

                        // Broadcast update to any open UI using explicit intent
                        val updateIntent = Intent(NotificationsActivity.ACTION_NOTIFICATION_UPDATED).apply {
                            setPackage(context.packageName)
                        }
                        context.sendBroadcast(updateIntent)
                    } catch (e: Exception) {
                        // Handle error - you might want to retry later
                    }
                }
            }
        }
    }
}