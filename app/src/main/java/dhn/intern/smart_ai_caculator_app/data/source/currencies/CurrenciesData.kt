package dhn.intern.smart_ai_caculator_app.data.source.currencies

object CurrenciesData {
    fun getCurrenciesData(): List<CurrenciesUi> {
        return listOf(
            CurrenciesUi(
                image = "\uD83C\uDDFA\uD83C\uDDF8",
                title = "USD",
                des = "United States Dollar"
            ),
            CurrenciesUi(
                image = "\uD83C\uDDEA\uD83C\uDDFA",
                title = "EUR",
                des = "Euro"
            ),
            CurrenciesUi(
                image = "\uD83C\uDDEC\uD83C\uDDE7",
                title = "GBP",
                des = "British Pound Sterling"
            ),
            CurrenciesUi(
                image = "\uD83C\uDDEF\uD83C\uDDF5",
                title = "JPY",
                des = "Japanese Yen"
            ),
            CurrenciesUi(
                image = "\uD83C\uDDE8\uD83C\uDDF3 ",
                title = "CNY",
                des = "Chinese Yuan"
            ),
        )
    }
}