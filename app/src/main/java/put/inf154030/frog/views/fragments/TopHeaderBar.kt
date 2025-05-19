package put.inf154030.frog.views.fragments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily

@Composable
fun TopHeaderBar(
    title: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(156.dp)
            .background(
                brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0f to MaterialTheme.colorScheme.primary,
                        0.85f to MaterialTheme.colorScheme.primary,
                        1f to MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.secondary,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Bold,
            fontSize = if (title.length <= 16) 28.sp else 24.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)
        )
    }
}

@Preview
@Composable
fun TopHeaderBarPreview() {
    FrogTheme {
        TopHeaderBar(
            title = "arsfaerviubheut vnjmcewanfvuyteb"
        )
    }
}