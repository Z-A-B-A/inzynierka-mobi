package put.inf154030.frog.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import put.inf154030.frog.R

// Define color palette
private val ColorScheme = lightColorScheme(
    primary = Color(0xFF626C4D),
    secondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFFF2E3C6),
    background = Color(0xFF0D2606),
    surface = Color.White,
)

// Define font family
val PoppinsFamily = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_bold, FontWeight.Bold)
)

@Composable
fun FrogTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = ColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
