package put.inf154030.frog.views.activities.locations

import android.Manifest
import android.content.Intent
import android.os.Build
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
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import put.inf154030.frog.R
import put.inf154030.frog.models.Location
import put.inf154030.frog.network.SessionManager
import put.inf154030.frog.repository.LocationsRepository
import put.inf154030.frog.services.FrogFirebaseMessagingService
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import put.inf154030.frog.utils.dataStore
import put.inf154030.frog.views.activities.notifications.NotificationsActivity
import put.inf154030.frog.views.fragments.LocationCard
import put.inf154030.frog.views.fragments.SideMenu
import put.inf154030.frog.views.fragments.TopNavigationBar

// Activity for displaying and managing all locations
class LocationsActivity : ComponentActivity() {
    // Launcher for add/edit location activities
    private lateinit var locationLauncher: ActivityResultLauncher<Intent>
    // List of locations
    private var locationsList by mutableStateOf<List<Location>>(emptyList())
    // Loading and error state
    private var isLoading by mutableStateOf(false)
    private var errorMessage by mutableStateOf<String?>(null)
    // User name for greeting
    private var userName = SessionManager.getUserName()

    private val locationsRepository = LocationsRepository()

    /* 
     * Permission launcher for notifications (Android 13+)
     * For Android 13 and above, we need to request permission to post notifications.
     * For earlier versions, this is not required.
    */
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Save the permission status to preferences
        lifecycleScope.launch {
            applicationContext.dataStore.updateData { preferences ->
                val mutablePreferences = preferences.toMutablePreferences()
                mutablePreferences[FrogFirebaseMessagingService.NOTIFICATIONS_ENABLED_KEY] = isGranted
                mutablePreferences
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fromNotification = intent.getBooleanExtra("FROM_NOTIFICATION", false)
        if (fromNotification) {
            val intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)
        }

        // Request notification permission if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        // Initialize activity result launcher for add/edit location
        locationLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            // Refresh locations after add/edit
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
                        // Launch add location activity
                        val intent = Intent(this, AddLocationActivity::class.java)
                        locationLauncher.launch(intent)
                    },
                    onLocationClick = { location ->
                        // Open location details
                        val intent = Intent(this, LocationActivity::class.java)
                        intent.putExtra("LOCATION_ID", location.id)
                        intent.putExtra("LOCATION_NAME", location.name)
                        startActivity(intent)
                    },
                    onEditClick = { location ->
                        // Launch edit location activity
                        val intent = Intent(this, EditLocationActivity::class.java)
                        intent.putExtra("LOCATION_ID", location.id)
                        locationLauncher.launch(intent)
                    }
                )
            }
        }
        // Load locations when activity is created
        loadLocations()
    }

    override fun onResume() {
        super.onResume()
        // Refresh locations when returning to this activity
        loadLocations()
        userName = SessionManager.getUserName()
    }

    private fun loadLocations() {
        isLoading = true
        errorMessage = null

        locationsRepository.getLocations(
            onResult = { success, locations, error ->
                if (success && !locations.isNullOrEmpty()) locationsList = locations
                isLoading = false
                errorMessage = error
            }
        )
    }
}

// Composable for the main locations screen UI
@Composable
fun LocationsScreen(
    userName: String?,
    locations: List<Location> = emptyList(),
    isLoading: Boolean,
    errorMessage: String?,
    onAddLocationClick: () -> Unit,
    onLocationClick: (Location) -> Unit,
    onEditClick: (Location) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
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

            // Show empty state if no locations
            if (locations.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tap + to add your first location",
                        color = MaterialTheme.colorScheme.secondary,
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                // List of locations
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
            Spacer(modifier = Modifier.size(16.dp))
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Floating action button for adding a location
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
fun LocationsActivityPreview () {
    FrogTheme {
        LocationsScreen(
            userName = "Bartosz",
            locations = listOf(
                Location(1, "Sklep1", ""),
                Location(2, "Zoo1", ""),
                Location(3, "Sklep2", ""),
                Location(4, "Zoo2", ""),
                Location(5, "Sklep3", ""),
                Location(6, "Zoo3", ""),
                Location(7, "Sklep4", ""),
                Location(8, "Zoo4", "")
            ),
            isLoading = false,
            errorMessage = null,
            onAddLocationClick = {},
            onLocationClick = { _ -> },
            onEditClick = { _ -> }
        )
    }
}