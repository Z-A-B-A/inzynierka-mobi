package put.inf154030.frog.views.activities.schedule

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.views.fragments.BackButton
import put.inf154030.frog.views.fragments.ScheduleItem
import put.inf154030.frog.views.fragments.TopHeaderBar
import put.inf154030.frog.models.Schedule
import put.inf154030.frog.models.responses.SchedulesResponse
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Activity for displaying and managing schedules
class ScheduleActivity : ComponentActivity() {
    // State for schedules, loading, and error message
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var schedulesList by mutableStateOf<List<Schedule>>(emptyList())
    private var isLoading by mutableStateOf(false)
    private var errorMessage by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val containerId = intent.getIntExtra("CONTAINER_ID", -1)

        // Register for result to reload schedules after create/edit
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            loadSchedules(containerId)
        }

        setContent {
            FrogTheme {
                // Main screen composable for schedules
                ScheduleScreen(
                    onBackClick = { finish() },
                    onCreateScheduleClick = {
                        val intent = Intent(this, CreateScheduleActivity::class.java)
                        intent.putExtra("CONTAINER_ID", containerId)
                        activityResultLauncher.launch(intent)
                    },
                    onEditScheduleClick = { schedule ->
                        val intent = Intent(this, EditScheduleActivity::class.java)
                        intent.putExtra("SCHEDULE_ID", schedule.id)
                        intent.putExtra("SCHEDULE_EXEC_TIME", schedule.executionTime)
                        activityResultLauncher.launch(intent)
                    },
                    schedulesList = schedulesList,
                    isLoading = isLoading,
                    errorMessage = errorMessage
                )
            }
        }
        // Initial load of schedules
        loadSchedules(containerId)
    }

    override fun onResume() {
        super.onResume()
        val containerId = intent.getIntExtra("CONTAINER_ID", -1)
        if (containerId != -1) {
            loadSchedules(containerId)
        }
    }

    // Fetch schedules from API
    private fun loadSchedules(
        containerId: Int
    ) {
        isLoading = true
        errorMessage = null

        ApiClient.apiService.getSchedules(containerId).enqueue(object:
            Callback<SchedulesResponse> {
            override fun onResponse(
                call: Call<SchedulesResponse>,
                response: Response<SchedulesResponse>
            ) {
                isLoading = false
                if (response.isSuccessful) {
                    // Update list with fetched schedules
                    schedulesList = response.body()?.schedules ?: emptyList()
                } else {
                    errorMessage = "Failed to load schedules: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<SchedulesResponse>, t: Throwable) {
                isLoading = false
                errorMessage = "Network error: ${t.message}"
            }
        })
    }
}

// Composable for the schedules screen UI
@Composable
fun ScheduleScreen(
    onBackClick: () -> Unit,
    onCreateScheduleClick: () -> Unit,
    onEditScheduleClick: (Schedule) -> Unit,
    schedulesList: List<Schedule> = emptyList(),
    isLoading: Boolean,
    errorMessage: String?
) {
    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            TopHeaderBar(title = "Schedule")
            BackButton { onBackClick() }
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Show loading spinner
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                    }
                // Show error message if present
                } else if (errorMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                // Show empty state if no schedules
                } else if (schedulesList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No schedule added yet",
                            color = MaterialTheme.colorScheme.secondary,
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Medium
                        )
                    }
                // Show list of schedules
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(schedulesList) { schedule ->
                            ScheduleItem(
                                onEditClick = { onEditScheduleClick(schedule) },
                                scheduleName = schedule.name,
                                frequency = schedule.frequency,
                                weekDays = schedule.weekdays,
                                executionTime = schedule.executionTime
                            )
                            Spacer(modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
            // "Create Schedule" action at the bottom
            Column (
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Spacer(modifier = Modifier.size(32.dp))
                Text(
                    text = "-- Create Schedule --",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 16.sp,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clickable { onCreateScheduleClick() }
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.size(64.dp))
            }
        }
    }
}

// Preview for Compose UI
@Preview
@Composable
fun ScheduleActivityPreview() {
    FrogTheme {
        ScheduleScreen(
            onBackClick = {},
            onCreateScheduleClick = {},
            onEditScheduleClick = { _ -> },
            schedulesList = listOf(
                Schedule(1, "Feeding", "", "daily", "0", "18:00", null, ""),
                Schedule(2, "Cleaning", "", "weekly", "1,4", "18:00", null, "")
            ),
            isLoading = false,
            errorMessage = null
        )
    }
}