package dhn.intern.smart_ai_caculator_app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import dhn.intern.smart_ai_caculator_app.navigation.AppNavHost
import dhn.intern.smart_ai_caculator_app.activities.theme.Smart_AI_Caculator_AppTheme
import dhn.intern.smart_ai_caculator_app.ui.viewmodel.ThemeViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: ThemeViewModel = koinViewModel()
            val isDark by themeViewModel.isDarkMode.collectAsState()

            Smart_AI_Caculator_AppTheme(
                darkTheme = isDark
            ) {
                val navController = rememberNavController()
                AppNavHost(navController = navController)
            }
        }
    }
}
