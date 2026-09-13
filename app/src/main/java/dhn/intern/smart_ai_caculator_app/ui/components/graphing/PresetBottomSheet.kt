package dhn.intern.smart_ai_caculator_app.ui.components.graphing

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dhn.intern.smart_ai_caculator_app.R
import dhn.intern.smart_ai_caculator_app.data.repository.GraphPreset
import dhn.intern.smart_ai_caculator_app.data.repository.PresetCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetBottomSheet(
    presets: List<GraphPreset>,
    onSelectPreset: (GraphPreset) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedCategory by remember { mutableStateOf<PresetCategory?>(null) }

    val filteredPresets = remember(selectedCategory, presets) {
        if (selectedCategory == null) presets
        else presets.filter { it.category == selectedCategory }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = stringResource(R.string.graphing_presets_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category filter chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("Tất cả") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategory == PresetCategory.POLYNOMIAL,
                        onClick = { selectedCategory = PresetCategory.POLYNOMIAL },
                        label = { Text("Đa thức") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategory == PresetCategory.TRIGONOMETRIC,
                        onClick = { selectedCategory = PresetCategory.TRIGONOMETRIC },
                        label = { Text("Lượng giác") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategory == PresetCategory.RATIONAL,
                        onClick = { selectedCategory = PresetCategory.RATIONAL },
                        label = { Text("Phân thức") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategory == PresetCategory.EXPONENTIAL_LOGARITHMIC,
                        onClick = { selectedCategory = PresetCategory.EXPONENTIAL_LOGARITHMIC },
                        label = { Text("Mũ & Log") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filteredPresets, key = { it.id }) { preset ->
                    PresetItemCard(
                        preset = preset,
                        onClick = {
                            onSelectPreset(preset)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PresetItemCard(
    preset: GraphPreset,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = preset.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (preset.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = preset.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "f(x) = ${preset.expression}",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}
