package put.inf154030.frog.views.activities.about

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.views.fragments.BackButton
import put.inf154030.frog.views.fragments.TopHeaderBar
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily

// Main activity for the About screen
class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content of the activity using Jetpack Compose
        setContent {
            FrogTheme { // Apply custom theme
                AboutScreen(
                    onBackClick = { finish() } // Close activity on back
                )
            }
        }
    }
}
// TODO("Napisać ładnie o aplikacji")
// Composable function that defines the UI for the About screen
@Composable
fun AboutScreen (
    onBackClick: () -> Unit
) {
    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            TopHeaderBar(
                title = "O Aplikacji" // Show header bar with title
            )
            BackButton { onBackClick() } // Show back button
            Box (
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp, vertical = 32.dp),
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    text = "Aplikacja F.R.O.G. umożliwia zdalne zarządzanie środowiskami hodowlanymi, takimi jak akwaria i terraria. Użytkownicy mogą grupować pojemniki w lokalizacje, monitorować i konfigurować ich parametry, przypisywać gatunki oraz definiować harmonogramy. System został zaprojektowany z myślą o wygodzie hodowców i automatyzacji codziennych zadań związanych z opieką nad organizmami."
                )
            }
        }
    }
}

// Preview function for the AboutScreen composable in Android Studio
@Preview
@Composable
fun AboutActivityPreview () {
    FrogTheme {
        AboutScreen(
            onBackClick = {}
        )
    }
}