package put.inf154030.frog.views.fragments

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily

@Composable
fun FilterButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.width(110.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background
        ),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.secondary
        )
    ) {
        Text(
            text = text,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun FilterButtonPreview() {
    FrogTheme {
        FilterButton(
            text = "aquariums",
            selected = true,
            onClick = {  }
        )
    }
}