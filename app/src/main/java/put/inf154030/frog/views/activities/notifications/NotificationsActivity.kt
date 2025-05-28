package put.inf154030.frog.views.activities.notifications

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.remember
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

class NotificationsActivity : ComponentActivity() {
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

        checkNotificationPermission()
        loadNotifications()

        setContent {
            FrogTheme {
                NotificationsScreen(
                    onBackClick = { finish() },
                    notificationsList = notificationsList,
                    notificationsEnabled = notificationsEnabled,
                    onNotificationsToggle = { enabled ->
                        notificationsEnabled = enabled
                        if (enabled) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        } else {
                            // When toggling off, just update preference - can't revoke permission programmatically
                            updateNotificationsEnabledPreference(false)

                            // Optionally open app settings if you want user to manually revoke permission
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
//                                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
//                                PackageManager.PERMISSION_GRANTED) {
//
//                                // Optionally open settings
//                                 val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                                 val uri = Uri.fromParts("package", packageName, null)
//                                 intent.data = uri
//                                 startActivity(intent)
//                            }
                        }
                    },
                    onMarkAllAsReadClick = {
                        markAllAsRead()
                    },
                    onMarkAsReadClick = { id ->
                        markAsRead(id)
                    }
                )
            }
        }
    }

    private fun checkNotificationPermission() {
        // On Android 13+, check notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationsEnabled = checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
            updateNotificationsEnabledPreference(notificationsEnabled)
        }
    }

    private fun updateNotificationsEnabledPreference(enabled: Boolean) = lifecycleScope.launch {
        applicationContext.dataStore.updateData { preferences ->
            val mutablePreferences = preferences.toMutablePreferences()
            mutablePreferences[FrogFirebaseMessagingService.NOTIFICATIONS_ENABLED_KEY] = enabled
            mutablePreferences
        }
    }

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

    private fun markAsRead(notificationId: Int) {
        ApiClient.apiService.markNotificationAsRead(notificationId)
            .enqueue(object : Callback<NotificationUpdateResponse> {
                override fun onResponse(
                    call: Call<NotificationUpdateResponse>,
                    response: Response<NotificationUpdateResponse>
                ) {
                    if (response.isSuccessful) {
                        loadNotifications()
                    } else {
                        // Error handling...
                    }
                }

                override fun onFailure(call: Call<NotificationUpdateResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }
                // onFailure implementation...
            })
    }

    private fun markAllAsRead() {
        ApiClient.apiService.markAllNotificationsAsRead()
            .enqueue(object : Callback<NotificationMarkAllReadResponse> {
                override fun onResponse(
                    call: Call<NotificationMarkAllReadResponse>,
                    response: Response<NotificationMarkAllReadResponse>
                ) {
                    if (response.isSuccessful) {
                        loadNotifications()
                    } else {
                        // Error handling...
                    }
                }

                override fun onFailure(call: Call<NotificationMarkAllReadResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }
                // onFailure implementation...
            })
    }
}

@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit,
    notificationsList: List<Notification>,
    notificationsEnabled: Boolean,
    onNotificationsToggle: (Boolean) -> Unit,
    onMarkAllAsReadClick: () -> Unit,
    onMarkAsReadClick: (Int) -> Unit
) {
    var isEnabled by remember { mutableStateOf(notificationsEnabled) }

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
                NotificationSetting(
                    isOn = isEnabled,
                    onToggle = {
                        isEnabled = it
                        onNotificationsToggle(it)
                    }
                )
                Spacer(modifier = Modifier.size(16.dp))
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
                        .clickable { onMarkAllAsReadClick() }
                )
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

                    if (notificationsList.isEmpty()) {
                        item {
                            Text(
                                text = "No notifications",
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
            onMarkAsReadClick = { _ -> }
        )
    }
}