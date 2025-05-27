package put.inf154030.frog.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import put.inf154030.frog.local_notification_base.AppDatabase
import put.inf154030.frog.local_notification_base.model.PendingNotification
import put.inf154030.frog.network.ApiClient
import java.util.UUID
import java.util.concurrent.TimeUnit

class NotificationBackgroundService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.Default)
    private var fetchJob: Job? = null
    private var processJob: Job? = null

    private val notificationDao by lazy {
        AppDatabase.getDatabase(applicationContext).notificationDao()
    }

    private val notificationService by lazy {
        NotificationService(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startFetchingNotifications()
        startProcessingNotifications()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startFetchingNotifications() {
        fetchJob = serviceScope.launch {
            while (isActive) {
                try {
                    Log.d("NotificationService", "Fetching notifications")
                    val response = ApiClient.apiService.getNotifications(unreadOnly = true).execute()

                    if (response.isSuccessful) {
                        val apiNotifications = response.body()?.notifications

                        if (!apiNotifications.isNullOrEmpty()) {
                            Log.d("NotificationService", "Received ${apiNotifications.size} notifications")
                            val pendingNotifications = apiNotifications.map { apiNotification ->
                                PendingNotification(
                                    id = (apiNotification.id ?: UUID.randomUUID()).toString(),
                                    title = apiNotification.schedule?.name ?: "New Notification",
                                    message = apiNotification.message ?: "",
                                    executionTime = apiNotification.executionTime.toLong() ?: System.currentTimeMillis(),
                                    isRead = false
                                )
                            }

                            notificationDao.insertNotifications(pendingNotifications)
                        } else {
                            Log.d("NotificationService", "No new notifications")
                        }

                        // Clean up old notifications
                        notificationDao.deleteReadNotifications()
                    } else {
                        Log.e("NotificationService", "API error: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e("NotificationService", "Error fetching notifications: ${e.message}")
                }
            }
        }
    }

    private fun startProcessingNotifications() {
        processJob = serviceScope.launch {
            while (isActive) {
                try {
                    val currentTime = System.currentTimeMillis()
                    val readyNotifications = notificationDao.getReadyNotifications(currentTime)

                    if (readyNotifications.isNotEmpty()) {
                        Log.d("NotificationService", "Processing ${readyNotifications.size} ready notifications")

                        readyNotifications.forEach { notification ->
                            notificationService.showNotification(notification.title, notification.message)
                            notificationDao.markAsRead(notification.id)
                        }
                    }

                    // Find the next notification to be shown
                    val futureNotifications = notificationDao.getReadyNotifications(Long.MAX_VALUE)
                        .filter { !it.isRead && it.executionTime > currentTime }
                        .sortedBy { it.executionTime }

                    if (futureNotifications.isNotEmpty()) {
                        val nextNotification = futureNotifications.first()
                        val waitTime = nextNotification.executionTime - currentTime

                        // If next notification is soon, wait for it; otherwise check every minute
                        val delayTime = minOf(waitTime, TimeUnit.MINUTES.toMillis(1))
                        delay(delayTime)
                    } else {
                        // If no scheduled notifications, check every minute
                        delay(TimeUnit.MINUTES.toMillis(1))
                    }
                } catch (e: Exception) {
                    Log.e("NotificationService", "Error processing notifications: ${e.message}")
                    delay(TimeUnit.MINUTES.toMillis(1))
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fetchJob?.cancel()
        processJob?.cancel()
    }
}