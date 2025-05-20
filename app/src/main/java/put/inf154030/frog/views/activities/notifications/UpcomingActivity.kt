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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import put.inf154030.frog.models.ContainerReference
import put.inf154030.frog.models.UpcomingEvent
import put.inf154030.frog.models.responses.UpcomingEventsResponse
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.views.fragments.BackButton
import put.inf154030.frog.views.fragments.TopHeaderBar
import put.inf154030.frog.views.fragments.UpcomingCard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpcomingActivity : ComponentActivity() {
    private var upcomingList by mutableStateOf<List<UpcomingEvent>>(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FrogTheme {
                UpcomingScreen(
                    onBackClick = { finish() },
                    upcomingList = upcomingList
                )
            }
        }
        loadUpcomingEvents()
    }

    private fun loadUpcomingEvents() {
        ApiClient.apiService.getUpcomingNotifications(1)
            .enqueue(object : Callback<UpcomingEventsResponse> {
                override fun onResponse(
                    call: Call<UpcomingEventsResponse>,
                    response: Response<UpcomingEventsResponse>
                ) {
                    if (response.isSuccessful) {
                        upcomingList = response.body()?.upcomingEvents ?: emptyList()
                    }
                }

                override fun onFailure(call: Call<UpcomingEventsResponse>, t: Throwable) {
                    TODO()
                }
            })
    }
}

@Composable
fun UpcomingScreen (
    onBackClick: () -> Unit,
    upcomingList: List<UpcomingEvent>
) {
    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            TopHeaderBar(title = "Upcoming")
            BackButton { onBackClick() }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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

@Preview
@Composable
fun UpcomingActivityPreview () {
    FrogTheme {
        UpcomingScreen(
            onBackClick = {},
            upcomingList = listOf(
                UpcomingEvent(1, 1, ContainerReference(1, "Terrarium Gekona Lamparci"), "Karmienie", "2025-05-23T18:00:00Z"),
                UpcomingEvent(2, 1, ContainerReference(1, "Terrarium Gekona Lamparci"), "Czyszczenie", "2025-05-24T16:30:00Z")
            )
        )
    }
}