package put.inf154030.frog.views.activities.locations

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.repository.LocationsRepository
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import put.inf154030.frog.views.fragments.TopHeaderBar

// Activity for confirming and executing location deletion
class DeleteLocationActivity : ComponentActivity() {
    // State for loading and error message
    private var isLoading by mutableStateOf(false)
    private var errorMessage by mutableStateOf<String?>(null)
    private val locationsRepository = LocationsRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        
        // Get the location ID from intent
        val locationId = intent.getIntExtra("LOCATION_ID", -1)

        super.onCreate(savedInstanceState)
        setContent {
            FrogTheme {
                DeleteLocationScreen(
                    onYesClick = { 
                        isLoading = true
                        errorMessage = null
                        // Call API to delete location
                        locationsRepository.deleteLocation(
                            locationId,
                            onResult = { success, loading, error ->
                                isLoading = loading
                                errorMessage = error
                                if (success) finish()
                            }
                        )
                     },
                    onNoClick = { finish() }, // Cancel and close activity
                    isLoading = isLoading,
                    errorMessage = errorMessage
                )
            }
        }
    }
}

// Composable for the delete confirmation UI
@Composable
fun DeleteLocationScreen (
    onYesClick: () -> Unit,
    onNoClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?
) {
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
            TopHeaderBar(title = "Delete Location")
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Show error message if present
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                // Confirmation question
                Text(
                    text = "Are you sure?",
                    color = MaterialTheme.colorScheme.secondary,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                )
                // Warning about irreversibility
                Text(
                    text = "Note: This operation deletes the location along with all containers, parameters, schedules, notifications and other related data",
                    color = MaterialTheme.colorScheme.secondary,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.size(32.dp))
                // Yes/No buttons
                Row {
                    Button(
                        modifier = Modifier.width(128.dp),
                        onClick = { onYesClick() },
                        enabled = !isLoading // Disable while loading
                    ) {
                        Text(
                            text = "Yes",
                            fontFamily = PoppinsFamily
                        )
                    }
                    Spacer(modifier = Modifier.size(32.dp))
                    Button(
                        modifier = Modifier.width(128.dp),
                        onClick = { onNoClick() }
                    ) {
                        Text(
                            text = "No",
                            fontFamily = PoppinsFamily
                        )
                    }
                }
                Spacer(modifier = Modifier.size(64.dp))
            }
        }
    }
}

// Preview for Compose UI
@Preview
@Composable
fun DeleteLocationPreview() {
    FrogTheme {
        DeleteLocationScreen(
            onYesClick = {  },
            onNoClick = {  },
            isLoading = false,
            errorMessage = null
        )
    }
}