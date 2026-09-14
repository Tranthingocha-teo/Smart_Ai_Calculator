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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
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
    val currentApiKey by viewModel.currentApiKey.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    var showApiKeyDialog by remember { mutableStateOf(false) }
    var inputKey by remember(currentApiKey) { mutableStateOf(currentApiKey) }

    LaunchedEffect(initialPrompt) {
        if (initialPrompt.isNotBlank()) {
            viewModel.sendMessage(initialPrompt)
            onPromptConsumed()
        }
    }

    LaunchedEffect(messages.size, isGenerating) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    if (showApiKeyDialog) {
        AlertDialog(
            onDismissRequest = { showApiKeyDialog = false },
            title = {
                Text(
                    text = "Cấu hình Google Gemini AI",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column {
                    Text(
                        text = "Nhập Gemini API Key để kích hoạt khả năng đàm thoại tự nhiên và giải toán nâng cao bằng mô hình Gemini 1.5 Flash.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = inputKey,
                        onValueChange = { inputKey = it },
                        label = { Text("Gemini API Key") },
                        placeholder = { Text("AIzaSy...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "💡 Lấy key miễn phí tại: aistudio.google.com",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveApiKey(inputKey)
                        showApiKeyDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B57D0))
                ) {
                    Text("Lưu", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showApiKeyDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF565F66))
                ) {
                    Text("Đóng")
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
                icon = R.drawable.setting,
                navController = navController,
                onclick = {
                    inputKey = currentApiKey
                    showApiKeyDialog = true
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

            if (isGenerating) {
                item {
                    AiMessageBubble(
                        message = "🤖 AI đang suy nghĩ và phân tích...",
                        time = "Vừa xong",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}