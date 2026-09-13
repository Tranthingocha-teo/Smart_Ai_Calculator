package dhn.intern.smart_ai_caculator_app.ui.screen

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import dhn.intern.smart_ai_caculator_app.R
import dhn.intern.smart_ai_caculator_app.ui.components.keypad.UnitKeypad
import java.util.Locale

enum class TipActiveField {
    BILL, PEOPLE, TIP, TAX
}

enum class TipMode {
    PERCENTAGE, VALUE
}

@Composable
fun TipCalculatorScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var tipOnPostTax by remember { mutableStateOf(true) }
    var showTipOnDropdown by remember { mutableStateOf(false) }

    var tipMode by remember { mutableStateOf(TipMode.PERCENTAGE) }
    var showTipModeDropdown by remember { mutableStateOf(false) }

    var bill by remember { mutableStateOf("") }
    var people by remember { mutableIntStateOf(1) }
    var tipInput by remember { mutableStateOf("0") }
    var taxPercent by remember { mutableStateOf("0") }

    var activeField by remember { mutableStateOf<TipActiveField?>(null) }

    // Colors matching Figma
    val fieldBackground = MaterialTheme.colorScheme.inversePrimary
    val fieldBorder = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
    val accentBlue = MaterialTheme.colorScheme.outline

    // Calculation logic
    val billNum = bill.toDoubleOrNull() ?: 0.0
    val taxPct = (taxPercent.toDoubleOrNull() ?: 0.0) / 100.0
    val numPeople = if (people < 1) 1 else people

    val taxAmount = billNum * taxPct

    val tipAmount = when (tipMode) {
        TipMode.PERCENTAGE -> {
            val tipPct = (tipInput.toDoubleOrNull() ?: 0.0) / 100.0
            if (tipOnPostTax) (billNum + taxAmount) * tipPct else billNum * tipPct
        }
        TipMode.VALUE -> {
            tipInput.toDoubleOrNull() ?: 0.0
        }
    }

    val totalAmount = billNum + taxAmount + tipAmount
    val totalPerPerson = totalAmount / numPeople
    val tipPerPerson = tipAmount / numPeople

    val hasInput = bill.isNotEmpty() && billNum > 0

    val totalStr = if (hasInput) String.format(Locale.US, "%.2f", totalAmount) else "- -"
    val totalPerPersonStr = if (hasInput) String.format(Locale.US, "%.2f", totalPerPerson) else "- -"
    val tipStr = if (hasInput) String.format(Locale.US, "%.2f", tipAmount) else "- -"
    val tipPerPersonStr = if (hasInput) String.format(Locale.US, "%.2f", tipPerPerson) else "- -"

    Scaffold(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.back_navbar),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { navController.popBackStack() },
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.tip_calculator_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(R.drawable.share),
                    contentDescription = "Share",
                    modifier = Modifier
                        .size(26.dp)
                        .clickable {
                            val shareBody = "Tip Calculation:\nBill: $bill\nPeople: $numPeople\nTip: $tipStr\nTotal: $totalStr\nPer Person: $totalPerPersonStr"
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareBody)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Tip Summary"))
                        },
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    painter = painterResource(R.drawable.autorenew),
                    contentDescription = "Reset",
                    modifier = Modifier
                        .size(26.dp)
                        .clickable {
                            bill = ""
                            people = 1
                            tipInput = "0"
                            taxPercent = "0"
                            tipOnPostTax = true
                            tipMode = TipMode.PERCENTAGE
                            activeField = null
                        },
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    activeField = null
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(bottom = if (activeField != null) 380.dp else 24.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Tip on selector
                Text(
                    text = stringResource(R.string.tip_calculator_tip_on),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, fieldBorder, RoundedCornerShape(14.dp))
                        .background(fieldBackground)
                        .clickable { showTipOnDropdown = true }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (tipOnPostTax) stringResource(R.string.tip_calculator_post_tax) else stringResource(R.string.tip_calculator_pre_tax),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "⇅",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    DropdownMenu(
                        expanded = showTipOnDropdown,
                        onDismissRequest = { showTipOnDropdown = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.tip_calculator_post_tax)) },
                            onClick = {
                                tipOnPostTax = true
                                showTipOnDropdown = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.tip_calculator_pre_tax)) },
                            onClick = {
                                tipOnPostTax = false
                                showTipOnDropdown = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bill and People Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Bill
                    Column(modifier = Modifier.weight(1.2f)) {
                        Text(
                            text = stringResource(R.string.tip_calculator_bill),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(
                                    width = if (activeField == TipActiveField.BILL) 2.dp else 1.dp,
                                    color = if (activeField == TipActiveField.BILL) accentBlue else fieldBorder,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .background(fieldBackground)
                                .clickable { activeField = TipActiveField.BILL }
                                .padding(horizontal = 14.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = if (bill.isEmpty()) "- -" else bill,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (bill.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // People
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.tip_calculator_people),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(
                                    width = if (activeField == TipActiveField.PEOPLE) 2.dp else 1.dp,
                                    color = if (activeField == TipActiveField.PEOPLE) accentBlue else fieldBorder,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .background(fieldBackground)
                                .padding(horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = { if (people > 1) people-- },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("-", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable { activeField = TipActiveField.PEOPLE },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$people",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            IconButton(
                                onClick = { people++ },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("+", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tip Percentage / Value
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { showTipModeDropdown = true }
                        ) {
                            Text(
                                text = if (tipMode == TipMode.PERCENTAGE) stringResource(R.string.tip_calculator_tip_percentage) else "Tip (Value)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("▾", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            DropdownMenu(
                                expanded = showTipModeDropdown,
                                onDismissRequest = { showTipModeDropdown = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Percentage (%)", color = if (tipMode == TipMode.PERCENTAGE) accentBlue else MaterialTheme.colorScheme.onSurface) },
                                    onClick = {
                                        tipMode = TipMode.PERCENTAGE
                                        showTipModeDropdown = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Value", color = if (tipMode == TipMode.VALUE) accentBlue else MaterialTheme.colorScheme.onSurface) },
                                    onClick = {
                                        tipMode = TipMode.VALUE
                                        showTipModeDropdown = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                width = if (activeField == TipActiveField.TIP) 2.dp else 1.dp,
                                color = if (activeField == TipActiveField.TIP) accentBlue else fieldBorder,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .background(fieldBackground)
                            .clickable { activeField = TipActiveField.TIP }
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        val displayTip = if (tipMode == TipMode.PERCENTAGE) {
                            "${if (tipInput.isEmpty()) "0" else tipInput}%"
                        } else {
                            if (tipInput.isEmpty()) "- -" else tipInput
                        }
                        Text(
                            text = displayTip,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (tipMode == TipMode.PERCENTAGE) {
                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Tip chips (10%, 15%, 18%, 20%)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("10", "15", "18", "20").forEach { pct ->
                                val isSelected = tipInput == pct
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) accentBlue else fieldBorder,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .background(
                                            if (isSelected) accentBlue.copy(alpha = 0.15f)
                                            else fieldBackground
                                        )
                                        .clickable {
                                            tipInput = pct
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$pct%",
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color = if (isSelected) accentBlue else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tax Percentage
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.tip_calculator_tax_percentage),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                width = if (activeField == TipActiveField.TAX) 2.dp else 1.dp,
                                color = if (activeField == TipActiveField.TAX) accentBlue else fieldBorder,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .background(fieldBackground)
                            .clickable { activeField = TipActiveField.TAX }
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = "${if (taxPercent.isEmpty()) "0" else taxPercent}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Results Card
                Text(
                    text = stringResource(R.string.tip_calculator_results),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, fieldBorder, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = fieldBackground
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        ResultRow(
                            label = stringResource(R.string.tip_calculator_total),
                            value = totalStr,
                            isHighlight = true,
                            accentColor = accentBlue
                        )
                        ResultRow(
                            label = stringResource(R.string.tip_calculator_total_per_person),
                            value = totalPerPersonStr,
                            isHighlight = false,
                            accentColor = accentBlue
                        )
                        ResultRow(
                            label = stringResource(R.string.tip_calculator_tip),
                            value = tipStr,
                            isHighlight = false,
                            accentColor = accentBlue
                        )
                        ResultRow(
                            label = stringResource(R.string.tip_calculator_tip_per_person),
                            value = tipPerPersonStr,
                            isHighlight = false,
                            accentColor = accentBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Keypad at bottom (visible only when activeField != null)
            AnimatedVisibility(
                visible = activeField != null,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                UnitKeypad(
                    onKeyPress = { key ->
                        val currentField = activeField ?: return@UnitKeypad
                        when (key) {
                            "C" -> {
                                when (currentField) {
                                    TipActiveField.BILL -> bill = ""
                                    TipActiveField.PEOPLE -> people = 1
                                    TipActiveField.TIP -> tipInput = "0"
                                    TipActiveField.TAX -> taxPercent = "0"
                                }
                            }
                            "⌫" -> {
                                when (currentField) {
                                    TipActiveField.BILL -> bill = bill.dropLast(1)
                                    TipActiveField.PEOPLE -> {
                                        val s = people.toString().dropLast(1)
                                        people = s.toIntOrNull() ?: 1
                                    }
                                    TipActiveField.TIP -> tipInput = tipInput.dropLast(1).ifEmpty { "0" }
                                    TipActiveField.TAX -> taxPercent = taxPercent.dropLast(1).ifEmpty { "0" }
                                }
                            }
                            "⇅" -> {
                                // Close keypad
                                activeField = null
                            }
                            "+/-" -> {
                                // No negative values
                            }
                            "." -> {
                                when (currentField) {
                                    TipActiveField.BILL -> if (!bill.contains(".")) bill += "."
                                    TipActiveField.TIP -> if (!tipInput.contains(".")) tipInput += "."
                                    TipActiveField.TAX -> if (!taxPercent.contains(".")) taxPercent += "."
                                    TipActiveField.PEOPLE -> {} // Integers only
                                }
                            }
                            else -> {
                                // Digit key (0-9)
                                when (currentField) {
                                    TipActiveField.BILL -> {
                                        if (bill.length < 10) bill += key
                                    }
                                    TipActiveField.PEOPLE -> {
                                        val newPeopleStr = if (people == 1 && key != "0") key else "$people$key"
                                        people = newPeopleStr.toIntOrNull()?.coerceIn(1, 999) ?: 1
                                    }
                                    TipActiveField.TIP -> {
                                        if (tipInput == "0") tipInput = key
                                        else if (tipInput.length < 6) tipInput += key
                                    }
                                    TipActiveField.TAX -> {
                                        if (taxPercent == "0") taxPercent = key
                                        else if (taxPercent.length < 5) taxPercent += key
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ResultRow(
    label: String,
    value: String,
    isHighlight: Boolean,
    accentColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = if (isHighlight) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = accentColor
        )
    }
}
