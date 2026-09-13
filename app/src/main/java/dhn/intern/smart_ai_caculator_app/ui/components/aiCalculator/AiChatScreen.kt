package dhn.intern.smart_ai_caculator_app.ui.components.aiCalculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dhn.intern.smart_ai_caculator_app.R
import dhn.intern.smart_ai_caculator_app.ui.components.NavBar_basic

@Composable
fun AiChatScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()


    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            NavBar_basic(
                title = R.string.title_ai_calculator,
                icon = R.drawable.history_time,
                navController = navController,
                onclick = {

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        },
        bottomBar = {
            TextFiledAiCalculator(
                generating = false,
                modifier = Modifier.imePadding()
            )
        },
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.primary),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                UserMessageBubble(
                    message = "What is the result of 25 multiplied by 4?",
                    image = null,
                    time = "10:31 AM",
                    modifier = Modifier.padding(16.dp)
                )
            }
            item {
                AiMessageBubble(
                    message = "Hello! How can I assist you with your calculations today?",
                    time = "10:30 AM",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}