package put.inf154030.frog.views.activities.locations

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import put.inf154030.frog.R
import put.inf154030.frog.views.fragments.LocationCard
import put.inf154030.frog.views.fragments.SideMenu
import put.inf154030.frog.views.fragments.TopNavigationBar
import put.inf154030.frog.models.Location
import put.inf154030.frog.models.responses.LocationsResponse
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.network.SessionManager
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationsActivity : ComponentActivity() {
    private lateinit var addLocationLauncher: ActivityResultLauncher<Intent>
    private var locationsList by mutableStateOf<List<Location>>(emptyList())
    private var isLoading by mutableStateOf(false)
    private var errorMessage by mutableStateOf<String?>(null)
    private val userName = SessionManager.getUserName()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize activity result launcher
        addLocationLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            // When returning from AddLocationActivity, refresh the locations
            loadLocations()
        }

        setContent {
            FrogTheme {
                LocationsScreen(
                    userName = userName,
                    locations = locationsList,
                    isLoading = isLoading,
                    errorMessage = errorMessage,
                    onAddLocationClick = {
                        val intent = Intent(this, AddLocationActivity::class.java)
                        addLocationLauncher.launch(intent)
                    },
                    onLocationClick = { location ->
                        val intent = Intent(this, LocationActivity::class.java)
                        intent.putExtra("LOCATION_ID", location.id)
                        intent.putExtra("LOCATION_NAME", location.name)
                        startActivity(intent)
                    },
                    onEditClick = { location ->
                        val intent = Intent(this, EditLocationActivity::class.java)
                        intent.putExtra("LOCATION_ID", location.id)
                        startActivity(intent)
                    },
                    context = this
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
fun LocationsScreen(
    userName: String? = "XYZ",
    locations: List<Location> = emptyList(),
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onAddLocationClick: () -> Unit,
    onLocationClick: (Location) -> Unit,
    onEditClick: (Location) -> Unit,
    context: Context
) {
    var showMenu by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopNavigationBar(
                    title = "Hi, $userName!",
                    onMenuClick = { showMenu = !showMenu }
                )

                // Show loading indicator if needed
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                    }
                }
                // Show error message if any
                else if (errorMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                // This is the scrollable content
                if (locations.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No locations added yet",
                            color = MaterialTheme.colorScheme.secondary,
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        items(locations) { location ->
                            LocationCard(
                                locationName = location.name,
                                onEditClick = { onEditClick(location) },
                                onClick = { onLocationClick(location) }
                            )
                        }
                    }
                }
            }

            // Floating action button
            IconButton(
                onClick = { onAddLocationClick() },
                modifier = Modifier
                    .padding(bottom = 48.dp, end = 32.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add_buton),
                    contentDescription = "Add new location",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        SideMenu(
            isVisible = showMenu,
            onDismiss = { showMenu = false },
            context = context
        )
    }
}

@Preview
@Composable
fun LocationsActivityPreview () {
    val context = androidx.compose.ui.platform.LocalContext.current
    FrogTheme {
        LocationsScreen(
            userName = "Bartosz",
            locations = listOf(
                Location(1, "Sklep", null, ""),
                Location(2, "Zoo", null, "")
            ),
            isLoading = false,
            errorMessage = null,
            onAddLocationClick = {},
            onLocationClick = { _ -> },
            onEditClick = { _ -> },
            context = context
        )
    }
}