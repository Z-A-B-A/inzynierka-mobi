package put.inf154030.frog.views.activities.containers

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.repository.ContainersRepository
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import put.inf154030.frog.views.fragments.TopHeaderBar

// Activity for confirming and executing container deletion
class DeleteContainerActivity : ComponentActivity() {
    // State for loading and error message
    private var isLoading by mutableStateOf(false)
    private var errorMessage by mutableStateOf<String?>(null)
    private val containersRepository = ContainersRepository()

    override fun onCreate(savedInstanceState: Bundle?) {

        val containerId = intent.getIntExtra("CONTAINER_ID", -1)

        super.onCreate(savedInstanceState)
        setContent {
            FrogTheme {
                // Main confirmation screen
                DeleteContainerScreen(
                    onYesClick = {
                        errorMessage = null
                        isLoading = true

                        containersRepository.deleteContainer(
                            containerId,
                            onResult = { success, error ->
                                isLoading = false
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
fun DeleteContainerScreen(
    onYesClick: () -> Unit,
    onNoClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?
) {
    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Show loading spinner if deleting
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.testTag("CircularProgressIndicator"))
            }
        }
        Column {
            TopHeaderBar(title = "Usuń Pojemnik")
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
                        fontFamily = PoppinsFamily,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
                // Confirmation question
                Text(
                    text = "Czy na pewno?",
                    color = MaterialTheme.colorScheme.secondary,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                )
                // Warning about irreversibility
                Text(
                    text = "Ta operacja jest nieodwracalna",
                    color = MaterialTheme.colorScheme.secondary,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
                Spacer(modifier = Modifier.size(64.dp))
                // Yes/No buttons
                Row {
                    Button(
                        modifier = Modifier.width((128.dp)),
                        onClick = { onYesClick() },
                        enabled = !isLoading // Disable while loading
                    ) {
                        Text(
                            text = "Tak",
                            fontFamily = PoppinsFamily
                        )
                    }
                    Spacer(modifier = Modifier.size(32.dp))
                    Button(
                        modifier = Modifier.width(128.dp),
                        onClick = { onNoClick() }
                    ) {
                        Text(
                            text = "Nie",
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
fun DeleteContainerActivityPreview () {
    FrogTheme {
        DeleteContainerScreen(
            onYesClick = {  },
            onNoClick = {  },
            isLoading = false,
            errorMessage = null
        )
    }
}