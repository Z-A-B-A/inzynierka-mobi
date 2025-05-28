package put.inf154030.frog.views.activities.containers

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import put.inf154030.frog.models.Location
import put.inf154030.frog.models.requests.ContainerUpdateRequest
import put.inf154030.frog.models.responses.ContainerUpdateResponse
import put.inf154030.frog.models.responses.LocationsResponse
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import put.inf154030.frog.views.fragments.BackButton
import put.inf154030.frog.views.fragments.TopHeaderBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Activity for editing a container's details
class EditContainerActivity : ComponentActivity() {
    private var locationsList by mutableStateOf<List<Location>>(emptyList())
    private var isLoading by mutableStateOf(false)
    private var errorMessage by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get initial container data from intent
        val containerId = intent.getIntExtra("CONTAINER_ID", -1)
        val containerName = intent.getStringExtra("CONTAINER_NAME") ?: "Couldn't load container name"
        val containerDescription = intent.getStringExtra("CONTAINER_DESCRIPTION") ?: "Couldn't load container description"

        val locationId = intent.getIntExtra("LOCATION_ID", -1)

        setContent {
            FrogTheme {
                EditContainerScreen(
                    onBackClick = { finish() },
                    onSaveClick = { name, description, selectedLocation ->
                        errorMessage = null
                        isLoading = true

                        // Prepare update request
                        val containerUpdateRequest = ContainerUpdateRequest(
                            name = name,
                            description = description,
                            active = true,
                            locationId = selectedLocation
                        )

                        // Call API to update container
                        ApiClient.apiService.updateContainer(containerId, containerUpdateRequest)
                            .enqueue(object : Callback<ContainerUpdateResponse> {
                                override fun onResponse(
                                    call: Call<ContainerUpdateResponse>,
                                    response: Response<ContainerUpdateResponse>
                                ) {
                                    if (response.isSuccessful) {
                                        isLoading = false
                                        finish()
                                    } else {
                                        // Handle error
                                        isLoading = false
                                        errorMessage = "Failed to update container: ${response.message()}"
                                    }
                                }

                                override fun onFailure(call: Call<ContainerUpdateResponse>, t: Throwable) {
                                    isLoading = false
                                    errorMessage = "Network error: ${t.message}"
                                }
                            })
                    },
                    onDeleteContainerClick = {
                        // Open delete confirmation activity
                        val intent = Intent(this, DeleteContainerActivity::class.java)
                        intent.putExtra("CONTAINER_ID", containerId)
                        startActivity(intent)
                        finish()
                    },
                    setLoading = { loading -> isLoading = loading },
                    setErrorMessage = { message -> errorMessage = message },
                    containerName = containerName,
                    containerDescription = containerDescription,
                    locationsList = locationsList,
                    locationId = locationId,
                    isLoading = isLoading,
                    errorMessage = errorMessage,
                )
            }
        }
        // Load locations when activity is created
        loadLocations()
    }

    // Loads available locations from API
    private fun loadLocations() {
        isLoading = true
        errorMessage = null

        ApiClient.apiService.getLocations().enqueue(object : Callback<LocationsResponse> {
            override fun onResponse(
                call: Call<LocationsResponse>,
                response: Response<LocationsResponse>
            ) {
                isLoading = false
                if (response.isSuccessful) {
                    locationsList = response.body()?.locations ?: emptyList()
                } else {
                    errorMessage = "Failed to load locations: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<LocationsResponse>, t: Throwable) {
                isLoading = false
                errorMessage = "Network error: ${t.message}"
            }

        })
    }
}

// Composable for editing container details
@Composable
fun EditContainerScreen(
    onBackClick: () -> Unit,
    onSaveClick: (String, String, Int) -> Unit,
    onDeleteContainerClick: () -> Unit,
    setLoading: (Boolean) -> Unit,
    setErrorMessage: (String) -> Unit,
    containerName: String,
    containerDescription: String,
    locationId: Int,
    locationsList: List<Location>,
    isLoading: Boolean,
    errorMessage: String?
) {
    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Local state for form fields and validation
        var name by remember { mutableStateOf(containerName) }
        var description by remember { mutableStateOf(containerDescription) }
        var errorMessageName by remember { mutableStateOf<String?>(null) }
        var errorMessageDescription by remember { mutableStateOf<String?>(null) }

        // State for location selection
        var isLocationDropdownExpanded by remember { mutableStateOf(false) }
        var selectedLocation by remember { mutableStateOf<Location?>(null) }

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
            TopHeaderBar(title = "Edit Container")
            BackButton { onBackClick() }
            Spacer(modifier = Modifier.size(16.dp))

            // Load locations when the screen is created
            LaunchedEffect(locationsList) {
                setLoading(false)
                val currentLocation = locationsList.find { it.id == locationId }
                selectedLocation = currentLocation ?: locationsList.firstOrNull()
            }

            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Name input
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
                // Name error message
                errorMessageName?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))

                // Description input
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Description",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .padding(start = 8.dp, bottom = 4.dp),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "${description.length}/300 characters",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .padding(bottom = 4.dp, end = 8.dp),
                        fontFamily = PoppinsFamily
                    )
                }
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
                    textStyle = TextStyle(
                        fontSize = 16.sp
                    ),
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

                // Description error message
                errorMessageDescription?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))

                // Location dropdown
                Text(
                    text = "Lokalizacja",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, bottom = 4.dp),
                    color = MaterialTheme.colorScheme.secondary
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { isLocationDropdownExpanded = true }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedLocation?.name ?: if (isLoading) "Loading locations..." else "Select location",
                            fontFamily = PoppinsFamily
                        )

                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Expand"
                        )
                    }

                    DropdownMenu(
                        expanded = isLocationDropdownExpanded,
                        onDismissRequest = { isLocationDropdownExpanded = false },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .fillMaxWidth(0.65f)
                    ) {
                        locationsList.forEach { location ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = location.name,
                                        fontFamily = PoppinsFamily
                                    )
                                },
                                onClick = {
                                    selectedLocation = location
                                    isLocationDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.size(16.dp))

            // Bottom section: delete and save
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Delete container link
                Text(
                    text = "delete container",
                    color = Color.Red,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .clickable { onDeleteContainerClick() }
                        .padding(end = 8.dp)
                )
                Spacer(modifier = Modifier.size(16.dp))
                // General error message
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                // Save button
                Button(
                    onClick = {
                        if (name.isBlank()) {
                            errorMessageName = "Name cannot be empty"
                        } else if (description.isBlank()) {
                            errorMessageDescription = "Description cannot be empty"
                        } else if (selectedLocation == null) {
                            setErrorMessage("Please select a location")
                        } else {
                            errorMessageName = null
                            errorMessageDescription = null
                            onSaveClick(name, description, selectedLocation!!.id)
                        }
                    },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(0.65f),
                    enabled = !isLoading
                ) {
                    Text(
                        text = "Save",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold
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
fun EditContainerActivityPreview () {
    FrogTheme {
        EditContainerScreen(
            onBackClick = {  },
            onSaveClick = { _, _, _ -> },
            onDeleteContainerClick = {  },
            setLoading = { _ -> },
            setErrorMessage = { _ -> },
            containerName = "Akwarium",
            containerDescription = "leleleleisonrgjnbtehib",
            locationId = 1,
            locationsList = listOf(
                Location(1, "Sklep", ""),
                Location(2, "Zoo", "")
            ),
            isLoading = false,
            errorMessage = null
        )
    }
}