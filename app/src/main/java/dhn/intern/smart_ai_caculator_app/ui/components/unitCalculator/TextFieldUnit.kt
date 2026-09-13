package dhn.intern.smart_ai_caculator_app.ui.components.unitCalculator

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import dhn.intern.smart_ai_caculator_app.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UnitInputField(
    label: String,
    value: String,
    isActive: Boolean,
    iconRes: Int,
    onFocus: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val copiedMessage = stringResource(R.string.unit_calculator_copied)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable {
                    onClick()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .clip(shape = shape)
                .fillMaxWidth()
                .height(60.dp)
                .border(
                    width = if (isActive) 2.dp else 1.dp,
                    color = if (isActive)
                        MaterialTheme.colorScheme.outline.copy(0.5f)
                    else
                        MaterialTheme.colorScheme.outline.copy(0.2f),
                    shape = RoundedCornerShape(10.dp)
                )
                .combinedClickable(
                    onClick = onFocus,
                    onLongClick = {
                        if (value.isNotEmpty() && value != "0") {
                            clipboardManager.setText(AnnotatedString(value))
                            Toast.makeText(context, copiedMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
        ) {
            TextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                modifier = Modifier.fillMaxSize(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(0.2f),
                    disabledTextColor = MaterialTheme.colorScheme.onBackground,
                    disabledIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }
    }
}