package com.ilynehdev.feature.plants.list

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ilynehdev.data.plants.filters.CareLevelFilter
import com.ilynehdev.data.plants.filters.LightFilter
import com.ilynehdev.data.plants.filters.MatureSizeFilter
import com.ilynehdev.data.plants.filters.PlantFilters
import com.ilynehdev.data.plants.filters.SafetyFilter
import com.ilynehdev.data.plants.filters.WateringFilter
import com.ilynehdev.feature.plants.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantsFilterSheet(
    current: PlantsListFiltersUiData,
    onApply: (PlantFilters) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var draft by remember { mutableStateOf(current.toPlantFilters()) }

    fun hideThen(action: () -> Unit) {
        scope.launch { sheetState.hide() }
            .invokeOnCompletion { action() }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        tonalElevation = 0.dp,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                TextButton(
                    onClick = { hideThen(onDismiss) },
                    modifier = Modifier.alignByBaseline()
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                Text(
                    text = stringResource(R.string.filters),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .alignByBaseline()
                        .semantics { heading() },
                )
                TextButton(
                    onClick = {
                        draft = PlantFilters()
                        hideThen { onApply(draft) }
                    },
                    modifier = Modifier.alignByBaseline()
                ) {
                    Text(
                        text = stringResource(R.string.reset),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            HorizontalDivider()

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                FilterSection(
                    title = stringResource(R.string.trait_light),
                    iconRes = R.drawable.ic_light_sun,
                    options = LightFilter.entries,
                    selected = draft.light,
                    labelRes = { it.labelRes() },
                    onToggle = { draft = draft.copy(light = draft.light.toggle(it)) },
                )
                FilterSection(
                    title = stringResource(R.string.filter_section_watering),
                    iconRes = R.drawable.ic_water_drop,
                    options = WateringFilter.entries,
                    selected = draft.watering,
                    labelRes = { it.labelRes() },
                    onToggle = { draft = draft.copy(watering = draft.watering.toggle(it)) },
                )
                FilterSection(
                    title = stringResource(R.string.filter_section_safety),
                    iconRes = R.drawable.ic_pets_paw,
                    options = SafetyFilter.entries,
                    selected = draft.safety,
                    labelRes = { it.labelRes() },
                    onToggle = { draft = draft.copy(safety = draft.safety.toggle(it)) },
                )
                FilterSection(
                    title = stringResource(R.string.care_level),
                    iconRes = R.drawable.ic_filter_care_level,
                    options = CareLevelFilter.entries,
                    selected = draft.careLevel,
                    labelRes = { it.labelRes() },
                    onToggle = { draft = draft.copy(careLevel = draft.careLevel.toggle(it)) },
                )
                FilterSection(
                    title = stringResource(R.string.mature_size),
                    iconRes = R.drawable.ic_size_ruler,
                    options = MatureSizeFilter.entries,
                    selected = draft.matureSize,
                    labelRes = { it.labelRes() },
                    onToggle = { draft = draft.copy(matureSize = draft.matureSize.toggle(it)) },
                )
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(onClick = { hideThen { onApply(draft) } }) {
                    Text(
                        text = stringResource(R.string.apply),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun <T> FilterSection(
    title: String,
    @DrawableRes iconRes: Int,
    options: List<T>,
    selected: Set<T>,
    labelRes: (T) -> Int,
    onToggle: (T) -> Unit,
) {
    Spacer(Modifier.height(20.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(iconRes),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null,
            modifier = Modifier
                .size(28.dp)
                .background(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(4.dp)
        )

        Spacer(Modifier.width(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.semantics { heading() },
        )
    }
    Spacer(Modifier.height(8.dp))
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { option ->
            FilterOptionChip(
                label = stringResource(labelRes(option)),
                selected = option in selected,
                onToggle = { onToggle(option) },
            )
        }
    }
}

private fun <T> Set<T>.toggle(item: T): Set<T> =
    if (item in this) this - item else this + item
