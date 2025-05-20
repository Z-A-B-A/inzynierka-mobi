package put.inf154030.frog.views.activities.containers

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

class EditContainerActivity : ComponentActivity() {
    private lateinit var addLocationLauncher: ActivityResultLauncher<Intent>
    private var locationsList by mutableStateOf<List<Location>>(emptyList())
    private var isLoading by mutableStateOf(false)
    private var errorMessage by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val containerId = intent.getIntExtra("CONTAINER_ID", -1)
        val containerName = intent.getStringExtra("CONTAINER_NAME") ?: "Couldn't load container name"
        val containerDescription = intent.getStringExtra("CONTAINER_DESCRIPTION") ?: "Couldn't load container description"

        val locationId = intent.getIntExtra("LOCATION_ID", -1)

        addLocationLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            // When returning from AddLocationActivity, refresh the locations
            loadLocations()
        }

        setContent {
            FrogTheme {
                EditContainerScreen(
                    onBackClick = { finish() },
                    onSaveClick = { name, description, selectedLocation ->
                        val containerUpdateRequest = ContainerUpdateRequest(
                            name = name,
                            description = description,
                            active = true,
                            locationId = selectedLocation
                        )

                        ApiClient.apiService.updateContainer(containerId, containerUpdateRequest)
                            .enqueue(object : Callback<ContainerUpdateResponse> {
                                override fun onResponse(
                                    call: Call<ContainerUpdateResponse>,
                                    response: Response<ContainerUpdateResponse>
                                ) {
                                    if (response.isSuccessful) {
                                        // Container updated successfully
                                        setResult(RESULT_OK)
                                        finish()
                                    } else {
                                        // Handle error
                                        errorMessage = "Failed to update container: ${response.message()}"
                                    }
                                }

                                override fun onFailure(call: Call<ContainerUpdateResponse>, t: Throwable) {
                                    errorMessage = "Network error: ${t.message}"
                                }
                            })
                    },
                    onDeleteContainerClick = {
                        val intent = Intent(this, DeleteContainerActivity::class.java)
                        intent.putExtra("CONTAINER_ID", containerId)
                        startActivity(intent)
                        finish()
                    },
                    containerName = containerName,
                    containerDescription = containerDescription,
                    locationsList = locationsList,
                    locationId = locationId
                )
            }
        }
        // Load locations when activity is created
        loadLocations()
    }
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

@Composable
fun EditContainerScreen(
    onBackClick: () -> Unit,
    onSaveClick: (String, String, Int) -> Unit,
    onDeleteContainerClick: () -> Unit,
    containerName: String,
    containerDescription: String,
    locationId: Int,
    locationsList: List<Location>
) {
    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            TopHeaderBar(
                title = "Edit Container"
            )
            BackButton { onBackClick() }
            Spacer(modifier = Modifier.size(16.dp))

            var errorMessage by remember { mutableStateOf<String?>(null) }

            var name by remember { mutableStateOf(containerName) }
            var description by remember { mutableStateOf(containerDescription) }
            var errorMessageName by remember { mutableStateOf<String?>(null) }
            var errorMessageDescription by remember { mutableStateOf<String?>(null) }

            // State for location selection
            var isLocationDropdownExpanded by remember { mutableStateOf(false) }
            var locations by remember { mutableStateOf(listOf<Location>()) }
            var selectedLocation by remember { mutableStateOf<Location?>(null) }
            var isLoading by remember { mutableStateOf(true) }

            // Load locations when the screen is created
            LaunchedEffect(locationsList) {
                isLoading = false

                // Find the current location in the locations list
                val currentLocation = locationsList.find { it.id == locationId }

                // Set the current location as selected if found, otherwise use the first location
                selectedLocation = currentLocation ?: locationsList.firstOrNull()

                // No need to sort locations - just display them as they come from the API
                locations = locationsList
            }

            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                // Error message
                errorMessageName?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))
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
                errorMessageDescription?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))
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
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                Button(
                    onClick = {
                        if (name.isBlank()) {
                            errorMessageName = "Name cannot be empty"
                        } else if (selectedLocation == null) {
                            errorMessage = "Please select a location"
                        } else {
                            onSaveClick(name, description, selectedLocation!!.id)
                        }
                    },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(0.65f),
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

@Preview
@Composable
fun EditContainerActivityPreview () {
    FrogTheme {
        EditContainerScreen(
            onBackClick = {  },
            onSaveClick = { _, _, _ -> },
            onDeleteContainerClick = {  },
            containerName = "Akwarium",
            containerDescription = "leleleleisonrgjnbtehib",
            locationId = 1,
            locationsList = listOf(
                Location(1, "Sklep", ""),
                Location(2, "Zoo", "")
            )
        )
    }
}