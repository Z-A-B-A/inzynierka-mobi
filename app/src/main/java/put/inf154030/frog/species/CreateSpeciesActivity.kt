package put.inf154030.frog.species

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
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