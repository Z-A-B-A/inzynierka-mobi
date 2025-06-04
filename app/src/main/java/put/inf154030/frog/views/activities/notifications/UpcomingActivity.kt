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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.models.ContainerReference
import put.inf154030.frog.models.UpcomingEvent
import put.inf154030.frog.repository.NotificationsRepository
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.views.fragments.BackButton
import put.inf154030.frog.views.fragments.TopHeaderBar
import put.inf154030.frog.views.fragments.UpcomingCard

// Activity for displaying upcoming events
class UpcomingActivity : ComponentActivity() {
    // State for upcoming events, loading, and error
    private var upcomingList by mutableStateOf<List<UpcomingEvent>>(emptyList())
    private var isLoading by mutableStateOf(true)
    private var errorMessage by mutableStateOf<String?>(null)
    private val notificationsRepository = NotificationsRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FrogTheme {
                // Main screen composable for upcoming events
                UpcomingScreen(
                    onBackClick = { finish() },
                    upcomingList = upcomingList,
                    isLoading = isLoading,
                    errorMessage = errorMessage
                )
            }
        }
        // Load upcoming events from API
        loadUpcomingEvents()
    }

    // Fetch upcoming events from API
    private fun loadUpcomingEvents() {
//        isLoading = true
//        errorMessage = null
//
//        TODO("czekam na ogarnięcie requesta")
    }
}

// Composable for the upcoming events screen UI
@Composable
fun UpcomingScreen (
    onBackClick: () -> Unit,
    upcomingList: List<UpcomingEvent>,
    isLoading: Boolean,
    errorMessage: String?
) {
    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            TopHeaderBar(title = "Nadchodzące")
            BackButton { onBackClick() }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Show error message if present
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                // Show loading spinner if loading
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(top = 48.dp)
                            .align(Alignment.CenterHorizontally)
                            .testTag("CircularProgressIndicator"),
                        color = MaterialTheme.colorScheme.secondary
                    )
                } else {
                    // Show empty state if no events
                    if (upcomingList.isEmpty()) {
                        Text(
                            text = "Brak nadchodzących wydarzeń 🎉",
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 48.dp),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        // List of upcoming events
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            items(upcomingList) { upcoming ->
                                UpcomingCard(
                                    containerName = upcoming.container.name,
                                    eventName = upcoming.eventName,
                                    scheduledFor = upcoming.scheduledFor
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
fun UpcomingActivityPreview () {
    FrogTheme {
        UpcomingScreen(
            onBackClick = {},
            upcomingList = listOf(
                UpcomingEvent(1, 1, ContainerReference(1, "Terrarium Gekona Lamparci"), "Karmienie", "2025-05-23T18:00:00Z"),
                UpcomingEvent(2, 1, ContainerReference(1, "Terrarium Gekona Lamparci"), "Czyszczenie", "2025-05-24T16:30:00Z")
            ),
            isLoading = false,
            errorMessage = null
        )
    }
}