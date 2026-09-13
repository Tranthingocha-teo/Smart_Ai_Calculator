package dhn.intern.smart_ai_caculator_app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dhn.intern.smart_ai_caculator_app.R
import dhn.intern.smart_ai_caculator_app.data.local.entity.CalculatorHistoryEntity
import dhn.intern.smart_ai_caculator_app.enum.HistorySource
import dhn.intern.smart_ai_caculator_app.ui.components.HalfScreenBottomSheet
import dhn.intern.smart_ai_caculator_app.ui.components.NavBar
import dhn.intern.smart_ai_caculator_app.ui.components.history.CaculatorHistory
import dhn.intern.smart_ai_caculator_app.ui.components.history.CardItemsChatbox
import dhn.intern.smart_ai_caculator_app.ui.viewmodel.CalculatorViewModel
import org.koin.compose.koinInject

@Composable
fun HistoryScreen(
    navController: NavHostController,
    source: HistorySource,
    calculatorViewModel: CalculatorViewModel = koinInject()
) {
    val histories by calculatorViewModel.history.collectAsState()
    var selectedHistory by remember { mutableStateOf<CalculatorHistoryEntity?>(null) }
    var showBottomSheet by remember {
        mutableStateOf(false)
    }
    Scaffold(
        topBar = {
            NavBar(
                title = R.string.Basic_caculator_history,
                navController = navController,
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
    ) {innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            when (source) {
                HistorySource.CALCULATOR -> {
                    LazyColumn {
                        items(
                            items = histories,
                            key = { it.id }
                        ) { historyItem ->
                            CaculatorHistory(
                                history = historyItem,
                                onClick = {
                                    selectedHistory = historyItem
                                    showBottomSheet = true
                                          },
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                }

                HistorySource.AI_CHAT -> {
                    CardItemsChatbox(navController = navController)
                }
            }
        }
        HalfScreenBottomSheet(
            show = showBottomSheet,
            history = selectedHistory,
            onDismiss = {
                showBottomSheet = false
                selectedHistory = null
            }
        )


    }
}