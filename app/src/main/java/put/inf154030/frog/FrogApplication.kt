package put.inf154030.frog

import put.inf154030.frog.network.SessionManager
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.work.BackoffPolicy
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import put.inf154030.frog.local_notification_base.AppDatabase
import put.inf154030.frog.services.NotificationService
import put.inf154030.frog.workers.NotificationWorker
import java.time.Duration
import java.util.concurrent.TimeUnit

class FrogApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SessionManager.init(applicationContext)

        // Initialize database
        AppDatabase.getDatabase(applicationContext)

        // Create notification channel
        createNotificationChannel()

        // Schedule periodic worker for checking notifications
        scheduleNotificationWorker()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NotificationService.CHANNEL_ID,
            "frog_channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun scheduleNotificationWorker() {
        // Constraints to run the worker
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Create a periodic work request that runs every 15 minutes
        val periodicWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            5, TimeUnit.MINUTES,  // Repeat interval
            30, TimeUnit.SECONDS    // Flex interval
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                Duration.ofMinutes(1)
            )
            .build()

        // Schedule the work
        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork(
                "notification_work",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWorkRequest
            )

        // For immediate tasks like showing already saved notifications when app starts
        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>().build()
        WorkManager.getInstance(applicationContext).enqueue(oneTimeWorkRequest)
    }
}