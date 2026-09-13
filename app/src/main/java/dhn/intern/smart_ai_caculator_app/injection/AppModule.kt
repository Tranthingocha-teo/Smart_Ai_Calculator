package dhn.intern.smart_ai_caculator_app.injection

val appModule = listOf(
    languageModule,

    ThemesModule,

    //key module the first ínstall app
    FirstAppModule,

    calculatorModule,

    databaseModule,

    bmiModule
)
