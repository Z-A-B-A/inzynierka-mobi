package put.inf154030.frog.views.activities.species

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.models.Species
import put.inf154030.frog.models.requests.AddSpeciesRequest
import put.inf154030.frog.models.responses.ContainerSpeciesItemResponse
import put.inf154030.frog.models.responses.SpeciesListResponse
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import put.inf154030.frog.views.fragments.BackButton
import put.inf154030.frog.views.fragments.TopHeaderBar
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
                    onSaveClick = { selectedSpecies, speciesCount ->
                        if (selectedSpecies == null) {
                            Toast.makeText(this, "No species selected!", Toast.LENGTH_LONG).show()
                        } else {
                            val addSpeciesRequest = AddSpeciesRequest(selectedSpecies.id, speciesCount)

                            ApiClient.apiService.addSpeciesToContainer(containerId, addSpeciesRequest)
                                .enqueue(object: Callback<ContainerSpeciesItemResponse> {
                                    override fun onResponse(
                                        call: Call<ContainerSpeciesItemResponse>,
                                        response: Response<ContainerSpeciesItemResponse>
                                    ) {
                                        finish()
                                    }

                                    override fun onFailure(
                                        call: Call<ContainerSpeciesItemResponse>,
                                        t: Throwable
                                    ) {
                                        Toast.makeText(this@AddSpeciesActivity, "Network error", Toast.LENGTH_LONG).show()
                                    }

                                })
                        }
                    },
                    speciesList = speciesList,
                    onFilterSelected = { category ->
                        loadSpecies(if (category == "none") null else category)
                    }
                )
            }
        }
        loadSpecies()
    }

    private fun loadSpecies(category: String? = null) {
        isLoading = true
        errorMessage = null

        ApiClient.apiService.getSpecies(if (category == "none") null else category).enqueue(object: Callback<SpeciesListResponse> {
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
    onSaveClick: (Species?, Int) -> Unit,
    speciesList: List<Species>,
    onFilterSelected: (String?) -> Unit
) {
    var isSpeciesDropdownExpanded by remember { mutableStateOf(false) }
    var selectedSpecies by remember { mutableStateOf<Species?>(null) }
    val categories = listOf("none", "reptile", "amphibian", "invertebrate", "fish", "aquatic invertebrate")
    var selectedCategory by remember { mutableStateOf("none") }
    var speciesCount by remember { mutableIntStateOf(1) }
    var isCountDropdownExpanded by remember { mutableStateOf(false) }

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
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = "-- filters --",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.size(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(bottom = 16.dp)
                ) {

                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { category ->
                            FilterChip(
                                selected = selectedCategory == category,
                                onClick = {
                                    selectedCategory = category
                                    // Filter species list based on category
                                    if (category == "none") {
                                        onFilterSelected(null)
                                    } else {
                                        onFilterSelected(category)
                                    }
                                },
                                label = {
                                    Text(
                                        text = category.replaceFirstChar { it.uppercase() },
                                        fontFamily = PoppinsFamily,
                                        fontSize = 14.sp,
                                        maxLines = 1
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.secondary,
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    labelColor = MaterialTheme.colorScheme.background
                                )
                            )
                        }
                    }
                }
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
                        // Species count picker
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Species Count:",
                                fontFamily = PoppinsFamily,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colorScheme.secondary,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable { isCountDropdownExpanded = true }
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = speciesCount.toString(),
                                        fontFamily = PoppinsFamily
                                    )

                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Expand count"
                                    )
                                }

                                DropdownMenu(
                                    expanded = isCountDropdownExpanded,
                                    onDismissRequest = { isCountDropdownExpanded = false },
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.surface)
                                        .fillMaxWidth(0.8f)
                                ) {
                                    // Create dropdown with numbers 1-100
                                    (1..100).forEach { count ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = count.toString(),
                                                    fontFamily = PoppinsFamily
                                                )
                                            },
                                            onClick = {
                                                speciesCount = count
                                                isCountDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
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
            }

            Spacer(modifier = Modifier.size(32.dp))
            Column (
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Button(
                    onClick = { onSaveClick(selectedSpecies, speciesCount) },
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
            onSaveClick = { _, _ -> },
            speciesList = listOf(
                Species(1, "FROG1", "FROG1", "frog1", "amphibians", true),
                Species(2, "FROG2", "FROG2", "frog2", "amphibians", true)
            ),
            onFilterSelected = { _ -> }
        )
    }
}