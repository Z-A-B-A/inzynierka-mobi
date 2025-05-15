package put.inf154030.frog.views.fragments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.R
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily

@Composable
fun ScheduleItem (
    onEditClick: () -> Unit,
    scheduleName: String,
    frequency: String,
    weekDays: String,
    executionTime: String
) {
    // Parse the ISO 8601 timestamp to extract only HH:mm
    val formattedTime = try {
        // Extract the time part from the ISO string
        // Format is like "0000-01-01T03:25:00Z", we want just "03:25"
        val timePattern = "\\d{4}-\\d{2}-\\d{2}T(\\d{2}:\\d{2}):\\d{2}Z".toRegex()
        val matchResult = timePattern.find(executionTime)
        matchResult?.groupValues?.get(1) ?: executionTime
    } catch (e: Exception) {
        // Fallback to original string if parsing fails
        executionTime
    }

    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box (
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = scheduleName,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.align(Alignment.Center)
            )
            IconButton(
                onClick = onEditClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = "Edit container",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.secondary
        )
        if (frequency == "daily") {
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formattedTime,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = frequency,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        } else {
            val dayLabels = listOf("Mondays", "Tuesdays", "Wednesdays", "Thursdays", "Fridays", "Saturdays", "Sundays")
            val days = weekDays.split(",").map { it.toInt() }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                days.forEach { day ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${dayLabels[day - 1]} at $formattedTime",
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = frequency,
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ScheduleItemPreview () {
    FrogTheme {
        ScheduleItem(
            onEditClick = {},
            scheduleName = "Feeding",
            frequency = "weekly",
            weekDays = "1,3,5",
            executionTime = "0000-01-01T07:15:00Z"
        )
    }
}