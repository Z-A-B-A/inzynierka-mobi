package put.inf154030.frog.views.activities.species

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.views.fragments.BackButton
import put.inf154030.frog.views.fragments.TopHeaderBar
import put.inf154030.frog.models.Species
import put.inf154030.frog.models.responses.SpeciesListResponse
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddSpeciesActivity : ComponentActivity() {
    private lateinit var addSpeciesLauncher: ActivityResultLauncher<Intent>
    private var speciesList by mutableStateOf<List<Species>>(emptyList())
    private var isLoading by mutableStateOf(false)
    private var errorMessage by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val containerId = intent.getIntExtra("CONTAINER_ID", -1)

        addSpeciesLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            // When returning from AddLocationActivity, refresh the parameters
            loadSpecies()
        }

        setContent {
            FrogTheme {
                AddSpeciesScreen(
                    onBackClick = { finish() },
                    onCreateSpeciesClick = { TODO() },
                    onSaveClick = { TODO() },
                    speciesList = speciesList
                )
            }
        }
        loadSpecies()
    }

    private fun loadSpecies () {
        isLoading = true
        errorMessage = null

        ApiClient.apiService.getSpecies(null).enqueue(object: Callback<SpeciesListResponse> {
            override fun onResponse(
                call: Call<SpeciesListResponse>,
                response: Response<SpeciesListResponse>
            ) {
                isLoading = false
                if (response.isSuccessful) {
                    speciesList = response.body()?.species ?: emptyList()
                } else {
                    errorMessage = "Failed to load species: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<SpeciesListResponse>, t: Throwable) {
                isLoading = false
                errorMessage = "Network error: ${t.message}"
            }
        })
    }
}

@Composable
fun AddSpeciesScreen(
    onBackClick: () -> Unit,
    onCreateSpeciesClick: () -> Unit,
    onSaveClick: () -> Unit,
    speciesList: List<Species> = emptyList()
) {
    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            TopHeaderBar(
                title = "Add Species",
            )
            BackButton { onBackClick() }
            Column (
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                var isSpeciesDropdownExpanded by remember { mutableStateOf(false) }
                var selectedSpecies by remember { mutableStateOf<Species?>(null) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { isSpeciesDropdownExpanded = true }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedSpecies?.name ?: "Select species",
                            fontFamily = PoppinsFamily
                        )

                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Expand"
                        )
                    }

                    DropdownMenu(
                        expanded = isSpeciesDropdownExpanded,
                        onDismissRequest = { isSpeciesDropdownExpanded = false },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .fillMaxWidth(0.8f)
                    ) {
                        speciesList.forEach { species ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = species.name,
                                        fontFamily = PoppinsFamily
                                    )
                                },
                                onClick = {
                                    selectedSpecies = species
                                    isSpeciesDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.size(4.dp))
                if (selectedSpecies != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Category: ${selectedSpecies!!.category}",
                            fontFamily = PoppinsFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth() // Match the width of your parameter row
                                .padding(vertical = 8.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "Description: \n${selectedSpecies!!.description}",
                            fontFamily = PoppinsFamily,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.size(64.dp))
                Text(
                    text = "Your species is not on the list? \nAdd it!",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 16.sp,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = "-- Create Species --",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 16.sp,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clickable { onCreateSpeciesClick() }
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.size(64.dp))
                Button(
                    onClick = { onSaveClick() },
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                ) {
                    Text(
                        text = "Save",
                        fontFamily = PoppinsFamily
                    )
                }
                Spacer(modifier = Modifier.size(64.dp))
            }
        }
    }
}

@Preview
@Composable
fun AddSpeciesActivityPreview () {
    FrogTheme {
        AddSpeciesScreen(
            onBackClick = {  },
            onCreateSpeciesClick = {  },
            onSaveClick = {  },
            speciesList = listOf(
                Species(1, "FROG1", "FROG1", "frog1", "amphibians", true),
                Species(2, "FROG2", "FROG2", "frog2", "amphibians", true)
            )
        )
    }
}