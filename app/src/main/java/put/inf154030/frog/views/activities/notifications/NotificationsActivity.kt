package put.inf154030.frog.views.activities.notifications

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import put.inf154030.frog.models.ContainerReference
import put.inf154030.frog.models.Notification
import put.inf154030.frog.models.ScheduleReference
import put.inf154030.frog.models.responses.NotificationMarkAllReadResponse
import put.inf154030.frog.models.responses.NotificationUpdateResponse
import put.inf154030.frog.models.responses.NotificationsResponse
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.services.FrogFirebaseMessagingService
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import put.inf154030.frog.utils.dataStore
import put.inf154030.frog.views.fragments.BackButton
import put.inf154030.frog.views.fragments.NotificationCard
import put.inf154030.frog.views.fragments.NotificationSetting
import put.inf154030.frog.views.fragments.TopHeaderBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Activity for displaying and managing notifications
class NotificationsActivity : ComponentActivity() {
    // State for notifications, loading, error, and toggle
    private var notificationsList by mutableStateOf<List<Notification>>(emptyList())
    private var isLoading by mutableStateOf(false)
    private var errorMessage by mutableStateOf<String?>(null)
    private var notificationsEnabled by mutableStateOf(true)

    // Modern permission request launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        notificationsEnabled = isGranted
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load notification toggle state from preferences
        lifecycleScope.launch {
            notificationsEnabled = applicationContext.dataStore.data.first()[FrogFirebaseMessagingService.NOTIFICATIONS_ENABLED_KEY] ?:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                    } else {
                        true
                    }

            // Update UI
            setContent {
                FrogTheme {
                    NotificationsScreen(
                        notificationsEnabled = notificationsEnabled,
                        isLoading = isLoading,
                        errorMessage = errorMessage,
                        onBackClick = { finish() },
                        notificationsList = notificationsList,
                        onNotificationsToggle = { enabled ->
                            toggleNotifications(enabled)
                        },
                        onMarkAllAsReadClick = { markAllAsRead() },
                        onMarkAsReadClick = { id -> markAsRead(id) }
                    )
                }
            }
            // Load notifications from API
            loadNotifications()
        }
    }

    // New function to properly handle toggle
    private fun toggleNotifications(enabled: Boolean) {
        notificationsEnabled = enabled
        updateNotificationsEnabledPreference(enabled)

        // Request permission if enabling on Android 13+
        if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else if (!enabled) {
            Toast.makeText(
                this,
                "Notifications disabled for this app",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Save notification toggle state to preferences
    private fun updateNotificationsEnabledPreference(enabled: Boolean) = lifecycleScope.launch {
        applicationContext.dataStore.updateData { preferences ->
            val mutablePreferences = preferences.toMutablePreferences()
            mutablePreferences[FrogFirebaseMessagingService.NOTIFICATIONS_ENABLED_KEY] = enabled
            mutablePreferences
        }
    }

    // Load notifications from API
    private fun loadNotifications() {
        isLoading = true
        errorMessage = null

        ApiClient.apiService.getNotifications(unreadOnly = true)
            .enqueue(object : Callback<NotificationsResponse> {
                override fun onResponse(
                    call: Call<NotificationsResponse>,
                    response: Response<NotificationsResponse>
                ) {
                    isLoading = false
                    if (response.isSuccessful) {
                        notificationsList = response.body()?.notifications ?: emptyList()
                    } else {
                        errorMessage = "Failed to load notifications"
                    }
                }

                override fun onFailure(call: Call<NotificationsResponse>, t: Throwable) {
                    isLoading = false
                    errorMessage = t.message ?: "Network error"
                }
            })
    }

    // Mark a single notification as read
    private fun markAsRead(notificationId: Int) {
        errorMessage = null
        ApiClient.apiService.markNotificationAsRead(notificationId)
            .enqueue(object : Callback<NotificationUpdateResponse> {
                override fun onResponse(
                    call: Call<NotificationUpdateResponse>,
                    response: Response<NotificationUpdateResponse>
                ) {
                    if (response.isSuccessful) {
                        loadNotifications()
                    } else {
                        errorMessage = "Failed to mark notification as read"
                    }
                }

                override fun onFailure(call: Call<NotificationUpdateResponse>, t: Throwable) {
                    errorMessage = t.message ?: "Network error"
                }
            })
    }

    // Mark all notifications as read
    private fun markAllAsRead() {
        errorMessage = null
        ApiClient.apiService.markAllNotificationsAsRead()
            .enqueue(object : Callback<NotificationMarkAllReadResponse> {
                override fun onResponse(
                    call: Call<NotificationMarkAllReadResponse>,
                    response: Response<NotificationMarkAllReadResponse>
                ) {
                    if (response.isSuccessful) {
                        loadNotifications()
                    } else {
                        errorMessage = "Failed to mark all notifications as read"
                    }
                }

                override fun onFailure(call: Call<NotificationMarkAllReadResponse>, t: Throwable) {
                    errorMessage = t.message ?: "Network error"
                }
            })
    }
}

// Composable for notifications UI
@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit,
    notificationsList: List<Notification>,
    notificationsEnabled: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onNotificationsToggle: (Boolean) -> Unit,
    onMarkAllAsReadClick: () -> Unit,
    onMarkAsReadClick: (Int) -> Unit
) {
    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            TopHeaderBar( title = "Notifications" )
            BackButton { onBackClick() }
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Toggle for enabling/disabling notifications
                NotificationSetting(
                    isOn = notificationsEnabled,
                    onToggle = { onNotificationsToggle(it) }
                )
                Spacer(modifier = Modifier.size(16.dp))
                // "Mark all as read" action
                Text(
                    text = "mark all as read",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = TextDecoration.Underline,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isLoading) { onMarkAllAsReadClick() }
                )
                // Show error message if present
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                // Show loading spinner if loading
                if (isLoading) {
                    Spacer(modifier = Modifier.size(32.dp))
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.secondary
                    )
                } else {
                    // List of notifications
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        items(notificationsList) { notification ->
                            NotificationCard(
                                eventName = notification.schedule?.name ?: "Notification",
                                containerName = notification.container?.name ?: "Unknown Container",
                                executionTime = notification.executionTime,
                                onMarkAsReadClick = { onMarkAsReadClick(notification.id) }
                            )
                        }
                        // Empty state message
                        if (notificationsList.isEmpty()) {
                            item {
                                Text(
                                    text = "No new notifications",
                                    fontFamily = PoppinsFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 32.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Preview for Compose UI
@Preview
@Composable
fun NotificationsActivityPreview() {
    FrogTheme {
        NotificationsScreen(
            onBackClick = {},
            notificationsList = listOf(
                Notification(
                    1,
                    "Karmienie",
                    false,
                    "2025-05-20T18:01:54.46Z",
                    "2025-05-20T18:00:00.00Z",
                    1,
                    ScheduleReference(1, "Karmienie"),
                    ContainerReference(1, "Terrarium Gekona Lamparci")
                ),
                Notification(
                    1,
                    "Czyszczenie",
                    false,
                    "2025-05-20T18:01:54.46Z",
                    "2025-05-20T18:00:00.00Z",
                    2,
                    ScheduleReference(2, "Czyszczenie"),
                    ContainerReference(1, "Terrarium Gekona Lamparci")
                )
            ),
            onMarkAllAsReadClick = {},
            notificationsEnabled = true,
            onNotificationsToggle = { _ -> },
            onMarkAsReadClick = { _ -> },
            isLoading = false,
            errorMessage = null
        )
    }
}