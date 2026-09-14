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

import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import dhn.intern.smart_ai_caculator_app.ui.viewmodel.AiChatViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AiChatScreen(
    navController: NavHostController,
    initialPrompt: String = "",
    onPromptConsumed: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AiChatViewModel = koinViewModel(),
) {
    val listState = rememberLazyListState()
    val messages by viewModel.messages.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(initialPrompt) {
        if (initialPrompt.isNotBlank()) {
            viewModel.sendMessage(initialPrompt)
            onPromptConsumed()
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

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
                generating = isGenerating,
                initialText = "",
                onSend = { viewModel.sendMessage(it) },
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
            items(messages, key = { it.id }) { msg ->
                if (msg.isFromUser) {
                    UserMessageBubble(
                        message = msg.text,
                        image = null,
                        time = msg.timestamp,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    AiMessageBubble(
                        message = msg.text,
                        time = msg.timestamp,
                        modifier = Modifier.padding(16.dp),
                        onAction = {
                            clipboardManager.setText(AnnotatedString(msg.text))
                        }
                    )
                }
            }
        }
    }
}