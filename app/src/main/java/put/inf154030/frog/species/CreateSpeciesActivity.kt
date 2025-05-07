package put.inf154030.frog.species

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import put.inf154030.frog.R
import put.inf154030.frog.theme.FrogTheme

class CreateSpeciesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FrogTheme {
                CreateSpeciesScreen()
            }
        }
    }
}

@Composable
fun CreateSpeciesScreen () {}

@Preview
@Composable
fun CreateSpeciesActivityPreview () {
    FrogTheme {
        CreateSpeciesScreen()
    }
}