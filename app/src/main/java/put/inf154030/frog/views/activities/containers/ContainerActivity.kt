package put.inf154030.frog.views.activities.containers

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import put.inf154030.frog.models.ParameterHistoryEntry
import put.inf154030.frog.models.responses.ContainerSpeciesResponse
import put.inf154030.frog.models.responses.ParameterHistoryResponse
import put.inf154030.frog.models.responses.ParametersResponse
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import put.inf154030.frog.views.activities.schedule.ScheduleActivity
import put.inf154030.frog.views.fragments.BackButton
import put.inf154030.frog.views.fragments.ParameterChart
import put.inf154030.frog.views.fragments.ParameterItem
import put.inf154030.frog.views.fragments.SpeciesItem
import put.inf154030.frog.views.fragments.TimeFilterChip
import put.inf154030.frog.views.fragments.TopHeaderBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ContainerActivity : ComponentActivity() {
    private var parametersList by mutableStateOf<List<Parameter>>(emptyList())
    private var speciesList by mutableStateOf<List<ContainerSpecies>>(emptyList())
    private var isLoadingParams by mutableStateOf(false)
    private var isLoadingSpecies by mutableStateOf(false)
    private var errorMessageParams by mutableStateOf<String?>(null)
    private var errorMessageSpecies by mutableStateOf<String?>(null)
    private var containerId = -1
    private var parameterHistoryData by mutableStateOf<Map<Int, List<ParameterHistoryEntry>>>(emptyMap())
    private var selectedTimeframe by mutableStateOf("1h")

    // Calculate date ranges based on timeframe
    private fun getDateRangeForTimeframe(timeframe: String): Pair<String, String> {
        // Get the actual UTC time (13:00)
        val utcNow = ZonedDateTime.now(ZoneOffset.UTC)

        // Add 2 hours to make it match your local time (15:00)
        val shiftedNow = utcNow.plusHours(2)

        // Calculate the from time and also shift it by 2 hours
        val shiftedFromDate = when(timeframe) {
            "1h" -> shiftedNow.minusHours(1)
            "6h" -> shiftedNow.minusHours(6)
            "12h" -> shiftedNow.minusHours(12)
            "24h" -> shiftedNow.minusDays(1)
            else -> shiftedNow.minusHours(1)
        }

        // Format both as UTC timestamps
        val fromDate = shiftedFromDate.format(DateTimeFormatter.ISO_INSTANT)
        val toDate = shiftedNow.format(DateTimeFormatter.ISO_INSTANT)

        println("Shifted time as UTC: $shiftedNow")
        println("Shifted from time as UTC: $fromDate")
        println("Shifted to time as UTC: $toDate")
        return Pair(fromDate, toDate)
    }

    // Load history for all parameters
    private fun loadParameterHistory(timeframe: String = "1h") {
        if (parametersList.isEmpty()) return

        val (fromDate, toDate) = getDateRangeForTimeframe(timeframe)
        val tempHistoryData = mutableMapOf<Int, List<ParameterHistoryEntry>>()
        var loadingCount = parametersList.size

        parametersList.forEach { parameter ->
            ApiClient.apiService.getParameterHistory(parameter.id, fromDate, toDate)
                .enqueue(object: Callback<ParameterHistoryResponse> {
                    override fun onResponse(
                        call: Call<ParameterHistoryResponse>,
                        response: Response<ParameterHistoryResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let { historyResponse ->
                                tempHistoryData[parameter.id] = historyResponse.history
                            }
                        }

                        loadingCount--
                        if (loadingCount == 0) {
                            // All parameters loaded
                            parameterHistoryData = tempHistoryData
                        }
                    }

                    override fun onFailure(call: Call<ParameterHistoryResponse>, t: Throwable) {
                        loadingCount--
                        if (loadingCount == 0) {
                            // All parameters loaded (even with failures)
                            parameterHistoryData = tempHistoryData
                        }
                    }
                })
        }
    }

//    TODO("parametry i species można dostać jednym strzałem z api GET /api/containers/{id}")
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
                        loadParameterHistory(selectedTimeframe)
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
                    speciesList = speciesList,
                    parameterHistoryData = parameterHistoryData,
                    onTimeframeSelected = { timeframe ->
                        selectedTimeframe = timeframe
                        loadParameterHistory(timeframe)
                    },
                    selectedTimeframe = selectedTimeframe
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
}

@Composable
fun ContainerScreen (
    onBackClick: () -> Unit,
    onChangeClick: () -> Unit,
    onScheduleClick: () -> Unit,
    containerName: String,
    containerDescription: String,
    parametersList: List<Parameter>,
    speciesList: List<ContainerSpecies>,
    parameterHistoryData: Map<Int, List<ParameterHistoryEntry>>,
    onTimeframeSelected: (String) -> Unit,
    selectedTimeframe: String
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Description Section
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
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
                    }
                }

                // Parameters Section
                item {
                    if (parametersList.isEmpty()) {
                        Text(
                            text = "-- no parameters --",
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            parametersList.forEach { parameter ->
                                ParameterItem(
                                    parameterName = parameter.name,
                                    currentValue = parameter.current_value ?: 0.0,
                                    unit = parameter.unit
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(32.dp))
                }

                // Species Header
                item {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
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
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                // Species List
                item {
                    if (speciesList.isEmpty()) {
                        Text(
                            text = "-- no species --",
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            speciesList.forEach { species ->
                                SpeciesItem(
                                    speciesName = species.name,
                                    speciesCount = species.count
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(32.dp))
                }

                // Parameter History Section
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Parameter History",
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        // Time filter chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                        ) {
                            TimeFilterChip("1h", "Last Hour", selectedTimeframe, onTimeframeSelected)
                            TimeFilterChip("6h", "Last 6 Hours", selectedTimeframe, onTimeframeSelected)
                            TimeFilterChip("12h", "Last 12 Hours", selectedTimeframe, onTimeframeSelected)
                            TimeFilterChip("24h", "Last Day", selectedTimeframe, onTimeframeSelected)
                        }

                        // History availability message
                        if (parametersList.isEmpty() || parameterHistoryData.isEmpty()) {
                            Text(
                                text = "No history data available",
                                fontFamily = PoppinsFamily,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }
                    }
                }

                // Parameter Charts
                if (parametersList.isNotEmpty() && parameterHistoryData.isNotEmpty()) {
                    items(parametersList) { parameter ->
                        ParameterChart(
                            parameter = parameter,
                            historyData = parameterHistoryData[parameter.id] ?: emptyList()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
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
            ),
            parameterHistoryData = mapOf(
                Pair(
                    5,
                    listOf(
                        ParameterHistoryEntry(21.5, "2025-05-14T10:00:00"),
                        ParameterHistoryEntry(22.0, "2025-05-14T10:10:00"),
                        ParameterHistoryEntry(22.3, "2025-05-14T10:20:00"),
                        ParameterHistoryEntry(21.9, "2025-05-14T10:30:00"),
                        ParameterHistoryEntry(21.7, "2025-05-14T10:40:00"),
                        ParameterHistoryEntry(21.6, "2025-05-14T10:50:00"),
                        ParameterHistoryEntry(21.8, "2025-05-14T11:00:00"),
                        ParameterHistoryEntry(22.1, "2025-05-14T11:10:00"),
                        ParameterHistoryEntry(22.4, "2025-05-14T11:20:00"),
                        ParameterHistoryEntry(22.2, "2025-05-14T11:30:00")
                    )
                )
            ),
            onTimeframeSelected = {},
            selectedTimeframe = "1h"
        )
    }
}