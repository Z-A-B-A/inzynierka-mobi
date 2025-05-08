package put.inf154030.frog.containers

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import put.inf154030.frog.fragments.BackButton
import put.inf154030.frog.fragments.TopHeaderBar
import put.inf154030.frog.schedule.ScheduleActivity
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily

class ContainerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val containerId = intent.getIntExtra("CONTAINER_ID", -1)
        val containerName = intent.getStringExtra("CONTAINER_NAME") ?: "ERROR READING NAME"
        val containerDescription = intent.getStringExtra("CONTAINER_DESCRIPTION") ?: "ERROR READING DESCRIPTION"

        setContent {
            FrogTheme {
                ContainerScreen(
                    onBackClick = { finish() },
                    onChangeClick = {
                        val intent = Intent(this, ManageContainerActivity::class.java)
                        intent.putExtra("CONTAINER_ID", containerId)
                        intent.putExtra("CONTAINER_NAME", containerName)
                        startActivity(intent)
                    },
                    onScheduleClick = {
                        val intent = Intent(this, ScheduleActivity::class.java)
                        intent.putExtra("CONTAINER_ID", containerId)
                        startActivity(intent)
                    },
                    containerName = containerName,
                    containerDescription = containerDescription
                )
            }
        }
    }
}

@Composable
fun ContainerScreen (
    onBackClick: () -> Unit,
    onChangeClick: () -> Unit,
    onScheduleClick: () -> Unit,
    containerName: String,
    containerDescription: String
) {
    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // TODO("Pozmieniać layout na wzór ManageContainerActivity")
        Column {
            TopHeaderBar(
                title = containerName,
            )
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BackButton { onBackClick() }
                Text(
                    text = "schedule >>>",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 20.sp,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { onScheduleClick() }
                        .padding(16.dp)
                )
            }
            Column (
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "-- description --",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = containerDescription,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Thin,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.size(8.dp))
//                TODO("Mam plan taki, żeby zrobić tutaj 3 pola:" +
//                        "parametry, statystyki, zwierzęta." +
//                        "jak klikasz na daną opcję to się rozwija widok z danymi")
                Spacer(modifier = Modifier.size(16.dp))
                Column (
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Button(
                        onClick = { onChangeClick() },
                        modifier = Modifier
                            .fillMaxWidth(0.65f)
                    ) {
                        Text(
                            text = "Change",
                            fontFamily = PoppinsFamily
                        )
                    }
                    Spacer(modifier = Modifier.size(64.dp))
                }
            }
        }
    }
}

@Preview
@Composable
fun ContainerActivityPreview () {
    FrogTheme {
        ContainerScreen(
            onBackClick = {  },
            onChangeClick = {  },
            onScheduleClick = {  },
            containerName = "Container X",
            containerDescription = "Potężny kontener na bycze ryby"
        )
    }
}