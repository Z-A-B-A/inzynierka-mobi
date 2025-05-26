package put.inf154030.frog

import put.inf154030.frog.network.SessionManager
import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import put.inf154030.frog.services.NotificationManager
import put.inf154030.frog.workers.NotificationWorker
import java.util.concurrent.TimeUnit

class FrogApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SessionManager.init(applicationContext)

        // Create notification channel
        val notificationManager = NotificationManager(this)
        notificationManager.createNotificationChannel()

        // Schedule periodic work to fetch notifications
        scheduleNotificationWorker()
    }

    private fun scheduleNotificationWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            5, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "notification_checker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}