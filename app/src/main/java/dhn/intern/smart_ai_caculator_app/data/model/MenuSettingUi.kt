package dhn.intern.smart_ai_caculator_app.data.model

data class SettingMenuUI(
    val title: Int,
    val child: List<ChildMenuSettingUI>
)
data class ChildMenuSettingUI(
    val title: Int,
    val descrip: Int,
    val icon: Int,
    val navhost: String
)