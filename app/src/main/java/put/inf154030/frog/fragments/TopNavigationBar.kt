package put.inf154030.frog.fragments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigationBar(
    title: String,
    onMenuClick: () -> Unit
) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = title,
                    fontSize = 28.sp,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                )
            }
        },
        actions = {
            Box(
                modifier = Modifier.fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .fillMaxHeight()
                            .size(48.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.secondary
        ),
        modifier = Modifier
            .background(
                brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0f to MaterialTheme.colorScheme.primary,
                        0.85f to MaterialTheme.colorScheme.primary,
                        1f to MaterialTheme.colorScheme.background
                    )
                )
            )
            .height(128.dp)
    )
}

@Preview
@Composable
fun TopNavigationBarPreview() {
    FrogTheme {
        TopNavigationBar(
            title = "Locations",
            onMenuClick = {}
        )
    }
}