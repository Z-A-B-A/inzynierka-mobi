package put.inf154030.frog.fragments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily

@Composable
fun BackButton (
    onClick: () -> Unit
) {
    Text(
        text = "<<< back",
        color = MaterialTheme.colorScheme.secondary,
        fontSize = 20.sp,
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clickable { onClick() }
            .padding(16.dp)
    )
}

@Preview
@Composable
fun BackButtonPreview() {
    FrogTheme {
        BackButton(
            onClick = {  }
        )
    }
}