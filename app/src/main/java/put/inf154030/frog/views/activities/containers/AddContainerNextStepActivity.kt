package put.inf154030.frog.views.activities.containers

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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.models.requests.ContainerCreateRequest
import put.inf154030.frog.repository.ContainersRepository
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import put.inf154030.frog.views.fragments.BackButton
import put.inf154030.frog.views.fragments.TopHeaderBar

// Activity for the second step of adding a container (name & description)
class AddContainerNextStepActivity : ComponentActivity() {
    private var isLoading by mutableStateOf(false)
    private var errorMessage by mutableStateOf<String?>(null)
    private val containersRepository = ContainersRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val locationId = intent.getIntExtra("LOCATION_ID", -1)
        val containerCode = intent.getStringExtra("CONTAINER_CODE") ?: "Invalid Code"

        setContent {
            FrogTheme {
                // Main screen composable for this step
                AddContainerNextStepScreen(
                    onBackClick = { finish() },
                    onFinishClick = { name, description ->
                        isLoading = true
                        errorMessage = null

                        // Prepare request object
                        val containerCreateRequest = ContainerCreateRequest(name, description, containerCode)
                        containersRepository.createContainer(
                            containerCreateRequest,
                            locationId,
                            onResult = { success, error ->
                                isLoading = false
                                errorMessage = error
                                if (success) finish()
                            }
                        )
                    },
                    isLoading = isLoading,
                    errorMessage = errorMessage
                )
            }
        }
    }
}

// Composable for entering container name and description
@Composable
fun AddContainerNextStepScreen (
    onBackClick: () -> Unit,
    onFinishClick: (String, String) -> Unit,
    isLoading: Boolean,
    errorMessage: String?
) {
    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            TopHeaderBar( title = "Nowy Pojemnik" ) // Header bar
            BackButton { onBackClick() } // Back button
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                // State for form fields and errors
                var name by remember { mutableStateOf("") }
                var description by remember { mutableStateOf("") }
                var errorMessageName by remember { mutableStateOf<String?>(null) }
                var errorMessageDescription by remember { mutableStateOf<String?>(null) }

                // Name label and input
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Nazwa",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .padding(start = 8.dp, bottom = 4.dp),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "${name.length}/32 znaki",
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
                        if (newValue.length <= 32) {
                            name = newValue
                            errorMessageName = null
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    singleLine = true,
                    textStyle = TextStyle( fontSize = 16.sp ),
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
                // Error message for name
                errorMessageName?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.size(24.dp))

                // Description label and input
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Opis",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .padding(start = 8.dp, bottom = 4.dp),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "${description.length}/300 znaków",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .padding(bottom = 4.dp, end = 8.dp),
                        fontFamily = PoppinsFamily
                    )
                }
                // Multiline description field
                BasicTextField(
                    value = description,
                    onValueChange = { newValue ->
                        if (newValue.length <= 300) {
                            description = newValue
                            errorMessageDescription = null
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(240.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    singleLine = false,
                    textStyle = TextStyle( fontSize = 16.sp ),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                            contentAlignment = Alignment.TopStart
                        ) {
                            innerTextField()
                        }
                    }
                )
                Spacer(modifier = Modifier.size(8.dp))

                // Error message for description
                errorMessageDescription?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.size(54.dp))
                // Loading spinner while submitting
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .testTag("CircularProgressIndicator")
                    )
                }

                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                // Finish button
                Button(
                    onClick = {
                        var hasError = false
                        // Validate name
                        if (name.trim().isEmpty()) {
                            errorMessageName = "Nazwa nie może być pusta"
                            hasError = true
                        }
                        // Validate description
                        if (description.trim().isEmpty()) {
                            errorMessageDescription = "Opis nie może być pusty"
                            hasError = true
                        }
                        if (hasError) return@Button

                        // Clear errors and call finish
                        errorMessageName = null
                        errorMessageDescription = null

                        onFinishClick(name, description)
                    },
                    modifier = Modifier.fillMaxWidth(0.65f),
                    enabled = !isLoading
                ) {
                    Text(
                        text = if (isLoading) "Ładowanie..." else "Zakończ",
                        fontFamily = PoppinsFamily
                    )
                }
                Spacer(modifier = Modifier.size(64.dp))
            }
        }
    }
}

// Preview for Compose UI
@Preview
@Composable
fun AddContainerNextStepActivityPreview () {
    FrogTheme {
        AddContainerNextStepScreen(
            onBackClick = {  },
            onFinishClick = { _, _ -> },
            isLoading = false,
            errorMessage = null
        )
    }
}