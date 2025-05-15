package put.inf154030.frog.views.activities.locations

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.views.fragments.BackButton
import put.inf154030.frog.views.fragments.TopHeaderBar
import put.inf154030.frog.models.requests.LocationCreateRequest
import put.inf154030.frog.models.responses.LocationResponse
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddLocationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FrogTheme {
                AddLocationScreen(
                    onBackClick = { finish() },
                    onAddSuccess = { finish() }
                )
            }
        }
    }
}

@Composable
fun AddLocationScreen (
    onBackClick: () -> Unit,
    onAddSuccess: () -> Unit
) {
    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            TopHeaderBar(
                title = "New Location"
            )
            BackButton { onBackClick() }
            Column (
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                var name by remember { mutableStateOf("") }
                var isLoading by remember { mutableStateOf(false) }
                var errorMessage by remember { mutableStateOf<String?>(null) }

                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Name",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .padding(start = 8.dp, bottom = 4.dp),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "${name.length}/32 characters",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .padding(bottom = 4.dp, end = 8.dp),
                        fontFamily = PoppinsFamily
                    )
                }

                BasicTextField(
                    value = name,
                    onValueChange = { newValue ->
                        // Only update if within character limit
                        if (newValue.length <= 32) {
                            name = newValue
                            errorMessage = null
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
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
                            innerTextField()
                        }
                    }
                )
                Spacer(modifier = Modifier.size(8.dp))
                // Error message
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.size(56.dp))
                Button(
                    onClick = {
                        if (name.trim().isEmpty()) {
                            errorMessage = "Location name cannot be empty"
                            return@Button
                        }

                        isLoading = true
                        errorMessage = null

                        val locationCreateRequest = LocationCreateRequest(name = name.trim())

                        ApiClient.apiService.createLocation(locationCreateRequest).enqueue(object : Callback<LocationResponse> {
                            override fun onResponse(
                                call: Call<LocationResponse>,
                                response: Response<LocationResponse>
                            ) {
                                isLoading = false
                                if (response.isSuccessful) {
                                    onAddSuccess()
                                } else {
                                    errorMessage =
                                        "Failed to create location: ${response.message()}"
                                }
                            }

                            override fun onFailure(call: Call<LocationResponse>, t: Throwable) {
                                isLoading = false
                                errorMessage = "Network error: ${t.message}"
                            }
                        })
                    },
                    modifier = Modifier.fillMaxWidth(0.65f),
                    enabled = !isLoading
                ) {
                    Text(
                        text = if (isLoading) "Adding..." else "Add",
                        fontFamily = PoppinsFamily
                    )
                }
                Spacer(modifier = Modifier.size(64.dp))
            }
        }
    }
}

@Preview
@Composable
fun AddLocationActivityPreview() {
    FrogTheme {
        AddLocationScreen(
            onBackClick = {  },
            onAddSuccess = {  }
        )
    }
}