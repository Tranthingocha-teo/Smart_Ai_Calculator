package dhn.intern.smart_ai_caculator_app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import dhn.intern.smart_ai_caculator_app.ui.components.NavBar_basic
import dhn.intern.smart_ai_caculator_app.R
import dhn.intern.smart_ai_caculator_app.enum.HistorySource
import dhn.intern.smart_ai_caculator_app.ui.components.keypad.ScientificKeypad
import dhn.intern.smart_ai_caculator_app.ui.components.keypad.SimpleKeypad
import dhn.intern.smart_ai_caculator_app.navigation.NavScreen
import dhn.intern.smart_ai_caculator_app.ui.viewmodel.CalculatorViewModel
import org.koin.androidx.compose.koinViewModel



@Composable
fun BasicCaculatorScreen(
    navController: NavHostController,
    calculatorViewModel: CalculatorViewModel = koinViewModel()
    ) {
    var isScientific by rememberSaveable{ mutableStateOf(false) }

    val expression by calculatorViewModel.expression.collectAsState()
    val result by calculatorViewModel.result.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary),
        ) {
            NavBar_basic(
                title = R.string.title_ai_calculator,
                icon = R.drawable.ai_caculator,
                navController = navController,
                onclick = {
                    //Navigation to AI Calculator Screen
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
            DisplayInputNumber(
                expression = expression,
                result = result,
                isScientific = isScientific,
                onToggleMode = {
                    isScientific = !isScientific
                },
                navController = navController
            )
            Spacer(modifier = Modifier.weight(1f))

            if (isScientific) {
                ScientificKeypad { key ->
                    calculatorViewModel.onKeyPress(key)
                }
            } else {
                SimpleKeypad { key ->
                    calculatorViewModel.onKeyPress(key)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

        }
    }
}

@Composable
fun DisplayInputNumber(
    isScientific: Boolean,
    onToggleMode: () -> Unit,
    navController: NavHostController,
    expression: String,
    result: String
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight * 0.35f)
            .clip(
                RoundedCornerShape(
                    bottomStart = 24.dp,
                    bottomEnd = 24.dp
                )
            )
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = expression.ifEmpty { "0" },
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 60.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = result,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.tertiary,
                    textAlign = TextAlign.End
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        TextBasicCaculatorScreen(
                            icon = R.drawable.swap,
                            title = if (isScientific)
                                    R.string.Basic_caculator_scientific
                                    else
                                    R.string.Basic_caculator_simple,
                            onClick = onToggleMode
                        )
                    }
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(40.dp)
                                .background(MaterialTheme.colorScheme.tertiary)
                        )
                    }
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        TextBasicCaculatorScreen(
                            icon = R.drawable.history,
                            title = R.string.Basic_caculator_history,
                            onClick = {
                                navController.navigate(
                                    NavScreen.HistoryScreen.createRoute(
                                        HistorySource.CALCULATOR
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }

    }
}

@Composable
fun TextBasicCaculatorScreen(
    icon: Int,
    title: Int,
    onClick: () -> Unit

) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.surfaceVariant),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(icon),
            modifier = Modifier
                .size(28.dp),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,

        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}