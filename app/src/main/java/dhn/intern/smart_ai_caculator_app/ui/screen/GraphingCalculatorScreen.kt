package dhn.intern.smart_ai_caculator_app.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dhn.intern.smart_ai_caculator_app.R
import dhn.intern.smart_ai_caculator_app.ui.components.graphing.CustomMathKeypad
import dhn.intern.smart_ai_caculator_app.ui.components.graphing.GraphCanvas
import dhn.intern.smart_ai_caculator_app.ui.components.graphing.PresetBottomSheet
import dhn.intern.smart_ai_caculator_app.ui.viewmodel.FunctionItem
import dhn.intern.smart_ai_caculator_app.ui.viewmodel.GraphingCalculatorViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphingCalculatorScreen(
    navController: NavController,
    viewModel: GraphingCalculatorViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val activeFunction = uiState.functions.getOrNull(uiState.activeFunctionIndex)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()
    var showPresetSheet by remember { mutableStateOf(false) }

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
                    IconButton(onClick = { showPresetSheet = true }) {
                        Icon(
                            painter = painterResource(R.drawable.library),
                            contentDescription = stringResource(R.string.graphing_presets),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = {
                        coroutineScope.launch {
                            try {
                                val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                                viewModel.exportGraphImage(context, bitmap)
                            } catch (_: Exception) {}
                        }
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.share),
                            contentDescription = stringResource(R.string.graphing_share),
                            modifier = Modifier.size(20.dp)
                        )
                    }
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
            // 1. Function chips row (f1, f2, ...)
            FunctionCardsRow(
                functions = uiState.functions,
                activeIndex = uiState.activeFunctionIndex,
                onSelect = { viewModel.selectFunction(it) },
                onToggleVisibility = { viewModel.toggleFunctionVisibility(it) },
                onRemove = { viewModel.removeFunction(it) },
                onAdd = { viewModel.addFunction("") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )

            // 2. Active Function Formula Bar
            if (activeFunction != null) {
                ActiveFormulaBar(
                    functionIndex = uiState.activeFunctionIndex,
                    functionItem = activeFunction,
                    onClear = { viewModel.clearActiveFunction() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                )
            }

            // 3. Interactive Graph Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .drawWithContent {
                        graphicsLayer.record {
                            this@drawWithContent.drawContent()
                        }
                        drawLayer(graphicsLayer)
                    }
            ) {
                GraphCanvas(
                    viewport = uiState.viewport,
                    renderedCurves = uiState.renderedCurves,
                    intersections = uiState.intersections,
                    tracePoint = uiState.tracePoint,
                    traceSpecialPoint = uiState.traceSpecialPoint,
                    isTraceMode = uiState.isTraceMode,
                    onPan = { delta, size -> viewModel.onPan(delta, size) },
                    onZoom = { zoom, center, size -> viewModel.onZoom(zoom, center, size) },
                    onZoomIn = { viewModel.zoomIn() },
                    onZoomOut = { viewModel.zoomOut() },
                    onResetZoom = { viewModel.resetViewport() },
                    onTrace = { offset, size -> viewModel.onTrace(offset, size) },
                    onToggleTraceMode = { viewModel.toggleTraceMode() },
                    onClearTrace = { viewModel.clearTracePoint() }
                )

                // Floating "Open Keypad" button when keypad is collapsed
                if (!uiState.isKeypadVisible) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { viewModel.setKeypadVisible(true) },
                        color = MaterialTheme.colorScheme.primaryContainer,
                        tonalElevation = 6.dp,
                        shadowElevation = 6.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.basic_caculator),
                                contentDescription = "Open Keypad",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Bàn phím toán học",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            // 4. Bottom Docked Custom Math Keypad
            AnimatedVisibility(
                visible = uiState.isKeypadVisible,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                CustomMathKeypad(
                    onKeyPress = { key -> viewModel.onKeyPress(key) },
                    onDismiss = { viewModel.setKeypadVisible(false) }
                )
            }
        }

        if (showPresetSheet) {
            PresetBottomSheet(
                presets = viewModel.getPresets(),
                onSelectPreset = { preset -> viewModel.applyPreset(preset) },
                onDismiss = { showPresetSheet = false }
            )
        }
    }
}

@Composable
private fun ActiveFormulaBar(
    functionIndex: Int,
    functionItem: FunctionItem,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Function prefix tag (colored)
                Text(
                    text = "f${functionIndex + 1}(x) =",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = functionItem.color
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Formula expression
                Text(
                    text = if (functionItem.expression.isEmpty()) "Nhập hàm số..." else functionItem.expression,
                    fontSize = 17.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = if (functionItem.expression.isEmpty()) FontWeight.Normal else FontWeight.Bold,
                    color = if (functionItem.expression.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (functionItem.expression.isNotEmpty()) {
                    IconButton(
                        onClick = onClear,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(24.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear formula",
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Error notice if formula has syntax error
            if (functionItem.errorMessage != null && functionItem.expression.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "⚠️ ${functionItem.errorMessage}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium
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
                    .clip(RoundedCornerShape(10.dp))
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) item.color else MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelect(index) },
                color = if (isSelected) item.color.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = if (isSelected) 3.dp else 0.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Single visibility / color indicator dot (tap toggles visibility)
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clickable { onToggleVisibility(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(if (item.isVisible) item.color else Color.Transparent)
                                .border(2.dp, item.color, CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = if (item.expression.isBlank()) "f${index + 1}(x)" else "f${index + 1}: ${item.expression}",
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (functions.size > 1) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clickable { onRemove(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove function",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // Add function button
        if (functions.size < 5) {
            item {
                Surface(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .clickable { onAdd() },
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add function",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
