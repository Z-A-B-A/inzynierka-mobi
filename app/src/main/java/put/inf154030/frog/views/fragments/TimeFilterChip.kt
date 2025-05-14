package put.inf154030.frog.views.fragments

import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily

@Composable
fun TimeFilterChip(
    value: String,
    label: String,
    selectedTimeframe: String,
    onTimeframeSelected: (String) -> Unit
) {
    FilterChip(
        selected = selectedTimeframe == value,
        onClick = { onTimeframeSelected(value) },
        label = {
            Text(
                text = label,
                fontFamily = PoppinsFamily,
                fontSize = 14.sp
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.secondary,
            containerColor = MaterialTheme.colorScheme.secondary,
            labelColor = MaterialTheme.colorScheme.background
        )
    )
}

@Preview
@Composable
fun TimeFilterChipPreview () {
    FrogTheme {
        TimeFilterChip(
            value = "1h",
            label = "Last Hour",
            selectedTimeframe = "1h",
            onTimeframeSelected = {}
        )
    }
}