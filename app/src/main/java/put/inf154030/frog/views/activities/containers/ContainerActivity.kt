package put.inf154030.frog.views.activities.containers

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
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
import put.inf154030.frog.models.ContainerSpecies
import put.inf154030.frog.models.Parameter
import put.inf154030.frog.models.responses.ContainerSpeciesResponse
import put.inf154030.frog.models.responses.ParametersResponse
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import put.inf154030.frog.views.activities.schedule.ScheduleActivity
import put.inf154030.frog.views.fragments.BackButton
import put.inf154030.frog.views.fragments.ParameterItem
import put.inf154030.frog.views.fragments.SpeciesItem
import put.inf154030.frog.views.fragments.TopHeaderBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContainerActivity : ComponentActivity() {
    private var parametersList by mutableStateOf<List<Parameter>>(emptyList())
    private var speciesList by mutableStateOf<List<ContainerSpecies>>(emptyList())
    private var isLoadingParams by mutableStateOf(false)
    private var isLoadingSpecies by mutableStateOf(false)
    private var errorMessageParams by mutableStateOf<String?>(null)
    private var errorMessageSpecies by mutableStateOf<String?>(null)
    private var containerId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        containerId = intent.getIntExtra("CONTAINER_ID", -1)
        val containerName = intent.getStringExtra("CONTAINER_NAME") ?: "ERROR READING NAME"
        val containerDescription = intent.getStringExtra("CONTAINER_DESCRIPTION") ?: "ERROR READING DESCRIPTION"

        setContent {
            FrogTheme {
                ContainerScreen(
                    onBackClick = { finish() },
                    onChangeClick = {
                        val intent = Intent(this, ManageContainerActivity::class.java)
                        intent.putExtra("CONTAINER_ID", containerId)
                        intent.putExtra("CONTAINER_NAME", containerName)
                        startActivity(intent)
                    },
                    onScheduleClick = {
                        val intent = Intent(this, ScheduleActivity::class.java)
                        intent.putExtra("CONTAINER_ID", containerId)
                        startActivity(intent)
                    },
                    containerName = containerName,
                    containerDescription = containerDescription,
                    parametersList = parametersList,
                    speciesList = speciesList
                )
            }
        }
        loadParameters()
        loadSpecies()
    }

    override fun onResume() {
        super.onResume()
        loadParameters()
        loadSpecies()
    }

    private fun loadParameters() {
        if (containerId == -1) {
            errorMessageParams = "Invalid container ID"
            return
        }

        isLoadingParams = true
        errorMessageParams = null

        ApiClient.apiService.getParameters(containerId)
            .enqueue(object: Callback<ParametersResponse> {
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

    private fun loadSpecies() {
        if (containerId == -1) {
            errorMessageParams = "Invalid container ID"
            return
        }

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
fun ContainerScreen (
    onBackClick: () -> Unit,
    onChangeClick: () -> Unit,
    onScheduleClick: () -> Unit,
    containerName: String,
    containerDescription: String,
    parametersList: List<Parameter>,
    speciesList: List<ContainerSpecies>
) {
    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            TopHeaderBar(
                title = containerName,
            )
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BackButton { onBackClick() }
                Text(
                    text = "schedule >>>",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 20.sp,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { onScheduleClick() }
                        .padding(16.dp)
                )
            }
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "-- description --",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = containerDescription,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Thin,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.size(32.dp))
                if (parametersList.isEmpty()) {
                    Text(
                        text = "-- no parameters --",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(parametersList) { parameter ->
                            ParameterItem(
                                parameterName = parameter.name,
                                currentValue = parameter.current_value ?: 0.0,
                                unit = parameter.unit
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.size(32.dp))
                Column {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth(0.8f),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Species",
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "Count",
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(vertical = 8.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                if (speciesList.isEmpty()) {
                    Text(
                        text = "-- no species --",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(speciesList) { species ->
                            SpeciesItem(
                                speciesName = species.name,
                                speciesCount = species.count
                            )
                        }
                    }
                }
//                TODO("Dorobić wykresy")
            }
            Spacer(modifier = Modifier.size(16.dp))
            Column (
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Button(
                    onClick = { onChangeClick() },
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                ) {
                    Text(
                        text = "Change",
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
fun ContainerActivityPreview () {
    FrogTheme {
        ContainerScreen(
            onBackClick = {  },
            onChangeClick = {  },
            onScheduleClick = {  },
            containerName = "Container X",
            containerDescription = "Potężny kontener na bycze ryby",
            parametersList = listOf(
                Parameter(5, "Temperatura wody", 25.0, "°C", 24.0, 28.0, true, "", "", "predefined"),
                Parameter(6, "pH", 6.8, "pH", 6.5, 7.5, false, "", "", "predefined"),
                Parameter(7, "Światło", 5.5, "on/off", 0.0, 0.0, true, "", "", "predefined")
            ),
            speciesList = listOf(
                ContainerSpecies(1, 1, "frog", 3, "")
            )
        )
    }
}