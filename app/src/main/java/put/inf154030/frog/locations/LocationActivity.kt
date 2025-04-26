package put.inf154030.frog.locations

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
import put.inf154030.frog.containers.AddContainerActivity
import put.inf154030.frog.fragments.BackButton
import put.inf154030.frog.fragments.ContainerCard
import put.inf154030.frog.fragments.FilterButtonsRow
import put.inf154030.frog.fragments.SideMenu
import put.inf154030.frog.fragments.TopNavigationBar
import put.inf154030.frog.models.Container
import put.inf154030.frog.models.responses.ContainersResponse
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationActivity : ComponentActivity() {
    private lateinit var addContainerLauncher: ActivityResultLauncher<Intent>
    private var containersList by mutableStateOf<List<Container>>(emptyList())
    private var isLoading by mutableStateOf(false)
    private var errorMessage by mutableStateOf<String?>(null)
    private var locationId: Int = -1
    private var locationName: String = "Location"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationId = intent.getIntExtra("LOCATION_ID", -1)
        locationName = intent.getStringExtra("LOCATION_NAME") ?: "Location"

        // Initialize activity result launcher
        addContainerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            loadContainers()
        }

        setContent {
            FrogTheme {
                LocationScreen(
                    locationName = locationName,
                    containers = containersList,
                    isLoading = isLoading,
                    errorMessage = errorMessage,
                    onBackClick = { finish() },
                    onAddContainerClick = {
                        val intent = Intent(this, AddContainerActivity::class.java)
                        intent.putExtra("LOCATION_ID", locationId)
                        addContainerLauncher.launch(intent)
                    },
                    onContainerClick = { container ->
//                        val intent = Intent(this, ContainerDetailActivity::class.java)
//                        intent.putExtra("CONTAINER_ID", container.id)
//                        startActivity(intent)
                    },
                    onEditClick = { container ->
//                        val intent = Intent(this, EditContainerActivity::class.java)
//                        intent.putExtra("CONTAINER_ID", container.id)
//                        addContainerLauncher.launch(intent)
                    }
                )
            }
        }
        // Load locations when activity is created
        loadContainers()
    }

    private fun loadContainers() {
        if (locationId == -1) {
            errorMessage = "Invalid location ID"
            return
        }

        isLoading = true
        errorMessage = null

        ApiClient.apiService.getContainers(locationId).enqueue(object : Callback<ContainersResponse> {
            override fun onResponse(
                call: Call<ContainersResponse>,
                response: Response<ContainersResponse>
            ) {
                isLoading = false
                if (response.isSuccessful) {
                    containersList = response.body()?.containers ?: emptyList()
                } else {
                    errorMessage = "Failed to load locations: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<ContainersResponse>, t: Throwable) {
                isLoading = false
                errorMessage = "Network error: ${t.message}"
            }

        })
    }
}

@Composable
fun LocationScreen(
    locationName: String,
    containers: List<Container> = emptyList(),
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onBackClick: () -> Unit = {},
    onAddContainerClick: () -> Unit = {},
    onContainerClick: (Container) -> Unit = {},
    onEditClick: (Container) -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("all") }

    val filteredContainers = when (selectedFilter) {
        "aquariums" -> containers.filter { it.type.lowercase() == "aquarium" }
        "terrariums" -> containers.filter { it.type.lowercase() == "terrarium" }
        else -> containers // "all" or any other case
    }

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
                    title = locationName,
                    onMenuClick = { showMenu = !showMenu }
                )
                Box (
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BackButton { onBackClick() }
                }

                FilterButtonsRow(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { filter -> selectedFilter = filter }
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
                else if (filteredContainers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (containers.isEmpty()) "No containers added yet"
                            else "No ${selectedFilter.dropLast(1)}s in this location",
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
                        items(filteredContainers) { container ->
                            ContainerCard(
                                containerName = container.name,
                                containerType = container.type,
                                onEditClick = { onEditClick(container) },
                                onClick = { onContainerClick(container) }
                            )
                        }
                    }
                }
            }

            // Floating action button
            IconButton(
                onClick = onAddContainerClick,
                modifier = Modifier
                    .padding(bottom = 48.dp, end = 32.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add_buton),
                    contentDescription = "Add new container",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        SideMenu(
            isVisible = showMenu,
            onDismiss = { showMenu = false }
        )
    }
}

@Preview
@Composable
fun LocationActivityPreview() {
    FrogTheme {
        LocationScreen(
            locationName = "Shop",
            containers = listOf(
                Container(1, "Aquarium", "aquarium", "", true, ""),
                Container(2, "Terrarium", "terrarium", "", true, "")
            ),
            isLoading = false,
            errorMessage = null,
            onBackClick = {  },
            onContainerClick = {  },
            onAddContainerClick = {  },
            onEditClick = {  }
        )
    }
}