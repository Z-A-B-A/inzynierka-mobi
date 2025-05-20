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
import put.inf154030.frog.models.responses.ContainerSpeciesResponse
import put.inf154030.frog.models.responses.ContainerSpeciesUpdateResponse
import put.inf154030.frog.models.responses.MessageResponse
import put.inf154030.frog.models.responses.ParameterResponse
import put.inf154030.frog.models.responses.ParametersResponse
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import put.inf154030.frog.views.activities.species.AddSpeciesActivity
import put.inf154030.frog.views.fragments.BackButton
import put.inf154030.frog.views.fragments.EditParameterRow
import put.inf154030.frog.views.fragments.EditSpeciesRow
import put.inf154030.frog.views.fragments.TopHeaderBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageContainerActivity : ComponentActivity() {
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var parametersList by mutableStateOf<List<Parameter>>(emptyList())
    private var speciesList by mutableStateOf<List<ContainerSpecies>>(emptyList())
    private var isLoadingParams by mutableStateOf(false)
    private var isLoadingSpecies by mutableStateOf(false)
    private var errorMessageParams by mutableStateOf<String?>(null)
    private var errorMessageSpecies by mutableStateOf<String?>(null)
    private var containerId = -1

    // Maps to store the updated values
    private var parameterMinValues = mutableMapOf<Int, String>()
    private var parameterMaxValues = mutableMapOf<Int, String>()
    private var speciesCounts = mutableMapOf<Int, String>()

    // Flags to track save status
    private var isSaving by mutableStateOf(false)
    private var saveErrorCount = 0
    private var totalSaveRequests = 0
    private var completedRequests = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        containerId = intent.getIntExtra("CONTAINER_ID", -1)
        val containerName = intent.getStringExtra("CONTAINER_NAME") ?: "ERROR READING NAME"

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            loadParameters(containerId = containerId)
            loadSpecies(containerId = containerId)
        }

        setContent {
            FrogTheme {
                ManageContainerScreen(
                    onBackClick = { finish() },
                    onAddSpeciesClick = {
                        val intent = Intent(this, AddSpeciesActivity::class.java)
                        intent.putExtra("CONTAINER_ID", containerId)
                        activityResultLauncher.launch(intent)
                    },
                    onRemoveSpeciesClick = { speciesId ->
                        ApiClient.apiService.deleteSpeciesFromContainer(containerId, speciesId)
                            .enqueue(object : Callback<MessageResponse> {
                                override fun onResponse(
                                    call: Call<MessageResponse>,
                                    response: Response<MessageResponse>
                                ) {
                                    Toast.makeText(this@ManageContainerActivity, "Species removed from container", Toast.LENGTH_LONG).show()
                                    loadSpecies(containerId)
                                }

                                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                                    errorMessageSpecies = t.message
                                }
                            })
                    },
                    onSaveClick = {
                        saveChanges()
                        finish()
                    },
                    onParameterMinValueChanged = { parameterId, value ->
                        parameterMinValues[parameterId] = value
                    },
                    onParameterMaxValueChanged = { parameterId, value ->
                        parameterMaxValues[parameterId] = value
                    },
                    onSpeciesCountChanged = { speciesId, value ->
                        speciesCounts[speciesId] = value
                    },
                    isLoadingParams = isLoadingParams,
                    isLoadingSpecies = isLoadingSpecies,
                    errorMessageParams = errorMessageParams,
                    errorMessageSpecies = errorMessageSpecies,
                    containerName = containerName,
                    parameters = parametersList,
                    species = speciesList
                )
            }
        }

        loadParameters(containerId = containerId)
        loadSpecies(containerId = containerId)
    }

    private fun saveChanges() {
        if (isSaving) return // Prevent duplicate requests

        isSaving = true
        saveErrorCount = 0
        completedRequests = 0

        // Build the list of parameter update requests
        val parameterUpdates = parametersList.mapNotNull { parameter ->
            val minValueStr = parameterMinValues[parameter.id]
            val maxValueStr = parameterMaxValues[parameter.id]

            // Skip if no changes
            if (minValueStr == null && maxValueStr == null) return@mapNotNull null

            val minValue = minValueStr?.toDoubleOrNull() ?: parameter.minValue
            val maxValue = maxValueStr?.toDoubleOrNull() ?: parameter.maxValue

            // Create update request
            val parameterRequest = ParameterUpdateRequest(
                name = null, // We're not updating the name
                minValue = minValue,
                maxValue = maxValue,
                isControlled = null // We're not updating this either
            )

            Pair(parameter.id, parameterRequest)
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
            Toast.makeText(this, "No changes to save", Toast.LENGTH_SHORT).show()
            isSaving = false
            return
        }

        // Process parameter updates
        for ((parameterId, updateRequest) in parameterUpdates) {
            ApiClient.apiService.updateParameter(parameterId, updateRequest)
                .enqueue(object : Callback<ParameterResponse> {
                    override fun onResponse(call: Call<ParameterResponse>, response: Response<ParameterResponse>) {
                        completedRequests++
                        if (!response.isSuccessful) {
                            saveErrorCount++
                        }
                        checkSaveCompletion()
                    }

                    override fun onFailure(call: Call<ParameterResponse>, t: Throwable) {
                        completedRequests++
                        saveErrorCount++
                        checkSaveCompletion()
                    }
                })
        }

        // Process species updates
        for ((speciesId, updateRequest) in speciesUpdates) {
            ApiClient.apiService.updateContainerSpecies(containerId, speciesId, updateRequest)
                .enqueue(object : Callback<ContainerSpeciesUpdateResponse> {
                    override fun onResponse(
                        call: Call<ContainerSpeciesUpdateResponse>,
                        response: Response<ContainerSpeciesUpdateResponse>
                    ) {
                        completedRequests++
                        if (!response.isSuccessful) {
                            saveErrorCount++
                        }
                        checkSaveCompletion()
                    }

                    override fun onFailure(call: Call<ContainerSpeciesUpdateResponse>, t: Throwable) {
                        completedRequests++
                        saveErrorCount++
                        checkSaveCompletion()
                    }
                })
        }
    }

    private fun checkSaveCompletion() {
        if (completedRequests >= totalSaveRequests) {
            // All requests completed
            isSaving = false

            if (saveErrorCount > 0) {
                Toast.makeText(this,
                    "Not all changes were saved (${saveErrorCount} errors)",
                    Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "All changes saved successfully", Toast.LENGTH_SHORT).show()
                finish() // Return to previous screen on success
            }

            // Reload data if not finishing
            if (saveErrorCount > 0) {
                loadParameters(containerId)
                loadSpecies(containerId)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadSpecies(containerId = containerId)
    }

    private fun loadParameters(
        containerId: Int
    ) {
        isLoadingParams = true
        errorMessageParams = null

        ApiClient.apiService.getParameters(containerId).enqueue(object:
            Callback<ParametersResponse> {
            override fun onResponse(
                call: Call<ParametersResponse>,
                response: Response<ParametersResponse>
            ) {
                isLoadingParams = false
                if (response.isSuccessful) {
                    parametersList = response.body()?.parameters ?: emptyList()
                } else {
                    errorMessageParams = "Failed to load parameters: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<ParametersResponse>, t: Throwable) {
                isLoadingParams = false
                errorMessageParams = "Network error: ${t.message}"
            }
        })
    }

    private fun loadSpecies(
        containerId: Int
    ) {
        isLoadingSpecies = true
        errorMessageSpecies = null

        ApiClient.apiService.getContainerSpecies(containerId)
            .enqueue(object: Callback<ContainerSpeciesResponse> {
                override fun onResponse(
                    call: Call<ContainerSpeciesResponse>,
                    response: Response<ContainerSpeciesResponse>
                ) {
                    isLoadingSpecies = false
                    if (response.isSuccessful) {
                        speciesList = response.body()?.species ?: emptyList()
                    } else {
                        errorMessageParams = "Failed to load species: ${response.message()}"
                    }
                }

                override fun onFailure(call: Call<ContainerSpeciesResponse>, t: Throwable) {
                    isLoadingSpecies = false
                    errorMessageSpecies = "Network error: ${t.message}"
                }
            })
    }
}

@Composable
fun ManageContainerScreen(
    onBackClick: () -> Unit,
    onAddSpeciesClick: () -> Unit,
    onRemoveSpeciesClick: (Int) -> Unit,
    onSaveClick: () -> Unit,
    onParameterMinValueChanged: (Int, String) -> Unit,
    onParameterMaxValueChanged: (Int, String) -> Unit,
    onSpeciesCountChanged: (Int, String) -> Unit,
    isLoadingParams: Boolean,
    isLoadingSpecies: Boolean,
    errorMessageParams: String?,
    errorMessageSpecies: String?,
    containerName: String,
    parameters: List<Parameter>,
    species: List<ContainerSpecies>
) {
    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            TopHeaderBar(
                title = containerName,
            )
            BackButton { onBackClick() }
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Parameter",
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
                if (isLoadingParams) {
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
                            text = "No parameter added yet",
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
                                parameterId = parameter.id,
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
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Species",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Count",
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
                if (isLoadingSpecies) {
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
                            text = "No species added yet",
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
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
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
            isLoadingParams = false,
            isLoadingSpecies = false,
            errorMessageParams = null,
            errorMessageSpecies = null,
            containerName = "Container X",
            parameters = listOf(
                Parameter(5, "Temperatura wody", 25.0, "°C", 24.0, 28.0, true, "", "", "predefined"),
                Parameter(6, "pH", 6.8, "pH", 6.5, 7.5, false, "", "", "predefined"),
                Parameter(7, "Światło", 5.5, "on/off", 0.0, 0.0, true, "", "", "predefined")
            ),
            species = listOf(
                ContainerSpecies(1, 1, "frog", 3, "")
            )
        )
    }
}