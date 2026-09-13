package dhn.intern.smart_ai_caculator_app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dhn.intern.smart_ai_caculator_app.enum.AiScanKey
import dhn.intern.smart_ai_caculator_app.ui.components.aiCalculator.AiChatScreen
import dhn.intern.smart_ai_caculator_app.ui.components.aiCalculator.AiScanScreen
import dhn.intern.smart_ai_caculator_app.ui.components.aiCalculator.BottomScanAndChat

@Composable
fun ai_caculator_screen(
    navController: NavHostController
) {
    var currentMode by rememberSaveable {
        mutableStateOf(AiScanKey.SCAN)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        when(currentMode) {
            AiScanKey.SCAN ->{
                AiScanScreen(
                    navController = navController,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
            AiScanKey.CHAT ->{
                AiChatScreen(
                    navController = navController,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 95.dp)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    if(currentMode == AiScanKey.SCAN) Color.Transparent
                    else MaterialTheme.colorScheme.primary)
        ) {
            BottomScanAndChat(
                selected = currentMode,
                onSelect = { currentMode = it },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}