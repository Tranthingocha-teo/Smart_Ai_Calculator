package dhn.intern.smart_ai_caculator_app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dhn.intern.smart_ai_caculator_app.enum.HistorySource
import dhn.intern.smart_ai_caculator_app.ui.screen.bmi.BMIScreen
import dhn.intern.smart_ai_caculator_app.ui.screen.BasicCaculatorScreen
import dhn.intern.smart_ai_caculator_app.ui.screen.ConverterScreen
import dhn.intern.smart_ai_caculator_app.ui.screen.DiscountCalculatorScreen
import dhn.intern.smart_ai_caculator_app.ui.screen.HistoryScreen
import dhn.intern.smart_ai_caculator_app.ui.screen.HomeScreen
import dhn.intern.smart_ai_caculator_app.ui.screen.LanguageScreen
import dhn.intern.smart_ai_caculator_app.ui.screen.SettingScreen
import dhn.intern.smart_ai_caculator_app.ui.screen.SplashScreen
import dhn.intern.smart_ai_caculator_app.ui.screen.UnitCalculatorScreen
import dhn.intern.smart_ai_caculator_app.ui.screen.TipCalculatorScreen
import dhn.intern.smart_ai_caculator_app.ui.screen.DateCalculatorScreen
import dhn.intern.smart_ai_caculator_app.ui.screen.LoanCalculatorScreen
import dhn.intern.smart_ai_caculator_app.ui.screen.GpaCalculatorScreen
import dhn.intern.smart_ai_caculator_app.ui.screen.GraphingCalculatorScreen
import dhn.intern.smart_ai_caculator_app.ui.screen.ai_caculator_screen
import dhn.intern.smart_ai_caculator_app.ui.viewmodel.AppViewModel
import dhn.intern.smart_ai_caculator_app.ui.viewmodel.CalculatorViewModel
import dhn.intern.smart_ai_caculator_app.ui.viewmodel.GraphingCalculatorViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavScreen.PlashScreen.route,
    appViewModel: AppViewModel = koinViewModel()
) {
    val isFirstLaunch by appViewModel.isFirstLaunch.collectAsState()
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavScreen.PlashScreen.route) {
            SplashScreen()
            LaunchedEffect(isFirstLaunch) {
                delay(1200)

                val target = if (isFirstLaunch) {
                    NavScreen.SelectLanguage.route
                } else {
                    NavScreen.HomeScreen.route
                }

                navController.navigate(target) {
                    popUpTo(NavScreen.PlashScreen.route) {
                        inclusive = true
                    }
                }
            }
        }


        composable(NavScreen.SelectLanguage.route){
            LanguageScreen(
                navController = navController
            )
        }

        composable(NavScreen.HomeScreen.route){
            HomeScreen(
                navController = navController
            )
        }

        composable(NavScreen.SettingScreen.route){
            SettingScreen(
                navController = navController
            )
        }

        composable(
            route = "${NavScreen.BasicCaculatorScreen.route}?expr={expr}",
            arguments = listOf(
                navArgument("expr") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val initialExpr = backStackEntry.arguments?.getString("expr")
            val calculatorViewModel: CalculatorViewModel = koinViewModel()
            LaunchedEffect(initialExpr) {
                if (!initialExpr.isNullOrBlank()) {
                    calculatorViewModel.setExpression(initialExpr)
                }
            }
            BasicCaculatorScreen(
                navController = navController,
                calculatorViewModel = calculatorViewModel
            )
        }
        composable(NavScreen.AiCaculatorScreen.route){
            ai_caculator_screen(
                navController = navController
            )
        }

        composable(NavScreen.UnitCaculatorScreen.route){
            UnitCalculatorScreen(
                navController = navController
            )
        }
        composable(NavScreen.CurreciesScreen.route){
            ConverterScreen(
                modifier = Modifier,
                navController = navController
            )
        }
        composable(NavScreen.DiscountScreen.route){
            DiscountCalculatorScreen(
                modifier = Modifier,
                navController = navController
            )
        }
        composable(NavScreen.BMIScreen.route){
            BMIScreen(
                navController = navController,
                modifier = Modifier
            )
        }
        composable(NavScreen.TipCalculatorScreen.route){
            TipCalculatorScreen(
                navController = navController,
                modifier = Modifier
            )
        }
        composable(NavScreen.DateCalculatorScreen.route){
            DateCalculatorScreen(
                navController = navController,
                modifier = Modifier
            )
        }
        composable(NavScreen.LoanCalculatorScreen.route){
            LoanCalculatorScreen(
                navController = navController,
                modifier = Modifier
            )
        }
        composable(NavScreen.GpaCalculatorScreen.route){
            GpaCalculatorScreen(
                navController = navController,
                modifier = Modifier
            )
        }
        composable(
            route = "${NavScreen.GraphingCalculatorScreen.route}?expr={expr}",
            arguments = listOf(
                navArgument("expr") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val initialExpr = backStackEntry.arguments?.getString("expr")
            val graphingViewModel: GraphingCalculatorViewModel = koinViewModel()
            LaunchedEffect(initialExpr) {
                if (!initialExpr.isNullOrBlank()) {
                    graphingViewModel.updateFunctionExpression(0, initialExpr)
                }
            }
            GraphingCalculatorScreen(
                navController = navController,
                viewModel = graphingViewModel
            )
        }

        composable(
            route = NavScreen.ResultBMIScreen.route,
            arguments = listOf(
                navArgument("weight") { type = NavType.FloatType },
                navArgument("heightFt") { type = NavType.IntType },
                navArgument("heightIn") { type = NavType.IntType },
                navArgument("age") { type = NavType.IntType },
                navArgument("gender") { type = NavType.StringType },
                navArgument("bmi") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val weight = backStackEntry.arguments?.getFloat("weight") ?: 0f
            val heightFt = backStackEntry.arguments?.getInt("heightFt") ?: 0
            val heightIn = backStackEntry.arguments?.getInt("heightIn") ?: 0
            val age = backStackEntry.arguments?.getInt("age") ?: 0
            val gender = backStackEntry.arguments?.getString("gender") ?: "Male"
            val bmi = backStackEntry.arguments?.getFloat("bmi") ?: 0f

            dhn.intern.smart_ai_caculator_app.ui.screen.bmi.ResultBMIScreen(
                navController = navController,
                modifier = Modifier,
                weight = weight,
                heightFt = heightFt,
                heightIn = heightIn,
                age = age,
                gender = gender,
                bmi = bmi
            )
        }

        composable(
            route = NavScreen.HistoryScreen.route,
            arguments = listOf(
                navArgument("source") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val source = backStackEntry.arguments
                ?.getString("source")
                ?.let { HistorySource.valueOf(it) }
                ?: HistorySource.CALCULATOR

            HistoryScreen(
                navController = navController,
                source = source
            )
        }
    }
}