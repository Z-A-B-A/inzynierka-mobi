package put.inf154030.frog.views.activities.containers

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
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
import put.inf154030.frog.repository.ContainersRepository
import put.inf154030.frog.repository.ParametersRepository
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import put.inf154030.frog.views.activities.containers.ContainerActivity.Companion.TIMEFRAME_12H
import put.inf154030.frog.views.activities.containers.ContainerActivity.Companion.TIMEFRAME_1H
import put.inf154030.frog.views.activities.containers.ContainerActivity.Companion.TIMEFRAME_24H
import put.inf154030.frog.views.activities.containers.ContainerActivity.Companion.TIMEFRAME_6H
import put.inf154030.frog.views.activities.schedule.ScheduleActivity
import put.inf154030.frog.views.fragments.BackButton
import put.inf154030.frog.views.fragments.ParameterChart
import put.inf154030.frog.views.fragments.ParameterItem
import put.inf154030.frog.views.fragments.SpeciesItem
import put.inf154030.frog.views.fragments.TimeFilterChip
import put.inf154030.frog.views.fragments.TopHeaderBar
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ContainerActivity : ComponentActivity() {
    // State variables for UI and data
    private var parametersList by mutableStateOf<List<Parameter>>(emptyList())
    private var speciesList by mutableStateOf<List<ContainerSpecies>>(emptyList())
    private var isLoading by mutableStateOf(false)
    private var errorMessage by mutableStateOf<String?>(null)
    private var containerId = -1
    private var parameterHistoryData by mutableStateOf<Map<String, List<ParameterHistoryEntry>>>(emptyMap())
    private var selectedTimeframe by mutableStateOf("1h")
    private var shouldReloadOnResume = false
    private val containersRepository = ContainersRepository()
    private val parametersRepository = ParametersRepository()

    // Timeframe constants for filtering parameter history
    companion object {
        const val TIMEFRAME_1H = "1h"
        const val TIMEFRAME_6H = "6h"
        const val TIMEFRAME_12H = "12h"
        const val TIMEFRAME_24H = "24h"
    }

    // Calculate date ranges for API requests based on selected timeframe
    private fun getDateRangeForTimeframe(timeframe: String): Pair<String, String> {
        // Get the actual UTC time
        val utcNow = ZonedDateTime.now(ZoneOffset.UTC)
        val shiftedNow = utcNow.plusHours(2) // Adjust for local time if needed
        val shiftedFromDate = when(timeframe) {
            TIMEFRAME_1H -> shiftedNow.minusHours(1)
            TIMEFRAME_6H -> shiftedNow.minusHours(6)
            TIMEFRAME_12H -> shiftedNow.minusHours(12)
            TIMEFRAME_24H -> shiftedNow.minusDays(1)
            else -> shiftedNow.minusHours(1)
        }

        // Format both as UTC timestamps
        val fromDate = shiftedFromDate.format(DateTimeFormatter.ISO_INSTANT)
        val toDate = shiftedNow.format(DateTimeFormatter.ISO_INSTANT)

        return Pair(fromDate, toDate)
    }

    // Load parameter history for all parameters in the selected timeframe
    private fun loadParameterHistory(timeframe: String = "1h") {
        if (parametersList.isEmpty()) return
        isLoading = true
        val (fromDate, toDate) = getDateRangeForTimeframe(timeframe)
        val tempHistoryData = mutableMapOf<String, List<ParameterHistoryEntry>>()
        var loadingCount = parametersList.size

        parametersList.forEach { parameter ->
            parametersRepository.getParameterHistory(
                containerId = containerId,
                parameterType = parameter.parameterType,
                fromDate = fromDate,
                toDate = toDate,
                onResult = { success, response, error ->
                    if (success && response != null) {
                        tempHistoryData[parameter.parameterType] = response.history
                    }
                    loadingCount--
                    if (loadingCount == 0) {
                        parameterHistoryData = tempHistoryData
                    }
                    errorMessage = error
                }
            )
        }
        isLoading = false
    }

    // Load container details and update state
    private fun loadContainerDetails() {
        if (containerId == -1) {
            errorMessage = "Invalid container ID"
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
                    loadParameterHistory(selectedTimeframe)
                } else {
                    errorMessage = error
                }
                isLoading = false
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Read intent extras for container info
        containerId = intent.getIntExtra("CONTAINER_ID", -1)
        val containerName = intent.getStringExtra("CONTAINER_NAME") ?: "ERROR READING NAME"
        val containerDescription = intent.getStringExtra("CONTAINER_DESCRIPTION") ?: "ERROR READING DESCRIPTION"

        setContent {
            FrogTheme {
                // Container screen composable
                ContainerScreen(
                    onBackClick = { finish() },
                    onChangeClick = {
                        shouldReloadOnResume = true
                        val intent = Intent(this, ManageContainerActivity::class.java)
                        intent.putExtra("CONTAINER_ID", containerId)
                        intent.putExtra("CONTAINER_NAME", containerName)
                        startActivity(intent)
                    },
                    onScheduleClick = {
                        shouldReloadOnResume = true
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
                    selectedTimeframe = selectedTimeframe,
                    errorMessage = errorMessage,
                    isLoading = isLoading
                )
            }
        }
        loadContainerDetails()
    }

    // Reload data only if needed (after returning from change/schedule screens)
    override fun onResume() {
        super.onResume()
        if (shouldReloadOnResume) {
            loadContainerDetails()
            shouldReloadOnResume = false
        }
    }
}

// Main UI composable for the container details screen
@Composable
fun ContainerScreen (
    onBackClick: () -> Unit,
    onChangeClick: () -> Unit,
    onScheduleClick: () -> Unit,
    containerName: String,
    containerDescription: String,
    parametersList: List<Parameter>,
    speciesList: List<ContainerSpecies>,
    parameterHistoryData: Map<String, List<ParameterHistoryEntry>>,
    onTimeframeSelected: (String) -> Unit,
    selectedTimeframe: String,
    errorMessage: String?,
    isLoading: Boolean
) {
    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Show loading spinner if loading
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        Column {
            TopHeaderBar(title = containerName)
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
                                    currentValue = parameter.currentValue ?: 0.0,
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
                            modifier = Modifier.fillMaxWidth(),
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
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Parameter History",
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        // Time filter chips for selecting history range
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                        ) {
                            TimeFilterChip(TIMEFRAME_1H, "Last Hour", selectedTimeframe, onTimeframeSelected)
                            TimeFilterChip(TIMEFRAME_6H, "Last 6 Hours", selectedTimeframe, onTimeframeSelected)
                            TimeFilterChip(TIMEFRAME_12H, "Last 12 Hours", selectedTimeframe, onTimeframeSelected)
                            TimeFilterChip(TIMEFRAME_24H, "Last Day", selectedTimeframe, onTimeframeSelected)
                        }

                        // Show message if no history data is available
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

                // Parameter Charts for each parameter
                if (parametersList.isNotEmpty() && parameterHistoryData.isNotEmpty()) {
                    items(parametersList) { parameter ->
                        ParameterChart(
                            parameter = parameter,
                            historyData = parameterHistoryData[parameter.parameterType] ?: emptyList()
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
                // Show error message if present
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontFamily = PoppinsFamily,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                // Button to change container details
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

// Preview for Compose UI
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
                Parameter("Temperatura wody", 25.0, "°C", 24.0, 28.0, true, "", "hotspot_temp", 1),
                Parameter("pH", 6.8, "pH", 6.5, 7.5, false, "", "ph_measure", 1),
                Parameter("Światło", 5.5, "on/off", 0.0, 0.0, true, "", "humidifier", 1)
            ),
            speciesList = listOf(
                ContainerSpecies(1, 1, "frog", 3, "")
            ),
            parameterHistoryData = mapOf(
                Pair(
                    "hotspot_temp",
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
            selectedTimeframe = "1h",
            errorMessage = null,
            isLoading = false
        )
    }
}