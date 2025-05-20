package put.inf154030.frog.views.fragments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily

@Composable
fun EditParameterRow(
    parameterId: Int,
    parameterName: String,
    parameterMin: Double,
    parameterMax: Double,
    onMinValueChanged: (Int, String) -> Unit,
    onMaxValueChanged: (Int, String) -> Unit
) {
    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = parameterName,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                var minValue by remember { mutableStateOf(parameterMin.toString()) }
                var maxValue by remember { mutableStateOf(parameterMax.toString()) }

                BasicTextField(
                    value = minValue,
                    onValueChange = { newValue ->
                        // Only accept numeric input with at most one decimal point
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                            minValue = newValue
                            onMinValueChanged(parameterId, newValue)
                        }
                    },
                    modifier = Modifier
                        .width(72.dp)
                        .size(24.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = PoppinsFamily,
                        textAlign = TextAlign.Center
                    ),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            innerTextField()
                        }
                    }
                )
                Spacer(modifier = Modifier.size(8.dp))
                BasicTextField(
                    value = maxValue,
                    onValueChange = { newValue ->
                        // Only accept numeric input with at most one decimal point
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                            maxValue = newValue
                            onMaxValueChanged(parameterId, newValue)
                        }
                    },
                    modifier = Modifier
                        .width(72.dp)
                        .size(24.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = PoppinsFamily,
                        textAlign = TextAlign.Center
                    ),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            innerTextField()
                        }
                    }
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Preview
@Composable
fun EditParameterRowPreview() {
    FrogTheme {
        EditParameterRow(
            parameterId = 1,
            parameterName = "Temperatura wody",
            parameterMin = 20.0,
            parameterMax = 25.0,
            onMaxValueChanged = { _, _ -> },
            onMinValueChanged = { _, _ -> }
        )
    }
}