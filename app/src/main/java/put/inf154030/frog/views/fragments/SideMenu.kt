package put.inf154030.frog.views.fragments

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import put.inf154030.frog.views.activities.about.AboutActivity
import put.inf154030.frog.views.activities.account.AccountActivity
import put.inf154030.frog.views.activities.notifications.NotificationsActivity
import put.inf154030.frog.views.activities.notifications.UpcomingActivity

@Composable
fun SideMenu(
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it })
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onDismiss() }
        ) {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.3f)
                        .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.69f))
                        .clickable { onDismiss() }
                ) { }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(enabled = false) { }  // Prevents click propagation
                        .background(color = MaterialTheme.colorScheme.background)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Box(
                            modifier = Modifier
                                .height(128.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 48.dp)  // Fine-tune as needed
                            ) {
                                IconButton(
                                    onClick = onDismiss
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Menu",
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "profil",
                            color = MaterialTheme.colorScheme.secondary,
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            modifier = Modifier
                                .clickable {
                                    val intent = Intent(context, AccountActivity::class.java)
                                    context.startActivity(intent)
                                    onDismiss()
                                }
                                .padding(end = 8.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "nadchodzące",
                            color = MaterialTheme.colorScheme.secondary,
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            modifier = Modifier
                                .clickable {
                                    val intent = Intent(context, UpcomingActivity::class.java)
                                    context.startActivity(intent)
                                    onDismiss()
                                }
                                .padding(end = 8.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "powiadomienia",
                            color = MaterialTheme.colorScheme.secondary,
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            modifier = Modifier
                                .clickable {
                                    val intent = Intent(context, NotificationsActivity::class.java)
                                    context.startActivity(intent)
                                    onDismiss()
                                }
                                .padding(end = 8.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "o aplikacji",
                            color = MaterialTheme.colorScheme.secondary,
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            modifier = Modifier
                                .clickable {
                                    val intent = Intent(context, AboutActivity::class.java)
                                    context.startActivity(intent)
                                    onDismiss()
                                }
                                .padding(end = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SideMenuPreview() {
    FrogTheme {
        SideMenu(
            isVisible = true,
            onDismiss = {}
        )
    }
}