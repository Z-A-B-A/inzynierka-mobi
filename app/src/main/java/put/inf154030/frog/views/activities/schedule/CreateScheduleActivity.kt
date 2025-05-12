package put.inf154030.frog.views.activities.schedule

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.views.fragments.BackButton
import put.inf154030.frog.views.fragments.TopHeaderBar
import put.inf154030.frog.models.requests.ScheduleCreateRequest
import put.inf154030.frog.models.responses.ScheduleResponse
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import retrofit2.Call
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import retrofit2.Callback
import retrofit2.Response

class CreateScheduleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val containerId = intent.getIntExtra("CONTAINER_ID", -1)

        setContent {
            FrogTheme {
                CreateScheduleScreen(
                    onBackClick = { finish() },
                    onCreateClick = { name, frequency, weekDays, executionTime ->
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
                        val currentDate = dateFormat.format(Date())

                        val scheduleCreateRequest = ScheduleCreateRequest(name, currentDate.toString(), frequency, weekDays, executionTime)

                        ApiClient.apiService.createSchedule(containerId, scheduleCreateRequest)
                            .enqueue(object: Callback<ScheduleResponse> {
                                override fun onResponse(
                                    call: Call<ScheduleResponse>,
                                    response: Response<ScheduleResponse>
                                ) {
//                                    TODO("Do zrobienia powiadomienie")
                                    finish()
                                }

                                override fun onFailure(call: Call<ScheduleResponse>, t: Throwable) {
                                    Toast.makeText(this@CreateScheduleActivity, "Oops! Something went wrong :( Try again.", Toast.LENGTH_LONG).show()
                                }
                            })
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun CreateScheduleScreen(
    onBackClick: () -> Unit,
    onCreateClick: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var execution by remember { mutableStateOf(false) }
    var weekDays by remember { mutableStateOf("") }

    // Add state for time
    var hour by remember { mutableIntStateOf(12) }
    var minute by remember { mutableIntStateOf(0) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Format time as "HH:mm"
    val formattedTime = String.format(Locale.US, "%02d:%02d", hour, minute)

    // Days of week selection
    var selectedDays by remember { mutableStateOf(setOf<Int>()) }

    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            TopHeaderBar(
                title = "Create Schedule",
            )
            BackButton { onBackClick() }
            Spacer(modifier = Modifier.size(64.dp))
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BasicTextField(
                    value = name,
                    onValueChange = { newValue -> name = newValue },
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 16.sp
                    ),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (name.isEmpty()) {
                                Text("Name", fontFamily = PoppinsFamily)
                            }
                            innerTextField()
                        }
                    }
                )
                Spacer(modifier = Modifier.size(32.dp))
                Text(
                    text = "-- execution --",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 16.sp,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.size(16.dp))
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(0.65f)
                ) {
                    Text(
                        text = "daily",
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 18.sp,
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(72.dp),
                        textAlign = TextAlign.End
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    Switch(
                        checked = execution,
                        onCheckedChange = { execution = !execution }
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    Text(
                        text = "weekly",
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 18.sp,
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(72.dp),
                        textAlign = TextAlign.Start
                    )
                }
                Spacer(modifier = Modifier.size(16.dp))
                if (execution) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        days.forEachIndexed { index, day ->
                            val isSelected = selectedDays.contains(index + 1)

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            color = if (isSelected)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.secondary,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            selectedDays = if (isSelected) {
                                                selectedDays - (index + 1)
                                            } else {
                                                selectedDays + (index + 1)
                                            }
                                            // Update weekDays string for API (comma-separated values)
                                            weekDays = selectedDays.joinToString(",")
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day,
                                        fontFamily = PoppinsFamily,
                                        fontSize = 14.sp,
                                        color = if (isSelected)
                                            MaterialTheme.colorScheme.secondary
                                        else
                                            MaterialTheme.colorScheme.background
                                    )
                                }
                            }
                        }
                    }

                    // Show selected days text summary
                    if (selectedDays.isNotEmpty()) {
                        Text(
                            text = "Selected: ${selectedDays.sorted().joinToString(", ")}",
                            fontSize = 14.sp,
                            fontFamily = PoppinsFamily,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    } else {
                        Text(
                            text = "Please select at least one day",
                            fontSize = 14.sp,
                            fontFamily = PoppinsFamily,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.size(16.dp))
                }
                Text(
                    text = "-- execution time --",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 16.sp,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.size(16.dp))

                // Time picker button
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
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
            Column (
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Button(
                    onClick = {
                        weekDays = selectedDays.sorted().joinToString(",")

                        // If daily mode is selected, use "0" as the weekdays value
                        if (!execution) {
                            weekDays = "0"
                        }

                        val frequency = if (execution) "weekly" else "daily"

                        onCreateClick(name, frequency, weekDays, formattedTime)
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                ) {
                    Text(
                        text = "Create",
                        fontFamily = PoppinsFamily
                    )
                }
                Spacer(modifier = Modifier.size(64.dp))
            }
        }
    }
}

// Custom Time Picker Dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    initialHour: Int,
    initialMinute: Int
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Select Time", fontFamily = PoppinsFamily) },
        text = {
            TimePicker(state = timePickerState)
        },
        confirmButton = {
            Button(
                onClick = {
                    onTimeSelected(timePickerState.hour, timePickerState.minute)
                }
            ) {
                Text("OK", fontFamily = PoppinsFamily)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismissRequest
            ) {
                Text("Cancel", fontFamily = PoppinsFamily)
            }
        }
    )
}

@Preview
@Composable
fun CreateScheduleActivityPreview() {
    FrogTheme {
        CreateScheduleScreen(
            onBackClick = {},
            onCreateClick = { _, _, _, _ -> }
        )
    }
}