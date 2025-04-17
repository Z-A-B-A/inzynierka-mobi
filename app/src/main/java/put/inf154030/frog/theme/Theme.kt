package put.inf154030.frog.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Define your color palette
private val ColorScheme = lightColorScheme(
    primary = Color(0xFF626C4D),
    secondary = Color(0xFFFFFFFF),
    background = Color(0xFF0D2606), // Light background
    surface = Color.White,
)

@Composable
fun FrogTheme(
    darkTheme: Boolean = false, // You can use isSystemInDarkTheme() for system theme
    content: @Composable () -> Unit
) {
    val colorScheme = ColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}