package put.inf154030.frog.views.activities.locations

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
import androidx.compose.foundation.layout.Spacer
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
import put.inf154030.frog.models.Container
import put.inf154030.frog.repository.ContainersRepository
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import put.inf154030.frog.views.activities.containers.AddContainerActivity
import put.inf154030.frog.views.activities.containers.ContainerActivity
import put.inf154030.frog.views.activities.containers.EditContainerActivity
import put.inf154030.frog.views.fragments.BackButton
import put.inf154030.frog.views.fragments.ContainerCard
import put.inf154030.frog.views.fragments.FilterButtonsRow
import put.inf154030.frog.views.fragments.SideMenu
import put.inf154030.frog.views.fragments.TopNavigationBar

// Activity for displaying and managing containers in a location
class LocationActivity : ComponentActivity() {
    // Launcher for add/edit container activities
    private lateinit var containerLauncher: ActivityResultLauncher<Intent>
    // List of containers for this location
    private var containersList by mutableStateOf<List<Container>>(emptyList())
    // Loading and error state
    private var isLoading by mutableStateOf(false)
    private var errorMessage by mutableStateOf<String?>(null)
    // Location info
    private var locationId: Int = -1
    private var locationName: String = "Location"

    private val containersRepository = ContainersRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get location info from intent
        locationId = intent.getIntExtra("LOCATION_ID", -1)
        locationName = intent.getStringExtra("LOCATION_NAME") ?: "Location"

        // Initialize activity result launcher for add/edit container
        containerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            loadContainers() // Refresh containers after add/edit
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
                        // Launch add container activity
                        val intent = Intent(this, AddContainerActivity::class.java)
                        intent.putExtra("LOCATION_ID", locationId)
                        containerLauncher.launch(intent)
                    },
                    onContainerClick = { container ->
                        // Open container details
                        val intent = Intent(this, ContainerActivity::class.java)
                        intent.putExtra("CONTAINER_ID", container.id)
                        intent.putExtra("CONTAINER_NAME", container.name)
                        intent.putExtra("CONTAINER_DESCRIPTION", container.description)
                        startActivity(intent)
                    },
                    onEditClick = { container ->
                        // Launch edit container activity
                        val intent = Intent(this, EditContainerActivity::class.java)
                        intent.putExtra("CONTAINER_ID", container.id)
                        intent.putExtra("CONTAINER_NAME", container.name)
                        intent.putExtra("CONTAINER_DESCRIPTION", container.description)
                        intent.putExtra("LOCATION_ID", locationId)
                        containerLauncher.launch(intent)
                    }
                )
            }
        }
        // Load locations when activity is created
        loadContainers()
    }

    // Fetch containers for this location from API
    private fun loadContainers() {
        if (locationId == -1) {
            errorMessage = "Invalid location ID"
            return
        }

        isLoading = true
        errorMessage = null

        containersRepository.getContainers(
            locationId,
            onResult = { success, containers, error ->
                isLoading = false
                errorMessage = error
                if (success && !containers.isNullOrEmpty()) containersList = containers
            }
        )
    }
}

// Composable for the main location screen UI
@Composable
fun LocationScreen(
    locationName: String,
    containers: List<Container>,
    isLoading: Boolean,
    errorMessage: String?,
    onBackClick: () -> Unit,
    onAddContainerClick: () -> Unit,
    onContainerClick: (Container) -> Unit,
    onEditClick: (Container) -> Unit
) {
    // State for showing the side menu and selected filter
    var showMenu by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("all") }

    // Filter containers based on selected type
    val filteredContainers = when (selectedFilter) {
        "aquariums" -> containers.filter { it.type.lowercase() == "aquarium" }
        "terrariums" -> containers.filter { it.type.lowercase() == "terrarium" }
        else -> containers // "all" or any other case
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top navigation bar with menu button
            TopNavigationBar(
                title = locationName,
                onMenuClick = { showMenu = !showMenu }
            )
            // Back button
            Box (
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                BackButton { onBackClick() }
            }

            // Filter buttons for container types
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

            // Show empty state if no containers
            else if (filteredContainers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selectedFilter == "all") "No containers added yet"
                        else "No ${selectedFilter}s in this location",
                        color = MaterialTheme.colorScheme.secondary,
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                // List of containers
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
            Spacer(modifier = Modifier.size(16.dp))
            Box (
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Floating action button for adding a container
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
        }

        // Side menu overlay
        SideMenu(
            isVisible = showMenu,
            onDismiss = { showMenu = false }
        )
    }
}

// Preview for Compose UI
@Preview
@Composable
fun LocationActivityPreview () {
    FrogTheme {
        LocationScreen(
            locationName = "Lokacja 1",
            containers = listOf(
                Container(1, "ASDF1", "aquarium", null, true, ""),
                Container(2, "ASDF2", "terrarium", null, true, ""),
                Container(3, "ASDF3", "aquarium", null, true, ""),
                Container(4, "ASDF4", "terrarium", null, true, "")
            ),
            isLoading = false,
            errorMessage = null,
            onBackClick = {},
            onAddContainerClick = {},
            onContainerClick = { _ -> },
            onEditClick = { _ -> }
        )
    }
}