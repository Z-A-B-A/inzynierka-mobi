package put.inf154030.frog.views.activities.account

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import put.inf154030.frog.network.SessionManager
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import put.inf154030.frog.views.fragments.BackButton
import put.inf154030.frog.views.fragments.TopHeaderBar

class AccountActivity : ComponentActivity() {
    private var userName by mutableStateOf<String?>(null)
    private var userEmail by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadUserData()
        setContent {
            FrogTheme {
                AccountScreen(
                    onBackClick = { finish() },
                    onEditClick = {
                        val intent = Intent(this, EditAccountActivity::class.java)
                        startActivity(intent)
                    },
                    userName = userName,
                    userEmail = userEmail
                )
            }
        }
    }
    override fun onResume() {
        super.onResume()
        // Reload user data when resuming the activity
        loadUserData()
    }

    private fun loadUserData() {
        userName = SessionManager.getUserName()
        userEmail = SessionManager.getUserEmail()
    }
}

@Composable
fun AccountScreen (
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    userName: String?,
    userEmail: String?
) {
    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            TopHeaderBar(
                title = "Account"
            )
            BackButton { onBackClick() }
            Spacer(modifier = Modifier.size(32.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(40.dp)
                    .padding(horizontal = 32.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = userName ?: "Could not load name",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
            Spacer(modifier = Modifier.size(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(40.dp)
                    .padding(horizontal = 32.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.CenterStart
            ) {
                AutoResizeText(
                    text = userEmail ?: "Could not load email",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
            Column (
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { onEditClick() },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(0.65f),
                ) {
                    Text(
                        text = "Edit",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.size(64.dp))
            }
        }
    }
}

@Composable
fun AutoResizeText(
    text: String,
    fontFamily: FontFamily,
    fontWeight: FontWeight,
    color: Color,
    modifier: Modifier = Modifier,
    maxFontSize: TextUnit = 20.sp,
    minFontSize: TextUnit = 12.sp
) {
    var fontSize by remember { mutableStateOf(maxFontSize) }
    Text(
        text = text,
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        fontSize = fontSize,
        color = color,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.hasVisualOverflow && fontSize > minFontSize) {
                fontSize = fontSize.times(0.9f)
            }
        }
    )
}

@Preview
@Composable
fun AccountActivityPreview () {
    FrogTheme {
        AccountScreen(
            onBackClick = {},
            onEditClick = {},
            userName = "Bartosz",
            userEmail = "bartoszkorszun@gmail.com"
        )
    }
}
