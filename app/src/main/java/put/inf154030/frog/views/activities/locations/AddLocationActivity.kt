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
import put.inf154030.frog.models.requests.LocationCreateRequest
import put.inf154030.frog.repository.LocationsRepository
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import put.inf154030.frog.views.fragments.BackButton
import put.inf154030.frog.views.fragments.TopHeaderBar

// Activity for adding a new location
class AddLocationActivity : ComponentActivity() {
    // State for loading and error message
    private var isLoading by mutableStateOf(false)
    private var errorMessage by mutableStateOf<String?>(null)
    private val locationsRepository = LocationsRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FrogTheme {
                // Main screen composable for adding a location
                AddLocationScreen(
                    onBackClick = { finish() },
                    onAddClick = { name ->
                        isLoading = true
                        errorMessage = null

                        // Prepare request and call API
                        val locationCreateRequest = LocationCreateRequest(name = name.trim())
                        locationsRepository.createLocation(
                            locationCreateRequest,
                            onResult = { success, loading, error ->
                                isLoading = loading
                                errorMessage = error
                                if (success) finish()
                            }
                        )
                    },
                    setErrorMessage = { message -> errorMessage = message },
                    isLoading = isLoading,
                    errorMessage = errorMessage
                )
            }
        }
    }
}

// Composable for the add location screen UI
@Composable
fun AddLocationScreen (
    onBackClick: () -> Unit,
    onAddClick: (String) -> Unit,
    setErrorMessage: (String?) -> Unit,
    isLoading: Boolean,
    errorMessage: String?
) {
    // Local state for the name input
    var name by remember { mutableStateOf("") }

    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            TopHeaderBar(title = "New Location")
            BackButton { onBackClick() }
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                // Name label and character counter
                Row (
                    modifier = Modifier.fillMaxWidth(),
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

                // Name input field
                BasicTextField(
                    value = name,
                    onValueChange = { newValue ->
                        val trimmed = newValue.trim()
                        if (trimmed.length <= 32) {
                            name = trimmed
                            setErrorMessage(null) // Reset error on input
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
                // Error message display
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.size(56.dp))
                // Add button
                Button(
                    onClick = {
                        if (name.isEmpty()) {
                            setErrorMessage("Location name cannot be empty")
                            return@Button
                        }
                        onAddClick(name)
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

// Preview for Compose UI
@Preview
@Composable
fun AddLocationActivityPreview() {
    FrogTheme {
        AddLocationScreen(
            onBackClick = {  },
            onAddClick = { _ -> },
            setErrorMessage = { _ -> },
            isLoading = false,
            errorMessage = null
        )
    }
}