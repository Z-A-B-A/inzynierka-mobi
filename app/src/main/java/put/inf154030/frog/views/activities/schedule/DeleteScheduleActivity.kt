package put.inf154030.frog.views.activities.schedule

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.models.responses.MessageResponse
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import put.inf154030.frog.views.fragments.TopHeaderBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeleteScheduleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scheduleId = intent.getIntExtra("SCHEDULE_ID", -1)

        setContent {
            FrogTheme {
                DeleteScheduleScreen(
                    onYesClick = {
                        ApiClient.apiService.deleteSchedule(scheduleId)
                            .enqueue(object: Callback<MessageResponse> {
                                override fun onResponse(
                                    call: Call<MessageResponse>,
                                    response: Response<MessageResponse>
                                ) {
                                    finish()
                                }

                                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                                    Toast.makeText(this@DeleteScheduleActivity, "Oops! Something went wrong :( Try again.", Toast.LENGTH_LONG).show()
                                }

                            })
                        finish()
                    },
                    onNoClick = { finish() }
                )
            }
        }
    }
}

@Composable
fun DeleteScheduleScreen (
    onYesClick: () -> Unit,
    onNoClick: () -> Unit
) {
    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            TopHeaderBar(
                title = "Delete Schedule"
            )
            Column (
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Are you sure?",
                    color = MaterialTheme.colorScheme.secondary,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                )
                Text(
                    text = "This operation can not be undone",
                    color = MaterialTheme.colorScheme.secondary,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
                Spacer(modifier = Modifier.size(64.dp))
                Row {
                    Button(
                        modifier = Modifier.width(128.dp),
                        onClick = { onYesClick() }
                    ) {
                        Text(
                            text = "Yes",
                            fontFamily = PoppinsFamily
                        )
                    }
                    Spacer(modifier = Modifier.size(32.dp))
                    Button(
                        modifier = Modifier.width(128.dp),
                        onClick = { onNoClick() }
                    ) {
                        Text(
                            text = "No",
                            fontFamily = PoppinsFamily
                        )
                    }
                }
                Spacer(modifier = Modifier.size(64.dp))
            }
        }
    }
}

@Preview
@Composable
fun DeleteScheduleActivityPreview() {
    FrogTheme {
        DeleteScheduleScreen(
            onYesClick = {  },
            onNoClick = {  }
        )
    }
}