package put.inf154030.frog.views.fragments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily

@Composable
fun ParameterItem(
    parameterName: String,
    currentValue: Double,
    unit: String,
) {
    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = parameterName,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            Row {
                Text(
                    text = currentValue.toString() + unit,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(vertical = 8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Preview
@Composable
fun ParameterItemPreview() {
    FrogTheme {
        ParameterItem(
            parameterName = "Temperature",
            currentValue = 25.0,
            unit = "°C"
        )
    }
}