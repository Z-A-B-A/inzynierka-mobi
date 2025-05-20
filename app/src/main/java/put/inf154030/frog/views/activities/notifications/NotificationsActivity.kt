package put.inf154030.frog.views.activities.notifications

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import put.inf154030.frog.models.ContainerReference
import put.inf154030.frog.models.Notification
import put.inf154030.frog.models.ScheduleReference
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.views.fragments.BackButton
import put.inf154030.frog.views.fragments.NotificationSetting
import put.inf154030.frog.views.fragments.TopHeaderBar

class NotificationsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FrogTheme {
                NotificationsScreen(
                    onBackClick = { finish() },
                    notificationsList = emptyList()
                )
            }
        }
    }
}

@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit,
    notificationsList: List<Notification>
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
                NotificationSetting(
                    notificationName = "Anomalies",
                    isOn = true
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(notificationsList) { notification ->
                        NotificationSetting(
                            notificationName = notification.message,
                            isOn = false
                        )
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
                    1,
                    ScheduleReference(1, "Karmienie"),
                    ContainerReference(1, "Terrarium Gekona Lamparci")
                ),
                Notification(
                    1,
                    "Czyszczenie",
                    false,
                    "2025-05-20T18:01:54.46Z",
                    2,
                    ScheduleReference(2, "Czyszczenie"),
                    ContainerReference(1, "Terrarium Gekona Lamparci")
                )
            )
        )
    }
}