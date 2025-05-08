package put.inf154030.frog.schedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.fragments.BackButton
import put.inf154030.frog.fragments.TopHeaderBar
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily

class EditScheduleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FrogTheme {
                EditScheduleScreen(
                    onBackClick = { finish() },
                    onAddScheduleClick = { TODO() },
                    onSaveClick = { TODO() }
                )
            }
        }
    }
}

@Composable
fun EditScheduleScreen(
    onBackClick: () -> Unit,
    onAddScheduleClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            TopHeaderBar(
                title = "Edit Schedule",
            )
            BackButton { onBackClick() }
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

            }
            Column (
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = "-- Add Schedule --",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 16.sp,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clickable { onAddScheduleClick() }
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.size(32.dp))
                Button(
                    onClick = { onSaveClick() },
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                ) {
                    Text(
                        text = "Save",
                        fontFamily = PoppinsFamily
                    )
                }
                Spacer(modifier = Modifier.size(64.dp))
            }
        }
    }
}

@Preview
@Composable
fun EditScheduleActivityPreview() {
    FrogTheme {
        EditScheduleScreen(
            onBackClick = {},
            onAddScheduleClick = {},
            onSaveClick = {}
        )
    }
}