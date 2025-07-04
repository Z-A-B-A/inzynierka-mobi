package put.inf154030.frog.views.activities.containers

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.models.ContainerSpecies
import put.inf154030.frog.models.Parameter
import put.inf154030.frog.models.requests.ParameterUpdateRequest
import put.inf154030.frog.models.requests.UpdateSpeciesCountRequest
import put.inf154030.frog.repository.ContainersRepository
import put.inf154030.frog.repository.ParametersRepository
import put.inf154030.frog.repository.SpeciesRepository
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import put.inf154030.frog.views.activities.species.AddSpeciesActivity
import put.inf154030.frog.views.fragments.BackButton
import put.inf154030.frog.views.fragments.EditParameterRow
import put.inf154030.frog.views.fragments.EditSpeciesRow
import put.inf154030.frog.views.fragments.TopHeaderBar

// Activity for managing container parameters and species
class ManageContainerActivity : ComponentActivity() {
    // ActivityResultLauncher for adding species
    private lateinit var speciesLauncher: ActivityResultLauncher<Intent>
    // State for parameters and species
    private var parametersList by mutableStateOf<List<Parameter>>(emptyList())
    private var speciesList by mutableStateOf<List<ContainerSpecies>>(emptyList())
    private var isLoading by mutableStateOf(false)
    private var errorMessage by mutableStateOf<String?>(null)
    private var errorMessageParams by mutableStateOf<String?>(null)
    private var errorMessageSpecies by mutableStateOf<String?>(null)
    private var containerId = -1
    // Maps to store the updated values
    private var parameterMinValues = mutableMapOf<String, String>()
    private var parameterMaxValues = mutableMapOf<String, String>()
    private var speciesCounts = mutableMapOf<Int, String>()
    // Sets to track invalid input fields
    private var invalidParameterInputs = mutableSetOf<String>()
    private var invalidSpeciesInputs = mutableSetOf<Int>()
    // Save status tracking
    private var saveErrorCount = 0
    private var totalSaveRequests = 0
    private var completedRequests = 0

    private val speciesRepository = SpeciesRepository()
    private val parametersRepository = ParametersRepository()
    private val containersRepository = ContainersRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        containerId = intent.getIntExtra("CONTAINER_ID", -1)
        val containerName = intent.getStringExtra("CONTAINER_NAME") ?: "ERROR READING NAME"

        // Register for result from AddSpeciesActivity
        speciesLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            loadContainerDetails()
        }

        setContent {
            FrogTheme {
                ManageContainerScreen(
                    onBackClick = { finish() },
                    onAddSpeciesClick = {
                        val intent = Intent(this, AddSpeciesActivity::class.java)
                        intent.putExtra("CONTAINER_ID", containerId)
                        speciesLauncher.launch(intent)
                    },
                    onRemoveSpeciesClick = { speciesId ->
                        // Remove species from container
                        speciesRepository.deleteSpeciesFromContainer(
                            containerId,
                            speciesId,
                            onResult = { success, error ->
                                isLoading = false
                                errorMessage = error
                                if (success) loadContainerDetails()
                            }
                        )
                    },
                    onSaveClick = {
                        saveChanges()
                        finish()
                    },
                    // Handle parameter min value changes and validation
                    onParameterMinValueChanged = { parameterType, value ->
                        parameterMinValues[parameterType] = value
                        if (value.isNotBlank() && value.toDoubleOrNull() == null) {
                            invalidParameterInputs.add(parameterType)
                        } else {
                            invalidParameterInputs.remove(parameterType)
                        }
                    },
                    // Handle parameter max value changes and validation
                    onParameterMaxValueChanged = { parameterType, value ->
                        parameterMaxValues[parameterType] = value
                        if (value.isNotBlank() && value.toDoubleOrNull() == null) {
                            invalidParameterInputs.add(parameterType)
                        } else {
                            invalidParameterInputs.remove(parameterType)
                        }
                    },
                    // Handle species count changes and validation
                    onSpeciesCountChanged = { speciesId, value ->
                        speciesCounts[speciesId] = value
                        if (value.isNotBlank() && value.toIntOrNull() == null) {
                            invalidSpeciesInputs.add(speciesId)
                        } else {
                            invalidSpeciesInputs.remove(speciesId)
                        }
                    },
                    isLoading = isLoading,
                    errorMessageParams = errorMessageParams,
                    errorMessageSpecies = errorMessageSpecies,
                    hasInvalidInput = invalidParameterInputs.isNotEmpty() || invalidSpeciesInputs.isNotEmpty(),
                    containerName = containerName,
                    parameters = parametersList,
                    species = speciesList
                )
            }
        }
        // Initial data load
        loadContainerDetails()
    }

    // Save all changes to parameters and species
    private fun saveChanges() {
        if (isLoading) return // Prevent duplicate requests

        isLoading = true
        saveErrorCount = 0
        completedRequests = 0

        // Build the list of parameter update requests
        val parameterUpdates = parametersList.mapNotNull { parameter ->
            val minValueStr = parameterMinValues[parameter.parameterType]
            val maxValueStr = parameterMaxValues[parameter.parameterType]

            // Skip if no changes
            if (minValueStr == null && maxValueStr == null) return@mapNotNull null

            val minValue = minValueStr?.toDoubleOrNull() ?: parameter.minValue
            val maxValue = maxValueStr?.toDoubleOrNull() ?: parameter.maxValue

            // Create update request
            val parameterRequest = ParameterUpdateRequest(
                minValue = minValue,
                maxValue = maxValue,
            )

            Pair(parameter.parameterType, parameterRequest)
        }

        // Build the list of species update requests
        val speciesUpdates = speciesList.mapNotNull { species ->
            val countStr = speciesCounts[species.speciesId] ?: return@mapNotNull null
            val count = countStr.toIntOrNull() ?: species.count

            // Skip if no actual change
            if (count == species.count) return@mapNotNull null

            // Create update request
            val speciesRequest = UpdateSpeciesCountRequest(count = count)
            Pair(species.speciesId, speciesRequest)
        }

        totalSaveRequests = parameterUpdates.size + speciesUpdates.size

        // If nothing to update, just return
        if (totalSaveRequests == 0) {
            Toast.makeText(this, "Brak zmian do zapisu", Toast.LENGTH_SHORT).show()
            isLoading = false
            return
        }

        // Process parameter updates
        for ((parameterType, updateRequest) in parameterUpdates) {
            parametersRepository.updateParameter(
                containerId,
                parameterType,
                updateRequest,
                onResult = { success, failure, error ->
                    completedRequests++
                    isLoading = false
                    errorMessage = error
                    if (!success) saveErrorCount++
                    if (failure) saveErrorCount++
                    checkSaveCompletion()
                }
            )
        }

        // Process species updates
        for ((speciesId, updateRequest) in speciesUpdates) {
            speciesRepository.updateContainerSpecies(
                containerId,
                speciesId,
                updateRequest,
                onResult = { success, failure, error ->
                    completedRequests++
                    isLoading = false
                    errorMessage = error
                    if (!success) saveErrorCount++
                    if (failure) saveErrorCount++
                    checkSaveCompletion()
                }
            )
        }
    }

    // Called after each save request completes
    private fun checkSaveCompletion() {
        if (completedRequests >= totalSaveRequests) {
            isLoading = false
            if (saveErrorCount > 0) {
                // Reload data if not finishing
                loadContainerDetails()
            } else {
                finish() // Return to previous screen on success
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadContainerDetails()
    }

    // Load container details and update state
    private fun loadContainerDetails() {
        if (containerId == -1) {
            errorMessage = "Nieprawidłowe ID pojemnika"
            return
        }

        isLoading = true
        errorMessage = null

        containersRepository.getContainerDetails(
            containerId,
            onResult = { success, response, error ->
                if (success && response != null) {
                    parametersList = response.parameters
                    speciesList = response.species ?: emptyList()
                } else {
                    errorMessage = error
                }
                isLoading = false
            }
        )
    }
}

// Composable for managing parameters and species in a container
@Composable
fun ManageContainerScreen(
    onBackClick: () -> Unit,
    onAddSpeciesClick: () -> Unit,
    onRemoveSpeciesClick: (Int) -> Unit,
    onSaveClick: () -> Unit,
    onParameterMinValueChanged: (String, String) -> Unit,
    onParameterMaxValueChanged: (String, String) -> Unit,
    onSpeciesCountChanged: (Int, String) -> Unit,
    isLoading: Boolean,
    errorMessageParams: String?,
    errorMessageSpecies: String?,
    hasInvalidInput: Boolean,
    containerName: String,
    parameters: List<Parameter>,
    species: List<ContainerSpecies>
) {
    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            TopHeaderBar(title = containerName)
            BackButton { onBackClick() }
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Parameter section header
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Parametr",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Row (
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Min",
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.width(72.dp),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Max",
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.width(72.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.secondary
                )
                // Parameter list, loading, or error
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                    }
                } else if (errorMessageParams != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessageParams,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                } else if (parameters.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Brak parametrów",
                            color = MaterialTheme.colorScheme.secondary,
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(parameters) { parameter ->
                            EditParameterRow(
                                parameterType = parameter.parameterType,
                                parameterName = parameter.name,
                                parameterMin = parameter.minValue ?: 0.0,
                                parameterMax = parameter.maxValue ?: 0.0,
                                onMinValueChanged = onParameterMinValueChanged,
                                onMaxValueChanged = onParameterMaxValueChanged
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.size(64.dp))
                // Species section header
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Gatunek",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ilość",
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.width(72.dp),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                        IconButton(
                            onClick = { onAddSpeciesClick() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add species",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.fillMaxSize(0.8f)
                            )
                        }
                    }
                }
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.secondary
                )
                // Species list, loading, or error
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                    }
                } else if (errorMessageSpecies != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessageSpecies,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                } else if (species.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Brak gatunków",
                            color = MaterialTheme.colorScheme.secondary,
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(species) { species ->
                            EditSpeciesRow(
                                speciesId = species.speciesId,
                                speciesName = species.name,
                                speciesCount = species.count,
                                onCountChanged = onSpeciesCountChanged,
                                onDeleteClick = { onRemoveSpeciesClick(species.speciesId) }
                            )
                        }
                    }
                }
            }
            // Save button and validation message
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                if (hasInvalidInput) {
                    Text(
                        text = "Popraw nieprawidłowe wartości parametrów lub liczbę gatunków",
                        color = MaterialTheme.colorScheme.error,
                        fontFamily = PoppinsFamily,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                Button(
                    onClick = { onSaveClick() },
                    modifier = Modifier.fillMaxWidth(0.65f),
                    enabled = !isLoading && !hasInvalidInput
                ) {
                    Text(
                        text = if (isLoading) "Zapisywanie..." else "Zapisz",
                        fontFamily = PoppinsFamily
                    )
                }
                Spacer(modifier = Modifier.size(64.dp))
            }
        }
    }
}

// Preview for Compose UI
@Preview
@Composable
fun ManageContainerActivityPreview () {
    FrogTheme {
        ManageContainerScreen(
            onBackClick = {  },
            onAddSpeciesClick = {  },
            onRemoveSpeciesClick = {  },
            onSaveClick = {  },
            onParameterMaxValueChanged = { _, _ -> },
            onParameterMinValueChanged = { _, _ -> },
            onSpeciesCountChanged = { _, _ -> },
            isLoading = false,
            errorMessageParams = null,
            errorMessageSpecies = null,
            hasInvalidInput = false,
            containerName = "Container X",
            parameters = listOf(
                Parameter("Temperatura wody", 25.0, "°C", 24.0, 28.0, true, "", "hotspot_temp", 1),
                Parameter("pH", 6.8, "pH", 6.5, 7.5, false, "", "ph_measure", 1),
                Parameter("Światło", 5.5, "on/off", 0.0, 0.0, true, "", "humidifier", 1)
            ),
            species = listOf(
                ContainerSpecies(1, 1, "frog", 3, "")
            )
        )
    }
}