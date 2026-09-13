package dhn.intern.smart_ai_caculator_app.activities.theme


import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    background = LightBackground,
    surface = LightBackground,

    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,

    tertiary = LightTextQuaternary,

    onPrimary = ProgressIndicatorLight,
    surfaceVariant = DisplaySurfaceLight,
    secondary = ProgressTrackLight,

    outline = SelectLanguage,

    primary = LightPrimary,
    onSurfaceVariant = LightTextHint,

    secondaryContainer = LightKeypadNormal,
    errorContainer = LightKeypadAC,

    outlineVariant = LightCard,
    inversePrimary = LightTextField
)

private val DarkColorScheme = darkColorScheme(
    background = DarkBackground,
    surface = DarkBackground,

    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    onPrimary = ProgressIndicatorDark,
    surfaceVariant = DisplaySurfaceDark,
    secondary = ProgressTrackDark,
    outline = SelectLanguage,
    tertiary = DarkTextQuaternary,

    primary = ProgressTrackDark,

    onSurfaceVariant = DarkTextHint,

    secondaryContainer = DarkKeypadNormal,
    errorContainer = DarkKeypadAC,

    outlineVariant = DarktCard,
    inversePrimary = DarkTextField
    )


@Composable
fun Smart_AI_Caculator_AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}