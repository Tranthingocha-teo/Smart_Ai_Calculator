package dhn.intern.smart_ai_caculator_app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dhn.intern.smart_ai_caculator_app.R
import dhn.intern.smart_ai_caculator_app.data.local.entity.CalculatorHistoryEntity
import dhn.intern.smart_ai_caculator_app.domain.export.HistoryExportManager
import dhn.intern.smart_ai_caculator_app.domain.export.model.ExportFormat
import dhn.intern.smart_ai_caculator_app.domain.export.model.ExportScope
import dhn.intern.smart_ai_caculator_app.enum.HistorySource
import dhn.intern.smart_ai_caculator_app.ui.components.HalfScreenBottomSheet
import dhn.intern.smart_ai_caculator_app.ui.components.NavBar
import dhn.intern.smart_ai_caculator_app.ui.components.export.ExportHistoryBottomSheet
import dhn.intern.smart_ai_caculator_app.ui.components.export.SingleItemExportBottomSheet
import dhn.intern.smart_ai_caculator_app.ui.components.history.CaculatorHistory
import dhn.intern.smart_ai_caculator_app.ui.components.history.CardItemsChatbox
import dhn.intern.smart_ai_caculator_app.ui.viewmodel.CalculatorViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun HistoryScreen(
    navController: NavHostController,
    source: HistorySource,
    calculatorViewModel: CalculatorViewModel = koinInject(),
    exportManager: HistoryExportManager = koinInject()
) {
    val histories by calculatorViewModel.history.collectAsState()
    val filtered = histories.filter { it.source == source.name }

    var selectedHistory by remember { mutableStateOf<CalculatorHistoryEntity?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    var showBatchExportSheet by remember { mutableStateOf(false) }
    var showSingleExportSheet by remember { mutableStateOf(false) }
    var singleExportTarget by remember { mutableStateOf<CalculatorHistoryEntity?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            NavBar(
                title = R.string.Basic_caculator_history,
                navController = navController,
                contentColor = MaterialTheme.colorScheme.onBackground,
                trailingContent = {
                    val isCurrentEmpty = filtered.isEmpty()
                    IconButton(
                        onClick = {
                            if (isCurrentEmpty) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = context.getString(R.string.export_empty_history_hint),
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            } else {
                                showBatchExportSheet = true
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.share),
                            contentDescription = stringResource(R.string.export_history_title),
                            modifier = Modifier.size(22.dp),
                            tint = if (isCurrentEmpty) {
                                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f)
                            } else {
                                MaterialTheme.colorScheme.onBackground
                            }
                        )
                    }
                }
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            when (source) {
                HistorySource.CALCULATOR,
                HistorySource.GRAPHING_CALCULATOR -> {
                    if (filtered.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 80.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.history),
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = stringResource(R.string.export_empty_history),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.export_empty_history_hint),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 32.dp)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(
                                items = filtered,
                                key = { it.id }
                            ) { historyItem ->
                                CaculatorHistory(
                                    history = historyItem,
                                    onClick = {
                                        selectedHistory = historyItem
                                        showBottomSheet = true
                                    },
                                )
                            }
                        }
                    }
                }

                HistorySource.AI_CHAT -> {
                    CardItemsChatbox(navController = navController)
                }
            }
        }

        // HalfScreenBottomSheet for item options
        HalfScreenBottomSheet(
            show = showBottomSheet,
            history = selectedHistory,
            onDismiss = {
                showBottomSheet = false
                selectedHistory = null
            },
            onMenuItemClick = { menuUi, item ->
                when (menuUi.code) {
                    3 -> {
                        // Copy result
                        clipboardManager.setText(AnnotatedString(item.result))
                        showBottomSheet = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(context.getString(R.string.unit_calculator_copied))
                        }
                    }
                    4 -> {
                        // Share / Export single item
                        showBottomSheet = false
                        singleExportTarget = item
                        showSingleExportSheet = true
                    }
                }
            }
        )

        // Batch Export BottomSheet
        ExportHistoryBottomSheet(
            show = showBatchExportSheet,
            currentSource = source.name,
            totalCountCurrent = filtered.size,
            totalCountAll = histories.size,
            onDismiss = { showBatchExportSheet = false },
            onShare = { config ->
                coroutineScope.launch {
                    val itemsToExport = if (config.scope == ExportScope.CURRENT_SOURCE) filtered else histories
                    exportManager.exportBatch(context, itemsToExport, config, source.name)
                        .onSuccess { result ->
                            exportManager.shareExportResult(context, result)
                            showBatchExportSheet = false
                        }
                        .onFailure { error ->
                            snackbarHostState.showSnackbar(
                                context.getString(R.string.export_error_generic, error.localizedMessage ?: "")
                            )
                        }
                }
            },
            onSaveToDownloads = { config ->
                coroutineScope.launch {
                    val itemsToExport = if (config.scope == ExportScope.CURRENT_SOURCE) filtered else histories
                    try {
                        val exportResult = exportManager.exportBatch(context, itemsToExport, config, source.name).getOrThrow()
                        val savedUri = exportManager.saveToDownloads(context, exportResult).getOrThrow()
                        showBatchExportSheet = false

                        val snackbarAction = snackbarHostState.showSnackbar(
                            message = context.getString(R.string.export_success_saved),
                            actionLabel = context.getString(R.string.export_action_open),
                            duration = SnackbarDuration.Long
                        )
                        if (snackbarAction == SnackbarResult.ActionPerformed) {
                            exportManager.openFile(context, savedUri, exportResult.mimeType)
                        }
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar(
                            context.getString(R.string.export_error_generic, e.localizedMessage ?: "")
                        )
                    }
                }
            }
        )

        // Single Item Export BottomSheet
        SingleItemExportBottomSheet(
            show = showSingleExportSheet,
            historyItem = singleExportTarget,
            onDismiss = {
                showSingleExportSheet = false
                singleExportTarget = null
            },
            onSelectFormat = { item, format ->
                showSingleExportSheet = false
                if (format == ExportFormat.PLAIN_TEXT) {
                    val textToCopy = "${item.expression} = ${item.result}"
                    clipboardManager.setText(AnnotatedString(textToCopy))
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(context.getString(R.string.export_copied_clipboard))
                    }
                } else {
                    coroutineScope.launch {
                        exportManager.exportSingle(context, item, format)
                            .onSuccess { result ->
                                exportManager.shareExportResult(context, result)
                            }
                            .onFailure { error ->
                                snackbarHostState.showSnackbar(
                                    context.getString(R.string.export_error_generic, error.localizedMessage ?: "")
                                )
                            }
                    }
                }
            }
        )
    }
}