package put.inf154030.frog.views.fragments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun NotificationCard (
    eventName: String,
    containerName: String,
    scheduledFor: String,
    onMarkAsReadClick: () -> Unit
) {
    val formattedDate = try {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val parsed = OffsetDateTime.parse(scheduledFor, formatter)
        val outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm")
        parsed.format(outputFormatter)
    } catch (e: Exception) {
        // Fallback to original string if parsing fails
        scheduledFor
    }

    Card (
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = eventName,
                    color = MaterialTheme.colorScheme.secondary,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "mark as read",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { onMarkAsReadClick() }
                )
            }
            Spacer(modifier = Modifier.size(16.dp))
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = containerName,
                    color = MaterialTheme.colorScheme.secondary,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = formattedDate,
                    color = MaterialTheme.colorScheme.secondary,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview
@Composable
fun NotificationCardPreview () {
    FrogTheme {
        NotificationCard(
            eventName = "Karmienie",
            containerName = "Terrarium Gekona",
            scheduledFor = "2025-03-23T18:00:00Z",
            onMarkAsReadClick = {}
        )
    }
}