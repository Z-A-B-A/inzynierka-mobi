package put.inf154030.frog.containers

import android.content.Intent
import android.os.Bundle
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.fragments.BackButton
import put.inf154030.frog.fragments.EditParameterRow
import put.inf154030.frog.fragments.EditSpeciesRow
import put.inf154030.frog.fragments.TopHeaderBar
import put.inf154030.frog.models.Parameter
import put.inf154030.frog.models.Species
import put.inf154030.frog.models.responses.ParametersResponse
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.parameters.AddParameterActivity
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageContainerActivity : ComponentActivity() {
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var parametersList by mutableStateOf<List<Parameter>>(emptyList())
    private var speciesList by mutableStateOf<List<Species>>(emptyList())
    private var isLoadingParams by mutableStateOf(false)
    private var isLoadingSpecies by mutableStateOf(false)
    private var errorMessageParams by mutableStateOf<String?>(null)
    private var errorMessageSpecies by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val containerId = intent.getIntExtra("CONTAINER_ID", -1)
        val containerName = intent.getStringExtra("CONTAINER_NAME") ?: "ERROR READING NAME"

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            loadParameters(containerId)
//            loadSpecies()
        }

        setContent {
            FrogTheme {
                ManageContainerScreen(
                    onBackClick = { finish() },
                    onAddParameterClick = {
                        val intent = Intent(this, AddParameterActivity::class.java)
                        intent.putExtra("CONTAINER_ID", containerId)
                        startActivity(intent)
                    },
                    onDeleteParameterClick = { TODO() },
                    onAddSpeciesClick = { TODO() },
                    onDeleteSpeciesClick = { TODO() },
                    onSaveClick = { TODO() },
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

        loadParameters(containerId)
//        loadSpecies()
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

//    private fun loadSpecies() {
//        TODO()
//    }
}

@Composable
fun ManageContainerScreen(
    onBackClick: () -> Unit,
    onAddParameterClick: () -> Unit,
    onDeleteParameterClick: (Parameter) -> Unit,
    onAddSpeciesClick: () -> Unit,
    onDeleteSpeciesClick: (Species) -> Unit,
    onSaveClick: () -> Unit,
    isLoadingParams: Boolean,
    isLoadingSpecies: Boolean,
    errorMessageParams: String?,
    errorMessageSpecies: String?,
    containerName: String,
    parameters: List<Parameter> = emptyList(),
    species: List<Species> = emptyList()
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
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth(0.8f),
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
                            text = "Value",
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.width(72.dp)
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                        IconButton(
                            onClick = { onAddParameterClick() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add parameter",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.fillMaxSize(0.8f)
                            )
                        }
                    }
                }
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth(0.8f) // Match the width of your parameter row
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
                            EditParameterRow (
                                parameterName = parameter.name,
                                parameterValue = parameter.current_value!!,
                                onDeleteClick = { onDeleteParameterClick(parameter) }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.size(64.dp))
                Row (
                    modifier = Modifier.fillMaxWidth(0.8f),
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
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth(0.8f) // Match the width of your parameter row
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
                                speciesName = species.name,
                                onDeleteClick = { onDeleteSpeciesClick(species) }
                            )
                        }
                    }
                }
            }
            Column (
                modifier = Modifier.fillMaxWidth(),
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
            onAddParameterClick = {  },
            onDeleteParameterClick = {  },
            onAddSpeciesClick = {  },
            onDeleteSpeciesClick = {  },
            onSaveClick = {  },
            isLoadingParams = false,
            isLoadingSpecies = false,
            errorMessageParams = null,
            errorMessageSpecies = null,
            containerName = "Container X",
            parameters = listOf(
                Parameter(1, "Temperature", 35.0, "C", null, null, true, null, null, null),
                Parameter(2, "Humidity", 20.0, "%", null, null, true, null, null, null),
                Parameter(3, "PH", 6.0, "", null, null, true, null, null, null)
            ),
            species = listOf(
                Species(1, "FROG1", "FROG1", "frog1", "amphibians", true),
                Species(2, "FROG2", "FROG2", "frog2", "amphibians", true)
            )
        )
    }
}