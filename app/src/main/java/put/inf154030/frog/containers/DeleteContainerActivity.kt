package put.inf154030.frog.containers

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
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
import put.inf154030.frog.fragments.TopHeaderBar
import put.inf154030.frog.models.MessageResponse
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeleteContainerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val containerId = intent.getIntExtra("CONTAINER_ID", -1)

        super.onCreate(savedInstanceState)
        setContent {
            FrogTheme {
                DeleteContainerScreen(
                    onYes = {
                        ApiClient.apiService.deleteContainer(containerId)
                            .enqueue(object : Callback<MessageResponse> {
                                override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                                    if (response.isSuccessful) {
                                        Toast.makeText(
                                            this@DeleteContainerActivity,
                                            "Container deleted successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        setResult(RESULT_OK)
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            this@DeleteContainerActivity,
                                            "Failed to delete container: ${response.message()}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                                    Toast.makeText(
                                        this@DeleteContainerActivity,
                                        "Network error: ${t.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                    },
                    onNo = { finish() }
                )
            }
        }
    }
}

@Composable
fun DeleteContainerScreen(
    onYes: () -> Unit = {},
    onNo: () -> Unit = {}
) {
    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            TopHeaderBar(
                title = "Delete Container"
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
                        modifier = Modifier.fillMaxWidth(0.3f),
                        onClick = { onYes() }
                    ) {
                        Text(
                            text = "Yes",
                            fontFamily = PoppinsFamily
                        )
                    }
                    Spacer(modifier = Modifier.size(32.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(0.5f),
                        onClick = { onNo() }
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
fun DeleteContainerActivityPreview () {
    FrogTheme {
        DeleteContainerScreen(
            onYes = {  },
            onNo = {  }
        )
    }
}