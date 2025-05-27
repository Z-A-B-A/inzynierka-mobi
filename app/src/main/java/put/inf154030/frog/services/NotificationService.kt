package put.inf154030.frog.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import put.inf154030.frog.R
import put.inf154030.frog.views.activities.notifications.NotificationsActivity

class NotificationService(
    private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_ID = "frog_channel"
    }

    fun showNotification() {
        val activityIntent = Intent(context, NotificationsActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            1,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val testNotification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.frog)
            .setContentTitle("Test")
            .setContentText("Lol dziaua")
            .setContentIntent(activityPendingIntent)
            .build()

        notificationManager.notify(1, testNotification)
    }
}