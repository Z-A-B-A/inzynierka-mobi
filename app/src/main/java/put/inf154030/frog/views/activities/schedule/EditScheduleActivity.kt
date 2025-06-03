package put.inf154030.frog.views.activities.schedule

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.models.requests.ScheduleUpdateRequest
import put.inf154030.frog.repository.SchedulesRepository
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import put.inf154030.frog.views.fragments.BackButton
import put.inf154030.frog.views.fragments.TopHeaderBar
import java.util.Locale

// Activity for editing a schedule
class EditScheduleActivity : ComponentActivity() {
    // State for loading and error message
    private var isLoading by mutableStateOf(false)
    private var errorMessage by mutableStateOf<String?>(null)
    private val schedulesRepository = SchedulesRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get schedule ID and execution time from intent
        val scheduleId = intent.getIntExtra("SCHEDULE_ID", -1)
        val scheduleExecTime = intent.getStringExtra("SCHEDULE_EXEC_TIME") ?: "0000-01-01T12:00:00Z"

        setContent {
            FrogTheme {
                // Main screen composable for editing schedule
                EditScheduleScreen(
                    onBackClick = { finish() },
                    onDeleteScheduleClick = {
                        // Navigate to delete activity
                        val intent = Intent(this, DeleteScheduleActivity::class.java)
                        intent.putExtra("SCHEDULE_ID", scheduleId)
                        startActivity(intent)
                        finish()
                    },
                    onSaveClick = { executionTime ->
                        isLoading = true
                        errorMessage = null

                        // Prepare update request
                        val scheduleUpdateRequest = ScheduleUpdateRequest(executionTime)

                        // Make API call to update schedule
                        schedulesRepository.updateSchedule(
                            scheduleId,
                            scheduleUpdateRequest,
                            onResult = { success, error ->
                                isLoading = false
                                errorMessage = error
                                if (success) finish()
                            }
                        )
                    },
                    executionTime = scheduleExecTime,
                    isLoading = isLoading,
                    errorMessage = errorMessage
                )
            }
        }
    }
}

// Composable for the edit schedule screen UI
@Composable
fun EditScheduleScreen(
    onBackClick: () -> Unit,
    onDeleteScheduleClick: () -> Unit,
    onSaveClick: (String) -> Unit,
    executionTime: String,
    isLoading: Boolean,
    errorMessage: String?
) {
    // Parse the ISO 8601 timestamp to extract only HH:mm
    val formattedCurrentTime = try {
        // Extract the time part from the ISO string
        // Format is like "0000-01-01T03:25:00Z", we want just "03:25"
        val timePattern = "\\d{4}-\\d{2}-\\d{2}T(\\d{2}:\\d{2}):\\d{2}Z".toRegex()
        val matchResult = timePattern.find(executionTime)
        matchResult?.groupValues?.get(1) ?: executionTime
    } catch (e: Exception) {
        // Fallback to original string if parsing fails
        executionTime
    }

    // Extract hour and minute from the formatted time
    val initialHour = try {
        formattedCurrentTime.split(":")[0].toInt()
    } catch (e: Exception) {
        12 // Default fallback
    }

    val initialMinute = try {
        formattedCurrentTime.split(":")[1].toInt()
    } catch (e: Exception) {
        0 // Default fallback
    }

    var hour by remember { mutableIntStateOf(initialHour) }
    var minute by remember { mutableIntStateOf(initialMinute) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Format time as "HH:mm"
    val formattedTime = String.format(Locale.US, "%02d:%02d", hour, minute)

    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            // Top bar and back button
            TopHeaderBar(title = "Edit Schedule")
            BackButton { onBackClick() }
            Spacer(modifier = Modifier.size(64.dp))
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Time selection label and picker
                Text(
                    text = "Execution time",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, bottom = 4.dp),
                    color = MaterialTheme.colorScheme.secondary
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { showTimePicker = true }
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = formattedTime,
                        fontFamily = PoppinsFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Time picker dialog
                if (showTimePicker) {
                    TimePickerDialog(
                        onDismissRequest = { showTimePicker = false },
                        onTimeSelected = { selectedHour, selectedMinute ->
                            hour = selectedHour
                            minute = selectedMinute
                            showTimePicker = false
                        },
                        initialHour = hour,
                        initialMinute = minute
                    )
                }
            }
            // Bottom actions: error, delete, save
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Spacer(modifier = Modifier.size(32.dp))
                // Show error message if present
                errorMessage?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                // Delete schedule action
                Text(
                    text = "delete schedule",
                    color = Color.Red,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .clickable { onDeleteScheduleClick() }
                        .padding(end = 8.dp)
                )
                Spacer(modifier = Modifier.size(64.dp))
                // Save button
                Button(
                    onClick = { onSaveClick(formattedTime) },
                    modifier = Modifier.fillMaxWidth(0.65f),
                    enabled = !isLoading,
                ) {
                    Text(
                        text = "Save",
                        fontFamily = PoppinsFamily
                    )
                }
                Spacer(modifier = Modifier.size(64.dp))
            }
        }
        // Overlay spinner if loading
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp).testTag("CircularProgressIndicator")
                )
            }
        }
    }
}

// Preview for Compose UI
@Preview
@Composable
fun EditScheduleActivityPreview() {
    FrogTheme {
        EditScheduleScreen(
            onBackClick = {},
            onDeleteScheduleClick = {},
            onSaveClick = { _ -> },
            executionTime = "0000-01-01T12:00:00Z",
            isLoading = false,
            errorMessage = null
        )
    }
}