package put.inf154030.frog.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import put.inf154030.frog.local_notification_base.AppDatabase
import put.inf154030.frog.local_notification_base.model.PendingNotification
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.services.NotificationService
import java.util.UUID

class NotificationWorker (
    private val appContext: Context,
    private val params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val notificationDao by lazy {
        AppDatabase.getDatabase(appContext).notificationDao()
    }

    private val notificationService by lazy {
        NotificationService(appContext)
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // 1. Fetch notifications from API
            val fetchResult = fetchNotifications()

            // 2. Process any notifications that are ready to be shown
            processReadyNotifications()

            // 3. Clean up old notifications
            notificationDao.deleteReadNotifications()

            return@withContext fetchResult
        } catch (e: Exception) {
            Log.e("NotificationWorker", "Error in worker: ${e.message}")
            return@withContext Result.retry()
        }
    }

    private suspend fun fetchNotifications(): Result {
        return try {
            val response = ApiClient.apiService.getNotifications(unreadOnly = true).execute()

            if (response.isSuccessful) {
                val apiNotifications = response.body()?.notifications

                if (!apiNotifications.isNullOrEmpty()) {
                    Log.d("NotificationWorker", "Fetched ${apiNotifications.size} notifications")

                    val pendingNotifications = apiNotifications.map { apiNotification ->
                        PendingNotification(
                            id = (apiNotification.id ?: UUID.randomUUID()).toString(),
                            title = apiNotification.schedule?.name ?: "New Notification",
                            message = apiNotification.message ?: "",
                            executionTime = apiNotification.executionTime.toLong() ?: System.currentTimeMillis(),
                            isRead = false
                        )
                    }

                    // Store in database
                    notificationDao.insertNotifications(pendingNotifications)
                }

                Result.success()
            } else {
                Log.e("NotificationWorker", "API error: ${response.code()}")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e("NotificationWorker", "Error fetching notifications: ${e.message}")
            Result.retry()
        }
    }

    private suspend fun processReadyNotifications() {
        val currentTime = System.currentTimeMillis()
        val readyNotifications = notificationDao.getReadyNotifications(currentTime)

        if (readyNotifications.isNotEmpty()) {
            Log.d("NotificationWorker", "Processing ${readyNotifications.size} notifications")

            readyNotifications.forEach { notification ->
                notificationService.showNotification(notification.title, notification.message)
                notificationDao.markAsRead(notification.id)
            }
        }
    }
}