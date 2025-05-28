package put.inf154030.frog.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import put.inf154030.frog.R
import put.inf154030.frog.utils.dataStore
import put.inf154030.frog.views.activities.login_pages.LogInActivity

class FrogFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        val FCM_TOKEN_KEY = stringPreferencesKey("fcm_token")
        val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("notifications_enabled")
        private const val CHANNEL_ID = "frog_notifications"
        private const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")

        storeTokenLocally(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "From: ${remoteMessage.from}")

        // Check if notifications are enabled
        val notificationsEnabled = runBlocking {
            applicationContext.dataStore.data.first()[NOTIFICATIONS_ENABLED_KEY] ?: true
        }

        if (!notificationsEnabled) {
            Log.d("FCM", "Notifications are disabled, ignoring message")
            return
        }

        // Log the data payload
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("FCM", "Message data payload: ${remoteMessage.data}")
        }

        // Show notification
        val title = remoteMessage.notification?.title ?: "Frog App"
        val body = remoteMessage.notification?.body ?: "New notification"
        showNotification(title, body)
    }
//    TODO("Zrobić tak, żeby otwierała się notifications activity")
    private fun showNotification(title: String, message: String) {
        val intent = Intent(this, LogInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntentFlags =
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, pendingIntentFlags
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.frog) // Replace with your app icon
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        val name = "Frog Notifications"
        val descriptionText = "Notifications for the Frog app"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun storeTokenLocally(token: String) = runBlocking {
        applicationContext.dataStore.updateData { preferences ->
            val mutablePreferences = preferences.toMutablePreferences()
            mutablePreferences[FCM_TOKEN_KEY] = token
            mutablePreferences
        }
    }
}