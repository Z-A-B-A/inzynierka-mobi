package put.inf154030.frog.fragments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import put.inf154030.frog.theme.FrogTheme

@Composable
fun FilterButtonsRow(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        FilterButton(
            text = "All",
            selected = selectedFilter == "all",
            onClick = { onFilterSelected("all") }
        )
        FilterButton(
            text = "Aquariums",
            selected = selectedFilter == "aquariums",
            onClick = { onFilterSelected("aquariums") }
        )
        FilterButton(
            text = "Terrariums",
            selected = selectedFilter == "terrariums",
            onClick = { onFilterSelected("terrariums") }
        )
    }
}

@Preview
@Composable
fun FilterButtonsRowPreview() {
    FrogTheme {
        FilterButtonsRow(
            selectedFilter = "aquariums",
            onFilterSelected = {  }
        )
    }
}