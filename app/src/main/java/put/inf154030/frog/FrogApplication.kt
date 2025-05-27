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
import put.inf154030.frog.services.NotificationService
import put.inf154030.frog.workers.NotificationWorker
import java.time.Duration
import java.util.concurrent.TimeUnit

class FrogApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SessionManager.init(applicationContext)

        createNotificationChannel()

        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            repeatInterval = 5,
            repeatIntervalTimeUnit = TimeUnit.MINUTES,
            flexTimeInterval = 1,
            flexTimeIntervalUnit = TimeUnit.MINUTES
        ).setBackoffCriteria(
            backoffPolicy = BackoffPolicy.LINEAR,
            duration = Duration.ofSeconds(15)
        ).build()
        val workManager = WorkManager.getInstance(applicationContext)
        workManager.enqueue(workRequest)
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
}