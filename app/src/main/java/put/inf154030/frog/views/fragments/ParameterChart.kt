package put.inf154030.frog.views.fragments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.models.Parameter
import put.inf154030.frog.models.ParameterHistoryEntry
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily

@Composable
fun ParameterChart(
    parameter: Parameter,
    historyData: List<ParameterHistoryEntry>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(MaterialTheme.colorScheme.primary),
    ) {
        Text(
            text = parameter.name,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.secondary
        )

        if (historyData.isNotEmpty()) {
            LineChart(
                data = historyData,
                parameter = parameter,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        } else {
            Text(
                text = "No data available",
                fontFamily = PoppinsFamily,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Preview
@Composable
fun ParameterChartPreview() {
    FrogTheme {
        ParameterChart(
            parameter = Parameter(5, "Temperatura wody", 25.0, "°C", 24.0, 28.0, true, "", "", "predefined"),
            historyData = listOf(
                ParameterHistoryEntry(21.5, "2025-05-14T10:00:00"),
                ParameterHistoryEntry(22.0, "2025-05-14T10:10:00"),
                ParameterHistoryEntry(22.3, "2025-05-14T10:20:00"),
                ParameterHistoryEntry(21.9, "2025-05-14T10:30:00"),
                ParameterHistoryEntry(21.7, "2025-05-14T10:40:00"),
                ParameterHistoryEntry(21.6, "2025-05-14T10:50:00"),
                ParameterHistoryEntry(21.8, "2025-05-14T11:00:00"),
                ParameterHistoryEntry(22.1, "2025-05-14T11:10:00"),
                ParameterHistoryEntry(22.4, "2025-05-14T11:20:00"),
                ParameterHistoryEntry(22.2, "2025-05-14T11:30:00")
            )
        )
    }
}