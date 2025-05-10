package put.inf154030.frog.views.activities.parameters

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.views.fragments.BackButton
import put.inf154030.frog.views.fragments.TopHeaderBar
import put.inf154030.frog.models.Parameter
import put.inf154030.frog.models.responses.ParametersResponse
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddParameterActivity : ComponentActivity() {
    private lateinit var addParameterLauncher: ActivityResultLauncher<Intent>
    private var parametersList by mutableStateOf<List<Parameter>>(emptyList())
    private var isLoading by mutableStateOf(false)
    private var errorMessage by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val containerId = intent.getIntExtra("CONTAINER_ID", -1)

        addParameterLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            // When returning from AddLocationActivity, refresh the parameters
            loadParameters(containerId)
        }

        setContent {
            FrogTheme {
                AddParameterScreen(
                    onBackClick = { finish() },
                    onSaveClick = { TODO() },
                    onCreateParameterClick = {
                        val intent = Intent(this, CreateParameterActivity::class.java)
                        intent.putExtra("CONTAINER_ID", containerId)
                        startActivity(intent)
                    },
                    parametersList = parametersList
                )
            }
        }

        loadParameters(containerId)
    }

    private fun loadParameters(
        containerId: Int
    ) {
        isLoading = true
        errorMessage = null

        ApiClient.apiService.getParameters(containerId).enqueue(object: Callback<ParametersResponse> {
            override fun onResponse(
                call: Call<ParametersResponse>,
                response: Response<ParametersResponse>
            ) {
                isLoading = false
                if (response.isSuccessful) {
                    parametersList = response.body()?.parameters ?: emptyList()
                } else {
                    errorMessage = "Failed to load parameters: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<ParametersResponse>, t: Throwable) {
                isLoading = false
                errorMessage = "Network error: ${t.message}"
            }
        })
    }
}

@Composable
fun AddParameterScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onCreateParameterClick: () -> Unit,
    parametersList: List<Parameter> = emptyList()
) {
    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            TopHeaderBar(
                title = "Add Parameter",
            )
            BackButton { onBackClick() }
            Column (
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                var isParameterDropdownExpanded by remember { mutableStateOf(false) }
                var selectedParameter by remember { mutableStateOf<Parameter?>(null) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { isParameterDropdownExpanded = true }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedParameter?.name ?: "Select parameter",
                            fontFamily = PoppinsFamily
                        )

                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Expand"
                        )
                    }

                    DropdownMenu(
                        expanded = isParameterDropdownExpanded,
                        onDismissRequest = { isParameterDropdownExpanded = false },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .fillMaxWidth(0.65f)
                    ) {
                        parametersList.forEach { parameter ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = parameter.name,
                                        fontFamily = PoppinsFamily
                                    )
                                },
                                onClick = {
                                    selectedParameter = parameter
                                    isParameterDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.size(64.dp))

                var valueText by remember { mutableStateOf("") }

                BasicTextField(
                    value = valueText,
                    onValueChange = { newValue ->
                        // Only accept numeric input with at most one decimal point
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                            valueText = newValue
                        }
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
                        fontSize = 16.sp,
                        fontFamily = PoppinsFamily
                    ),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (valueText.isEmpty()) {
                                Text("Value")
                            }
                            innerTextField()
                        }
                    }
                )
                Spacer(modifier = Modifier.size(64.dp))
                Text(
                    text = "-- Create Parameter --",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 16.sp,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clickable { onCreateParameterClick() }
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.size(64.dp))
                Button(
                    onClick = { onSaveClick() },
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                ) {
                    Text(
                        text = "Save",
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
fun AddParameterActivityPreview () {
    FrogTheme {
        AddParameterScreen(
            onBackClick = {  },
            onSaveClick = {  },
            onCreateParameterClick = {  },
            parametersList = listOf(
                Parameter(1, "Temperature", 35.0, "C", null, null, true, null, null, null),
                Parameter(2, "Humidity", 20.0, "%", null, null, true, null, null, null),
                Parameter(3, "PH", 6.0, "", null, null, true, null, null, null)
            )
        )
    }
}