package dhn.intern.smart_ai_caculator_app.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.toDateTimeString(): String {
    val formatter = SimpleDateFormat("HH:mm MMM dd, yyyy", Locale.getDefault())
    return formatter.format(Date(this))
}
