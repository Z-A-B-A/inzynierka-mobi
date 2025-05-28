package put.inf154030.frog.views.activities.locations

import android.content.Intent
import android.os.Bundle
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.models.requests.LocationUpdateRequest
import put.inf154030.frog.models.responses.LocationDetailResponse
import put.inf154030.frog.models.responses.LocationUpdateResponse
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import put.inf154030.frog.views.fragments.BackButton
import put.inf154030.frog.views.fragments.TopHeaderBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Activity for editing a location's name
class EditLocationActivity : ComponentActivity() {
    // State for loading, error message, and location name
    private var isLoading by mutableStateOf(false)
    private var errorMessage by mutableStateOf<String?>(null)
    private var locationName by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Get location ID from intent
        val locationId = intent.getIntExtra("LOCATION_ID", -1)

        setContent {
            FrogTheme {
                EditLocationScreen(
                    onBackClick = { finish() },
                    // Navigate to delete screen
                    onDeleteLocationClick = {
                        val intent = Intent(this, DeleteLocationActivity::class.java)
                        intent.putExtra("LOCATION_ID", locationId)
                        startActivity(intent)
                        finish()
                    },
                    // Save changes to location name
                    onSaveClick = { name ->
                        isLoading = true
                        errorMessage = null

                        val locationUpdateRequest = LocationUpdateRequest(name = name)
                        ApiClient.apiService.updateLocation(locationId, locationUpdateRequest)
                            .enqueue(object : Callback<LocationUpdateResponse> {
                                override fun onResponse(
                                    call: Call<LocationUpdateResponse>,
                                    response: Response<LocationUpdateResponse>
                                ) {
                                    isLoading = false
                                    if (response.isSuccessful) {
                                        finish()
                                    } else {
                                        errorMessage = "Failed to update location: ${response.message()}"
                                    }
                                }
                                override fun onFailure(call: Call<LocationUpdateResponse>, t: Throwable) {
                                    isLoading = false
                                    errorMessage = "Network error: ${t.message}"
                                }
                            })
                    },
                    setErrorMessage = { message -> errorMessage = message },
                    locationName = locationName,
                    isLoading = isLoading,
                    errorMessage = errorMessage
                )
            }
        }
        // Load current location data on start
        loadLocationData(locationId)
    }

    // Fetch location details from API
    private fun loadLocationData(locationId: Int) {
        isLoading = true
        ApiClient.apiService.getLocation(locationId).enqueue(object : Callback<LocationDetailResponse> {
            override fun onResponse(call: Call<LocationDetailResponse>, response: Response<LocationDetailResponse>) {
                isLoading = false
                if (response.isSuccessful) {
                    locationName = response.body()?.name ?: ""
                } else {
                    errorMessage = "Failed to load location: ${response.message()}"
                }
            }
            override fun onFailure(call: Call<LocationDetailResponse>, t: Throwable) {
                isLoading = false
                errorMessage = "Network error: ${t.message}"
            }
        })
    }
}

// Composable for editing location UI
@Composable
fun EditLocationScreen (
    onBackClick: () -> Unit,
    onDeleteLocationClick: () -> Unit,
    onSaveClick: (String) -> Unit,
    setErrorMessage: (String?) -> Unit,
    locationName: String,
    isLoading: Boolean,
    errorMessage: String?
) {
    // Local state for the editable name field
    var name by remember(locationName) { mutableStateOf(locationName) }
    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Show loading spinner if loading
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        Column {
            TopHeaderBar(title = "Edit Location")
            BackButton { onBackClick() }
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                // Name label and character count
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
                    // Character count display
                    Text(
                        text = "${name.length}/32 characters",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .padding(bottom = 4.dp, end = 8.dp),
                        fontFamily = PoppinsFamily
                    )
                }
                // Editable name input
                BasicTextField(
                    value = name,
                    onValueChange = { newValue ->
                        val trimmed = newValue.trim()
                        if (trimmed.length <= 32) {
                            name = trimmed
                            setErrorMessage(null)
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
                // Delete location link
                Text(
                    text = "delete location",
                    color = Color.Red,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .clickable { onDeleteLocationClick() }
                        .padding(end = 8.dp)
                )
                Spacer(modifier = Modifier.size(64.dp))
                // Save button
                Button(
                    onClick = {
                        if (name.isEmpty()) {
                            setErrorMessage("Location name cannot be empty")
                            return@Button
                        }
                        onSaveClick(name)
                    },
                    modifier = Modifier.fillMaxWidth(0.65f),
                    enabled = !isLoading
                ) {
                    Text(
                        text = if (isLoading) "Saving..." else "Save",
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
fun EditLocationPreview() {
    FrogTheme {
        EditLocationScreen(
            onBackClick = {  },
            onDeleteLocationClick = {},
            onSaveClick = { _ -> },
            setErrorMessage = { _ -> },
            locationName = "Sample Location",
            isLoading = false,
            errorMessage = null
        )
    }
}