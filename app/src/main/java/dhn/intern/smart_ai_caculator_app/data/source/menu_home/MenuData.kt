package dhn.intern.smart_ai_caculator_app.data.source.menu_home

import dhn.intern.smart_ai_caculator_app.R
import dhn.intern.smart_ai_caculator_app.data.model.MenuUI
import dhn.intern.smart_ai_caculator_app.navigation.NavScreen

object MenuData {

    fun getSettingHistoryData(): List<MenuUI> {
        return listOf(
            MenuUI(
                code = 1,
                name = R.string.history_bottom_insert_formula,
                icon = R.drawable.add_box,
                navhost = "insert_formula_screen"
            ),
            MenuUI(
                code = 2,
                name = R.string.history_bottom_replace,
                icon = R.drawable.autorenew,
                navhost = "replace_formula_screen"
            ),
            MenuUI(
                code = 3,
                name = R.string.history_bottom_copy_resulit,
                icon = R.drawable.content_copy,
                navhost = "copy_result_screen"
            ),
            MenuUI(
                code = 1,
                name = R.string.history_bottom_delete,
                icon = R.drawable.delete,
                navhost = "delete_history_screen"
            ),
        )
    }
    fun getMenuData(selectedCode: String): List<MenuUI> {
        return listOf(
            MenuUI(
                code = 1,
                name = R.string.menu_basic_calculator,
                icon = R.drawable.basic_caculator,
                navhost = NavScreen.BasicCaculatorScreen.route
            ),
            MenuUI(
                code = 2,
                name = R.string.menu_ai_calculator,
                icon = R.drawable.ai_caculator,
                navhost = "ai_caculator"
            ),
            MenuUI(
                code = 3,
                name = R.string.menu_unit_converter,
                icon = R.drawable.unit_converter,
                navhost = "unit_converter"
            ),
            MenuUI(
                code = 4,
                name = R.string.menu_currency_converter,
                icon = R.drawable.currency_converter,
                navhost = "currency_converter"
            ),
            MenuUI(
                code = 5,
                name = R.string.menu_discount_calculator,
                icon = R.drawable.discount_caculator,
                navhost = "discount_caculator"
            ),
            MenuUI(
                code = 6,
                name = R.string.menu_tip_calculator,
                icon = R.drawable.tip_caculator,
                navhost = "tip_caculator"
            ),
            MenuUI(
                code = 7,
                name = R.string.menu_date_calculator,
                icon = R.drawable.date_caculator,
                navhost = "date_caculator"
            ),
            MenuUI(
                code = 8,
                name = R.string.menu_loan_calculator,
                icon = R.drawable.loan_caculator,
                navhost = "loan_caculator"
            ),
            MenuUI(
                code = 9,
                name = R.string.menu_gpa_calculator,
                icon = R.drawable.gpa_caculator,
                navhost = "gpa_caculator"
            ),
            MenuUI(
                code = 10,
                name = R.string.menu_bmi_calculator,
                icon = R.drawable.bmi_caculator,
                navhost = "bmi_caculator"
            ),
            )

    }

}
