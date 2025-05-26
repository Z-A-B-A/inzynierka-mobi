package put.inf154030.frog.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.services.NotificationManager
import java.time.Instant

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
): CoroutineWorker(context, workerParams) {
    private val notificationManager = NotificationManager(applicationContext)

    override suspend fun doWork(): Result {
        try {
            // Fetch unread notifications
            val response = ApiClient.apiService.getNotifications(unreadOnly = true).execute()

            if (response.isSuccessful && response.body() != null) {
                val notifications = response.body()!!.notifications
                val now = Instant.now()

                for (notification in notifications) {
                    // Store all notifications
                    notificationManager.storeNotification(notification)

                    // Check if notification should be shown now
                    try {
                        val executionTime = Instant.parse(notification.executionTime)

                        // If execution time is now or in the past, show notification
                        if (!executionTime.isAfter(now) && !notification.read) {
                            notificationManager.showNotification(notification)
                        }
                    } catch (e: Exception) {
                        // If time parsing fails, show notification anyway
                        if (!notification.read) {
                            notificationManager.showNotification(notification)
                        }
                    }
                }
                return Result.success()
            }
            return Result.retry()
        } catch (e: Exception) {
            return Result.retry()
        }
    }
}