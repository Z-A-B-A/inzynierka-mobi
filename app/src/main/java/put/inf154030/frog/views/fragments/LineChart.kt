package put.inf154030.frog.views.fragments

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.models.Parameter
import put.inf154030.frog.models.ParameterHistoryEntry
import put.inf154030.frog.theme.FrogTheme
import java.util.Locale

@Composable
fun LineChart(
    data: List<ParameterHistoryEntry>,
    parameter: Parameter,
    modifier: Modifier = Modifier
) {
    // Capture theme colors at the Composable level
    val secondaryColor = MaterialTheme.colorScheme.secondary

    val values = data.map { it.value }
    val minValue = values.minOrNull() ?: 0.0
    val maxValue = values.maxOrNull() ?: 0.0
    val range = (maxValue - minValue).coerceAtLeast(0.1) // Avoid division by zero

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val padding = 40f

        val chartWidth = canvasWidth - 2 * padding
        val chartHeight = canvasHeight - 2 * padding

        // Draw axes
        drawLine(
            color = secondaryColor,
            start = Offset(padding, padding),
            end = Offset(padding, canvasHeight - padding),
            strokeWidth = 2f
        )
        drawLine(
            color = secondaryColor,
            start = Offset(padding, canvasHeight - padding),
            end = Offset(canvasWidth - padding, canvasHeight - padding),
            strokeWidth = 2f
        )

        // Draw data points and lines
        if (data.size > 1) {
            val path = Path()
            var isFirstPoint = true

            data.forEachIndexed { index, point ->
                val x = padding + (index.toFloat() / (data.size - 1)) * chartWidth
                val normalizedValue = ((point.value - minValue) / range).coerceIn(0.0, 1.0)
                val y = (canvasHeight - padding) - (normalizedValue * chartHeight).toFloat()

                if (isFirstPoint) {
                    path.moveTo(x, y)
                    isFirstPoint = false
                } else {
                    path.lineTo(x, y)
                }

                // Draw point
                drawCircle(
                    color = Color.Red,
                    radius = 6f,
                    center = Offset(x, y)
                )
            }

            // Draw line connecting points
            drawPath(
                path = path,
                color = Color.Red,
                style = Stroke(width = 3f)
            )

            // Draw min, max values as text
            drawContext.canvas.nativeCanvas.let { nativeCanvas ->
                val textPaint = Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 12.sp.toPx()
                }

                nativeCanvas.drawText(
                    String.format(Locale.US, "%.1f %s", maxValue, parameter.unit),
                    padding + 10,
                    padding + 25,
                    textPaint
                )

                nativeCanvas.drawText(
                    String.format(Locale.US, "%.1f %s", minValue, parameter.unit),
                    padding + 10,
                    canvasHeight - padding + 30,
                    textPaint
                )
            }
        }
    }
}

@Preview
@Composable
fun LineChartPreview() {
    FrogTheme {
        LineChart(
            data = listOf(
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
            ),
            parameter = Parameter("Temperatura wody", 25.0, "°C", 24.0, 28.0, true, "", "", 1),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}