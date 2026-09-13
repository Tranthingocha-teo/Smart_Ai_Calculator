package dhn.intern.smart_ai_caculator_app.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dhn.intern.smart_ai_caculator_app.R
import dhn.intern.smart_ai_caculator_app.data.model.ChildMenuSettingUI
import dhn.intern.smart_ai_caculator_app.data.source.menu_setting.SettingMenuData
import dhn.intern.smart_ai_caculator_app.data.model.SettingMenuUI
import dhn.intern.smart_ai_caculator_app.ui.components.NavBar
import dhn.intern.smart_ai_caculator_app.ui.components.ThemeSwitchItem
import dhn.intern.smart_ai_caculator_app.ui.viewmodel.LanguageViewModel
import dhn.intern.smart_ai_caculator_app.ui.viewmodel.ThemeViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun SettingScreen(
    navController: NavHostController,
    languageViewModel: LanguageViewModel = koinViewModel(),
    themeViewModel: ThemeViewModel = koinViewModel()
    ) {
    val context = LocalContext.current
    val versionName = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: Exception) {
            "Unknown"
        }
    }
    val selectedCode by languageViewModel.selectedCode.collectAsState()

    val settingMenus = remember(selectedCode) {
        SettingMenuData.getSettingMenuData(selectedCode)
    }

    Scaffold(
        topBar = {NavBar(
            navController,
            title = R.string.Setting_title,
            contentColor = MaterialTheme.colorScheme.onBackground
        )},

    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(settingMenus) { section ->
                SettingSectionCard(
                    section = section,
                    onItemClick = { navController.navigate(it) },
                    themeViewModel = themeViewModel
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(
                        R.string.version_name_app,
                        versionName ?: "Not found Version Name"
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
@Composable
fun SettingSectionCard(
    section: SettingMenuUI,
    onItemClick: (String) -> Unit,
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    val isDark by themeViewModel.isDarkMode.collectAsState()
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = stringResource(id = section.title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            section.child.forEachIndexed { index, item ->
                SettingChildItem(
                    item = item,
                    onClick = {onItemClick(item.navhost)},
                    isDark = isDark,
                    themeViewModel = themeViewModel
                )

                if (index != section.child.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}

@Composable
fun SettingChildItem(
    item: ChildMenuSettingUI,
    isDark: Boolean,
    onClick: () -> Unit,
    themeViewModel: ThemeViewModel = koinViewModel()
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = item.icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = stringResource(id = item.title),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(id = item.descrip),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if(item.navhost != "theme_setting"){
            Icon(
                painter = painterResource(R.drawable.back),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(18.dp)
            )
        }else{
            ThemeSwitchItem(
                isDark = isDark,
                onCheckedChange = themeViewModel::toggleDarkMode
            )
        }
    }
}
