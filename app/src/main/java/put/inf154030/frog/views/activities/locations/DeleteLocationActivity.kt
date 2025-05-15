package put.inf154030.frog.views.activities.locations

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.views.fragments.TopHeaderBar
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily

class DeleteLocationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val locationId = intent.getIntExtra("LOCATION_ID", -1)

        super.onCreate(savedInstanceState)
        setContent {
            FrogTheme {
                DeleteLocationScreen(
                    onYesClick = { finish() },
                    onNoClick = { finish() }
                )
            }
        }
    }
}

@Composable
fun DeleteLocationScreen (
    onYesClick: () -> Unit,
    onNoClick: () -> Unit
) {
    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            TopHeaderBar(
                title = "Delete Location"
            )
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Are you sure?",
                    color = MaterialTheme.colorScheme.secondary,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                )
                Text(
                    text = "This operation can not be undone",
                    color = MaterialTheme.colorScheme.secondary,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
                Spacer(modifier = Modifier.size(64.dp))
                Row {
                    Button(
                        modifier = Modifier.width(128.dp),
                        onClick = {
                            onYesClick()
                            TODO("Waiting for API request")
                        }
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

@Preview
@Composable
fun DeleteLocationPreview() {
    FrogTheme {
        DeleteLocationScreen(
            onYesClick = {  },
            onNoClick = {  }
        )
    }
}