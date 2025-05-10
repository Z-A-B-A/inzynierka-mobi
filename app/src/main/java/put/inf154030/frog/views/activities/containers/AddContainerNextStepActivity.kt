package put.inf154030.frog.views.activities.containers

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.views.fragments.BackButton
import put.inf154030.frog.views.fragments.TopHeaderBar
import put.inf154030.frog.models.requests.ContainerCreateRequest
import put.inf154030.frog.models.responses.ContainerResponse
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddContainerNextStepActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val locationId = intent.getIntExtra("LOCATION_ID", -1)
//        val containerCode = intent.getStringExtra("CONTAINER_CODE")
        val containerType = intent.getStringExtra("CONTAINER_TYPE")

        setContent {
            FrogTheme {
                AddContainerNextStepScreen(
                    onBackClick = { finish() },
                    onFinishClick = { name, description ->
                        val containerCreateRequest = ContainerCreateRequest(name, containerType!!, description)

                        ApiClient.apiService.createContainer(locationId, containerCreateRequest)
                            .enqueue(object : Callback<ContainerResponse> {
                                override fun onResponse(
                                    call: Call<ContainerResponse>,
                                    response: Response<ContainerResponse>
                                ) {
                                    if (!response.isSuccessful) {
                                        Toast.makeText(
                                            this@AddContainerNextStepActivity,
                                            "Failed to create location: ${response.message()}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }

                                override fun onFailure(
                                    call: Call<ContainerResponse>,
                                    t: Throwable
                                ) {
                                    Toast.makeText(
                                        this@AddContainerNextStepActivity,
                                        "Network error: ${t.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
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
fun AddContainerNextStepScreen (
    onBackClick: () -> Unit = {},
    onFinishClick: (String, String) -> Unit
) {
    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            TopHeaderBar(
                title = "New Container"
            )
            BackButton { onBackClick() }
            Column (
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                var name by remember { mutableStateOf("") }
                var description by remember { mutableStateOf("") }
                var isLoading by remember { mutableStateOf(false) }
                var errorMessageName by remember { mutableStateOf<String?>(null) }
                var errorMessageDescription by remember { mutableStateOf<String?>(null) }

                BasicTextField(
                    value = name,
                    onValueChange = { newValue ->
                        name = newValue
                        errorMessageName = null
                    },
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
                                Text("Name")
                            }
                            innerTextField()
                        }
                    }
                )
                Spacer(modifier = Modifier.size(8.dp))
                // Error message
                errorMessageName?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.size(24.dp))
                BasicTextField(
                    value = description,
                    onValueChange = { newValue ->
                        description = newValue
                        errorMessageDescription = null
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .size(240.dp)
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
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                            contentAlignment = Alignment.TopStart
                        ) {
                            if (description.isEmpty()) {
                                Text("Description")
                            }
                            innerTextField()
                        }
                    }
                )
                Spacer(modifier = Modifier.size(8.dp))
                errorMessageName?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.size(54.dp))
                Button(
                    onClick = {
                        if (name.trim().isEmpty()) {
                            errorMessageName = "Container name cannot be empty"
                            return@Button
                        }

                        if (description.trim().isEmpty()) {
                            errorMessageDescription = "Container name cannot be empty"
                            return@Button
                        }

                        isLoading = true
                        errorMessageName = null
                        errorMessageDescription = null

                        onFinishClick(name, description)
                    },
                    modifier = Modifier.fillMaxWidth(0.65f),
                    enabled = !isLoading
                ) {
                    Text(
                        text = if (isLoading) "Loading..." else "Finish",
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
fun AddContainerNextStepActivityPreview () {
    FrogTheme {
        AddContainerNextStepScreen(
            onBackClick = {  },
            onFinishClick = { _, _ ->  }
        )
    }
}