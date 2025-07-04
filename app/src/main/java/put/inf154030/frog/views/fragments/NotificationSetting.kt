package put.inf154030.frog.views.fragments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily

@Composable
fun NotificationSetting (
    isOn: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Powiadomienia",
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.secondary
        )
        Switch(
            checked = isOn,
            onCheckedChange = { onToggle(it) }
        )
    }
}

@Preview
@Composable
fun NotificationSettingPreview() {
    FrogTheme {
        NotificationSetting(
            isOn = true,
            onToggle = {}
        )
    }
}