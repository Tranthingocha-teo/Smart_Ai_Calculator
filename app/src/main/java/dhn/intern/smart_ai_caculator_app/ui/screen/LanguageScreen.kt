package dhn.intern.smart_ai_caculator_app.ui.screen

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import dhn.intern.smart_ai_caculator_app.R
import dhn.intern.smart_ai_caculator_app.data.preferences.language.LanguageData
import dhn.intern.smart_ai_caculator_app.data.model.LanguageUi
import dhn.intern.smart_ai_caculator_app.ui.components.LoadingScreen
import dhn.intern.smart_ai_caculator_app.ui.viewmodel.AppViewModel
import dhn.intern.smart_ai_caculator_app.ui.viewmodel.LanguageViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LanguageScreen(
    languageViewModel: LanguageViewModel = koinViewModel(),
    navController: NavHostController,
    appViewModel: AppViewModel = koinViewModel()
) {

    val context = LocalContext.current
    val selectedCode by languageViewModel.selectedCode.collectAsState()
    val languages = LanguageData.getLanguages(selectedCode)

    val isLoading by languageViewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically

        ) {
            Text(
                text = stringResource(R.string.select_language),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(R.drawable.select_lang),
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        navController.navigate("home_screen") {
                            popUpTo("selectlanguage_screen") { inclusive = true }
                            launchSingleTop = true
                            appViewModel.markFirstLaunchDone()
                        }
                    },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,

            )
        }

        if (isLoading) {
            LoadingScreen()
            return
        }
        LazyColumn() {
            items(languages) { language ->
                LanguageItem(
                    language = language,
                    onClick = {
                        languageViewModel.selectLanguage(language.code, context as? Activity)
                    }
                )

                Spacer(modifier = Modifier.height(5.dp))

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.secondary,
                    thickness = 2.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }

    }
}

@Composable
fun LanguageItem(
    language: LanguageUi,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = language.icon,
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.width(12.dp))
        if(language.isSelected){
            Text(
                text = stringResource(language.name),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.outline
            )
        }else{
            Text(
                text = stringResource(language.name),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        if (language.isSelected) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(24.dp)
                    .background(MaterialTheme.colorScheme.outline, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.background,
                    modifier = Modifier.size(16.dp)
                )
            }
        }else{
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        shape = CircleShape
                    )
            )
        }

    }
}

