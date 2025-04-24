package put.inf154030.frog.locations

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import put.inf154030.frog.R
import put.inf154030.frog.fragments.LocationCard
import put.inf154030.frog.fragments.TopNavigationBar
import put.inf154030.frog.models.Location
import put.inf154030.frog.theme.FrogTheme

class LocationsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FrogTheme {
                LocationsScreen(
                    locations = listOf() // You'll populate this from your data source
                )
            }
        }
    }
}

@Composable
fun LocationsScreen(
    locations: List<Location> = emptyList()
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopNavigationBar(
                    title = "Hi, XYZ!",
                    onMenuClick = { /* Handle menu click */ }
                )

                // This is the scrollable content
                if (locations.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No locations added yet",
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        items(locations) { location ->
                            LocationCard(
                                locationName = location.name,
                                onEditClick = { /* Handle edit click */ }
                            )
                        }
                    }
                }
            }

            // Floating action button
            IconButton(
                onClick = { /* Handle add click */ },
                modifier = Modifier
                    .padding(bottom = 48.dp, end = 32.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add_buton),
                    contentDescription = "Add new location",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LocationsActivityPreview() {
    FrogTheme {
        LocationsScreen(
            locations = listOf(
                Location(1, "Home", "", ""),
                Location(2, "Office", "", ""),
                Location(3, "Gym", "", "")
            )
        )
    }
}