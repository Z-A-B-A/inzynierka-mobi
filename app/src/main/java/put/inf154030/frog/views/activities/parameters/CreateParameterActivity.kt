package put.inf154030.frog.views.activities.parameters

import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.material3.Switch
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
import put.inf154030.frog.models.requests.ParameterCreateRequest
import put.inf154030.frog.models.responses.ParameterResponse
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateParameterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val containerId = intent.getIntExtra("CONTAINER_ID", -1)

        setContent {
            FrogTheme {
                CreateParameterScreen(
                    onBackClick = { finish() },
                    onSaveClick = { name, unit, value, isControlled ->
                        val parameterCreateRequest = ParameterCreateRequest(
                            name, unit, value, value, isControlled, "custom"
                        )

                        ApiClient.apiService.createParameter(containerId, parameterCreateRequest)
                            .enqueue(object: Callback<ParameterResponse> {
                                override fun onResponse(
                                    call: Call<ParameterResponse>,
                                    response: Response<ParameterResponse>
                                ) {
                                    finish()
                                }

                                override fun onFailure(
                                    call: Call<ParameterResponse>,
                                    t: Throwable
                                ) {
                                    Toast.makeText(this@CreateParameterActivity, "Oops! Something went wrong. Try again.", Toast.LENGTH_LONG).show()
                                }

                            })
                    }
                )
            }
        }
    }
}

@Composable
fun CreateParameterScreen (
    onBackClick: () -> Unit,
    onSaveClick: (String, String, Double, Boolean) -> Unit
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
                var name by remember { mutableStateOf("") }
                var unit by remember { mutableStateOf("") }
                var valueText by remember { mutableStateOf("") }
                var isControlled by remember { mutableStateOf(false) }

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
                        fontSize = 16.sp,
                        fontFamily = PoppinsFamily
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
                Spacer(modifier = Modifier.size(64.dp))
                BasicTextField(
                    value = unit,
                    onValueChange = { newValue -> unit = newValue },
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
                            if (unit.isEmpty()) {
                                Text("Unit")
                            }
                            innerTextField()
                        }
                    }
                )
                Spacer(modifier = Modifier.size(64.dp))
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
                Row (
                    modifier = Modifier.fillMaxWidth(0.65f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Is controlled",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Switch(
                        checked = isControlled,
                        onCheckedChange = { isControlled = !isControlled }
                    )
                }
                Spacer(modifier = Modifier.size(64.dp))
                Button(
                    onClick = { onSaveClick(name, unit, valueText.toDouble(), isControlled) },
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
fun CreateParameterActivityPreview () {
    FrogTheme {
        CreateParameterScreen(
            onBackClick = {  },
            onSaveClick = { _, _, _, _ -> }
        )
    }
}