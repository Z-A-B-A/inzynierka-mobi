package put.inf154030.frog.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.services.NotificationService

class NotificationWorker (
    private val appContext: Context,
    private val params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = ApiClient.apiService.getNotifications(unreadOnly = true).execute()
            if (response.isSuccessful) {
                val notifications = response.body()?.notifications
                // Check for notifications and show them if needed
                if (!notifications.isNullOrEmpty()) {
                    // Example: show notification for the first one
                    NotificationService(appContext).showNotification()
                }
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e("NotificationWorker", "Error: ${e.message}")
            Result.retry()
        }
    }
}