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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
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

    var showClearConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(initialPrompt) {
        if (initialPrompt.isNotBlank()) {
            viewModel.sendMessage(initialPrompt)
            onPromptConsumed()
        }
    }

    LaunchedEffect(messages.size, messages.lastOrNull()?.text) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = {
                Text(
                    text = "Xóa cuộc trò chuyện",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Text(
                    text = "Bạn có chắc chắn muốn xóa toàn bộ cuộc trò chuyện để bắt đầu phiên mới không?",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearConversation()
                        showClearConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) {
                    Text("Xóa", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearConfirmDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF565F66))
                ) {
                    Text("Hủy")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            NavBar_basic(
                title = R.string.title_ai_calculator,
                icon = R.drawable.delete,
                navController = navController,
                onclick = {
                    showClearConfirmDialog = true
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
                    val displayText = if (msg.text.isEmpty() && isGenerating) {
                        "🤖 AI đang suy nghĩ và phân tích..."
                    } else {
                        msg.text
                    }
                    AiMessageBubble(
                        message = displayText,
                        time = msg.timestamp,
                        modifier = Modifier.padding(16.dp),
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(displayText))
                        },
                        onRewrite = {
                            viewModel.rewriteMessage(msg.id)
                        }
                    )
                }
            }
        }
    }
}