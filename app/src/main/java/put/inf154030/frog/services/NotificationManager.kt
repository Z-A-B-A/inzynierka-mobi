package put.inf154030.frog.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.preference.PreferenceManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import put.inf154030.frog.R
import put.inf154030.frog.models.Notification
import put.inf154030.frog.receivers.NotificationActionReceiver
import put.inf154030.frog.views.activities.notifications.NotificationsActivity
import java.time.Instant

class NotificationManager (
    private val context: Context
) {
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val notificationManagerCompat = NotificationManagerCompat.from(context)

    companion object {
        const val CHANNEL_ID = "frog_notifications"
        const val NOTIFICATIONS_ENABLED_KEY = "notifications_enabled"
        const val FIRST_RUN_KEY = "first_run"
        const val ACTION_MARK_AS_READ = "put.inf154030.frog.ACTION_MARK_AS_READ"
        const val EXTRA_NOTIFICATION_ID = "notification_id"
    }

    fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Frog Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Animal care notifications"
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun areNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(NOTIFICATIONS_ENABLED_KEY, true)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(NOTIFICATIONS_ENABLED_KEY, enabled).apply()
    }

    fun isFirstRun(): Boolean {
        return sharedPreferences.getBoolean(FIRST_RUN_KEY, true)
    }

    fun setFirstRunCompleted() {
        sharedPreferences.edit().putBoolean(FIRST_RUN_KEY, false).apply()
    }

    fun showNotification(notification: Notification) {
        if (!areNotificationsEnabled()) {
            return
        }

        // Create intent to open NotificationsActivity when notification is tapped
        val intent = Intent(context, NotificationsActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Create mark-as-read action
        val actionIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_MARK_AS_READ
            putExtra(EXTRA_NOTIFICATION_ID, notification.id)
        }
        val actionPendingIntent = PendingIntent.getBroadcast(
            context,
            notification.id,
            actionIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.frog)
            .setContentTitle(notification.container?.name ?: "Frog")
            .setContentText(notification.message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(R.drawable.frog, "Mark as read", actionPendingIntent)

        // Show the notification
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManagerCompat.notify(notification.id, builder.build())
        }
    }

    fun storeNotification(notification: Notification) {
        val notifications = getStoredNotifications().toMutableList()

        // Add if not already present
        if (notifications.none { it.id == notification.id }) {
            notifications.add(notification)
            val json = android.util.JsonWriter(java.io.StringWriter()).apply {
                beginArray()
                for (n in notifications) {
                    // Simplified serialization - in a real app use Gson/Moshi
                    beginObject()
                    name("id").value(n.id)
                    name("message").value(n.message)
                    name("read").value(n.read)
                    name("created_at").value(n.createdAt)
                    name("execution_time").value(n.executionTime)
                    // We'll skip complex objects for simplicity
                    endObject()
                }
                endArray()
                close()
            }.toString()

            sharedPreferences.edit().putString("stored_notifications", json).apply()
        }
    }

    fun getStoredNotifications(): List<Notification> {
        // This is a simplified implementation - in a real app, use Gson or Room database
        return emptyList()
    }

    fun removeNotification(id: Int) {
        val notifications = getStoredNotifications().filterNot { it.id == id }
        // Simplified - real implementation would use proper serialization
        sharedPreferences.edit().putString("stored_notifications", "[]").apply()
        // Cancel any displayed notification
        notificationManagerCompat.cancel(id)
    }

    fun removeAllNotifications() {
        sharedPreferences.edit().putString("stored_notifications", "[]").apply()
        notificationManagerCompat.cancelAll()
    }

    fun shouldShowNotification(notification: Notification): Boolean {
        if (notification.read) return false

        try {
            val executionTime = Instant.parse(notification.executionTime)
            val now = Instant.now()
            return !executionTime.isAfter(now)  // Show if execution time is now or in the past
        } catch (e: Exception) {
            // If we can't parse the time, show it anyway
            return true
        }
    }
}