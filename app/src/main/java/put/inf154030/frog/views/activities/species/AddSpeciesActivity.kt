package put.inf154030.frog.views.activities.species

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.models.Species
import put.inf154030.frog.models.requests.AddSpeciesRequest
import put.inf154030.frog.repository.SpeciesRepository
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import put.inf154030.frog.views.fragments.BackButton
import put.inf154030.frog.views.fragments.TopHeaderBar

data class Category(val value: String, val label: String)

// Activity for adding a species to a container
class AddSpeciesActivity : ComponentActivity() {
    // State for species list, loading, and error message
    private var speciesList by mutableStateOf<List<Species>>(emptyList())
    private var isLoading by mutableStateOf(false)
    private var errorMessage by mutableStateOf<String?>(null)
    private val speciesRepository = SpeciesRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val containerId = intent.getIntExtra("CONTAINER_ID", -1)

        setContent {
            FrogTheme {
                AddSpeciesScreen(
                    onBackClick = { finish() },
                    onSaveClick = { selectedSpecies, speciesCount ->
                        if (selectedSpecies == null) {
                            Toast.makeText(this, "No species selected!", Toast.LENGTH_LONG).show()
                        } else {
                            isLoading = true
                            errorMessage = null

                            val addSpeciesRequest = AddSpeciesRequest(selectedSpecies.id, speciesCount)
                            // Make API call to add species to container
                            speciesRepository.addSpeciesToContainer(
                                containerId,
                                addSpeciesRequest,
                                onResult = { success, error ->
                                    isLoading = false
                                    errorMessage = error
                                    if (success) finish()
                                }
                            )
                        }
                    },
                    speciesList = speciesList,
                    onFilterSelected = { category ->
                        loadSpecies(if (category == "brak") null else category)
                    },
                    isLoading = isLoading,
                    errorMessage = errorMessage
                )
            }
        }
        // Initial load of species
        loadSpecies()
    }

    // Fetch species from API, optionally filtered by category
    private fun loadSpecies(category: String? = null) {
        isLoading = true
        errorMessage = null

        speciesRepository.getSpecies(
            if (category == "brak") null else category,
            onResult = { success, species, error ->
                if (success && !species.isNullOrEmpty()) speciesList = species
                isLoading = false
                errorMessage = error
            }
        )
    }
}

// Composable for the add species screen UI
@Composable
fun AddSpeciesScreen(
    onBackClick: () -> Unit,
    onSaveClick: (Species?, Int) -> Unit,
    speciesList: List<Species>,
    onFilterSelected: (String?) -> Unit,
    isLoading: Boolean,
    errorMessage: String?
) {
    var isSpeciesDropdownExpanded by remember { mutableStateOf(false) }
    var selectedSpecies by remember { mutableStateOf<Species?>(null) }
    val categories = listOf(
        Category("none", "Brak"),
        Category("reptile", "Gad"),
        Category("amphibian", "Płaz"),
        Category("invertebrate", "Bezkręgowce"),
        Category("fish", "Ryba"),
        Category("aquatic invertebrate", "Bezkręgowce wodne")
    )
    var selectedCategory by remember { mutableStateOf(Category("none", "Brak")) }
    var speciesCount by remember { mutableIntStateOf(1) }
    var isCountDropdownExpanded by remember { mutableStateOf(false) }

    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            TopHeaderBar(title = "Dodaj gatunek")
            BackButton { onBackClick() }
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.size(16.dp))
                // Filter chips for categories
                Text(
                    text = "-- filtry --",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.size(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
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
                                    if (category.value == "none") {
                                        onFilterSelected(null)
                                    } else {
                                        onFilterSelected(category.value)
                                    }
                                },
                                label = {
                                    Text(
                                        text = category.label,
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
                // Dropdown for selecting species
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
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
                            text = selectedSpecies?.name ?: "Wybierz gatunek",
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
                // Show details and count picker if a species is selected
                if (selectedSpecies != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        // Species count picker
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Liczba osobników:",
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
                        Spacer(modifier = Modifier.size(16.dp))
                        // Show selected species details
                        Text(
                            text = "Kategoria: ${selectedSpecies!!.category}",
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
                            text = "Opis: \n${selectedSpecies!!.description}",
                            fontFamily = PoppinsFamily,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.size(32.dp))
            // Show error message if present
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PoppinsFamily,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
            // Save button at the bottom
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Button(
                    onClick = { onSaveClick(selectedSpecies, speciesCount) },
                    modifier = Modifier.fillMaxWidth(0.65f),
                    enabled = !isLoading // Disabled while loading
                ) {
                    Text(
                        text = "Zapisz",
                        fontFamily = PoppinsFamily
                    )
                }
                Spacer(modifier = Modifier.size(64.dp))
            }
            // Overlay spinner if loading
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
        }
    }
}

// Preview for Compose UI
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
            onFilterSelected = { _ -> },
            isLoading = false,
            errorMessage = null
        )
    }
}