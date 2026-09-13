package dhn.intern.smart_ai_caculator_app.data.source.menu_setting

import dhn.intern.smart_ai_caculator_app.R
import dhn.intern.smart_ai_caculator_app.data.preferences.language.languageNameRes
import dhn.intern.smart_ai_caculator_app.data.model.ChildMenuSettingUI
import dhn.intern.smart_ai_caculator_app.data.model.SettingMenuUI

object SettingMenuData {

    fun getSettingMenuData(selectedCode: String): List<SettingMenuUI> {
        return listOf(
            SettingMenuUI(
                title = R.string.Setting_personalization,
                child = listOf(
                    ChildMenuSettingUI(
                        title = R.string.Setting_language,
                        descrip = languageNameRes(selectedCode),
                        icon = R.drawable.language,
                        navhost = "selectlanguage_screen"
                    ),
                    ChildMenuSettingUI(
                        title = R.string.Setting_app_theme,
                        descrip = R.string.Setting_system,
                        icon = R.drawable.dark_system,
                        navhost = "theme_setting"
                    ),
                )
            ),

            SettingMenuUI(
                title = R.string.Setting_comunicate,
                child = listOf(
                    ChildMenuSettingUI(
                        title = R.string.Setting_feedback,
                        descrip = R.string.Setting_feedback_share,
                        icon = R.drawable.chat,
                        navhost = "feedback_setting"
                    ),
                    ChildMenuSettingUI(
                        title = R.string.Setting_privacy_policy,
                        descrip = R.string.Setting_your_data,
                        icon = R.drawable.policy,
                        navhost = "policy_setting"
                    ),
                )
            ),

            SettingMenuUI(
                title = R.string.Setting_others,
                child = listOf(
                    ChildMenuSettingUI(
                        title = R.string.Setting_share_app,
                        descrip = R.string.Setting_share_friend,
                        icon = R.drawable.share,
                        navhost = "share_setting"
                    ),
                    ChildMenuSettingUI(
                        title = R.string.Setting_rate_us,
                        descrip = R.string.Setting_give_your_rating,
                        icon = R.drawable.star_system,
                        navhost = "rating_setting"
                    ),
                    ChildMenuSettingUI(
                        title = R.string.Setting_about_us,
                        descrip = R.string.Setting_know_about_us,
                        icon = R.drawable.info,
                        navhost = "about_us_setting"
                    ),
                )
            ),
        )

    }

}
