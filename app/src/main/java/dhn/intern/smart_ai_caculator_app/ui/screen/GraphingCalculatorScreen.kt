package dhn.intern.smart_ai_caculator_app.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dhn.intern.smart_ai_caculator_app.R
import dhn.intern.smart_ai_caculator_app.ui.components.graphing.CustomMathKeypad
import dhn.intern.smart_ai_caculator_app.ui.components.graphing.GraphCanvas
import dhn.intern.smart_ai_caculator_app.ui.viewmodel.FunctionItem
import dhn.intern.smart_ai_caculator_app.ui.viewmodel.GraphingCalculatorViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphingCalculatorScreen(
    navController: NavController,
    viewModel: GraphingCalculatorViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.graphing_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.resetViewport() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Zoom"
                        )
                    }
                    IconButton(onClick = { viewModel.toggleKeypad() }) {
                        Icon(
                            painter = painterResource(R.drawable.basic_caculator),
                            contentDescription = "Toggle Keypad",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Function chips list
            FunctionCardsRow(
                functions = uiState.functions,
                activeIndex = uiState.activeFunctionIndex,
                onSelect = { viewModel.selectFunction(it) },
                onToggleVisibility = { viewModel.toggleFunctionVisibility(it) },
                onRemove = { viewModel.removeFunction(it) },
                onAdd = { viewModel.addFunction("") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            )

            // Canvas takes remaining space
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                GraphCanvas(
                    viewport = uiState.viewport,
                    sampledCurves = uiState.sampledCurves,
                    curveColors = uiState.functions.map { it.color },
                    intersections = uiState.intersections,
                    tracePoint = uiState.tracePoint,
                    traceSpecialPoint = uiState.traceSpecialPoint,
                    onPan = { delta, size -> viewModel.onPan(delta, size) },
                    onZoom = { zoom, center, size -> viewModel.onZoom(zoom, center, size) },
                    onTrace = { offset, size -> viewModel.onTrace(offset, size) }
                )
            }

            // Bottom docked Custom Math Keypad
            AnimatedVisibility(
                visible = uiState.isKeypadVisible,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                CustomMathKeypad(
                    onKeyPress = { key -> viewModel.onKeyPress(key) }
                )
            }
        }
    }
}

@Composable
private fun FunctionCardsRow(
    functions: List<FunctionItem>,
    activeIndex: Int,
    onSelect: (Int) -> Unit,
    onToggleVisibility: (Int) -> Unit,
    onRemove: (Int) -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        itemsIndexed(functions) { index, item ->
            val isSelected = index == activeIndex
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) item.color else MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelect(index) },
                color = if (isSelected) item.color.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = if (isSelected) 4.dp else 0.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Function indicator circle
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(item.color)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = if (item.expression.isBlank()) "f${index + 1}(x)" else "f${index + 1}(x) = ${item.expression}",
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Visibility Toggle Dot
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(if (item.isVisible) item.color else Color.Transparent)
                            .border(1.5.dp, item.color, CircleShape)
                            .clickable { onToggleVisibility(index) }
                    )

                    if (functions.size > 1) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove function",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { onRemove(index) }
                        )
                    }
                }
            }
        }

        // Add function button
        if (functions.size < 5) {
            item {
                Surface(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onAdd() },
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add function",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(18.dp)
                    )
                }
            }
        }
    }
}
