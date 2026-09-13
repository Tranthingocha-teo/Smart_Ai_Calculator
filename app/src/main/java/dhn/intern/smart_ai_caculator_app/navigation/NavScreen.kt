package dhn.intern.smart_ai_caculator_app.navigation

import dhn.intern.smart_ai_caculator_app.enum.HistorySource

sealed class NavScreen {

    object PlashScreen : NavScreen() {
        const val route = "plash_screen"
    }

    object SelectLanguage : NavScreen() {
        const val route = "selectlanguage_screen"
    }

    object HomeScreen : NavScreen() {
        const val route = "home_screen"
    }

    object SettingScreen : NavScreen() {
        const val route = "setting_screen"
    }

    object BasicCaculatorScreen : NavScreen() {
        const val route = "basic_caculator"
    }

    object AiCaculatorScreen : NavScreen() {
        const val route = "ai_caculator"
    }

    object UnitCaculatorScreen : NavScreen() {
        const val route = "unit_converter"
    }

    object CurreciesScreen : NavScreen() {
        const val route = "currency_converter"
    }

    object DiscountScreen : NavScreen() {
        const val route = "discount_caculator"
    }
    object BMIScreen : NavScreen() {
        const val route = "bmi_caculator"
    }

    object TipCalculatorScreen : NavScreen() {
        const val route = "tip_caculator"
    }

    object DateCalculatorScreen : NavScreen() {
        const val route = "date_caculator"
    }

    object LoanCalculatorScreen : NavScreen() {
        const val route = "loan_caculator"
    }

    object GpaCalculatorScreen : NavScreen() {
        const val route = "gpa_caculator"
    }

    object GraphingCalculatorScreen : NavScreen() {
        const val route = "graphing_calculator"
    }

    object ResultBMIScreen {
        const val route = "result_bmi_screen/{weight}/{heightFt}/{heightIn}/{age}/{gender}/{bmi}"

        fun createRoute(
            weight: Float,
            heightFt: Int,
            heightIn: Int,
            age: Int,
            gender: String,
            bmi: Float
        ): String {
            return "result_bmi_screen/$weight/$heightFt/$heightIn/$age/$gender/$bmi"
        }
    }

    object HistoryScreen {
        const val route = "history_screen/{source}"

        fun createRoute(source: HistorySource): String {
            return "history_screen/${source.name}"
        }
    }
}