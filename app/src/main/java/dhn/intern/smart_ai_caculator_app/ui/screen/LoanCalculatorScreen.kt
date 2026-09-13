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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dhn.intern.smart_ai_caculator_app.R
import dhn.intern.smart_ai_caculator_app.ui.components.keypad.UnitKeypad
import java.text.DecimalFormat
import kotlin.math.pow

private val PrimaryBlue = Color(0xFF0B57D0)
private val TextDark = Color(0xFF272727)
private val TextGray = Color(0xFF6B7588)
private val BoxBackground = Color(0xFFF9FAFD)
private val BoxBorder = Color(0xFFE8EFF7)

private enum class LoanActiveField {
    PRINCIPAL,
    INTEREST,
    TERM
}

enum class LoanRepaymentMethod {
    EQUAL_PRINCIPAL,
    EQUAL_PRINCIPAL_AND_INTEREST
}

@Composable
fun LoanCalculatorScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var repaymentMethod by remember { mutableStateOf(LoanRepaymentMethod.EQUAL_PRINCIPAL) }
    var principalInput by remember { mutableStateOf("") }
    var interestInput by remember { mutableStateOf("") }
    var termInput by remember { mutableStateOf("") }

    var activeField by remember { mutableStateOf<LoanActiveField?>(null) }
    var methodDropdownExpanded by remember { mutableStateOf(false) }

    // Calculations
    val principal = principalInput.toDoubleOrNull() ?: 0.0
    val annualRate = interestInput.toDoubleOrNull() ?: 0.0
    val termMonths = termInput.toIntOrNull() ?: 0

    val decimalFormat = remember { DecimalFormat("#,##0.00") }
    val currencyFormat = remember { DecimalFormat("$#,##0.00") }

    val hasValidInput = principal > 0 && termMonths > 0

    // Compute Results
    val monthlyRate = (annualRate / 100.0) / 12.0
    var totalPayment = 0.0
    var totalInterest = 0.0
    var monthlyPaymentStr = "--"

    if (hasValidInput) {
        when (repaymentMethod) {
            LoanRepaymentMethod.EQUAL_PRINCIPAL_AND_INTEREST -> {
                // EMI formula
                val emi = if (monthlyRate > 0) {
                    val powTerm = (1.0 + monthlyRate).pow(termMonths.toDouble())
                    principal * monthlyRate * powTerm / (powTerm - 1.0)
                } else {
                    principal / termMonths
                }
                totalPayment = emi * termMonths
                totalInterest = totalPayment - principal
                monthlyPaymentStr = currencyFormat.format(emi)
            }
            LoanRepaymentMethod.EQUAL_PRINCIPAL -> {
                val principalPerMonth = principal / termMonths
                // Total interest = (termMonths + 1) / 2 * principal * monthlyRate
                totalInterest = if (monthlyRate > 0) {
                    ((termMonths + 1.0) / 2.0) * principal * monthlyRate
                } else {
                    0.0
                }
                totalPayment = principal + totalInterest

                // First month payment: principalPerMonth + principal * monthlyRate
                val firstMonthPayment = principalPerMonth + (principal * monthlyRate)
                val lastMonthPayment = principalPerMonth + (principalPerMonth * monthlyRate)

                monthlyPaymentStr = if (termMonths == 1 || annualRate == 0.0) {
                    currencyFormat.format(firstMonthPayment)
                } else {
                    "${currencyFormat.format(firstMonthPayment)} ~ ${currencyFormat.format(lastMonthPayment)}"
                }
            }
        }
    }

    val totalStr = if (hasValidInput) currencyFormat.format(totalPayment) else "--"
    val totalInterestStr = if (hasValidInput) currencyFormat.format(totalInterest) else "--"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.systemBars,
        containerColor = Color.White,
        topBar = {
            LoanTopBar(
                onBackClick = { navController.popBackStack() },
                onShareClick = {
                    if (hasValidInput) {
                        val shareText = buildString {
                            appendLine("Loan Calculation Summary:")
                            appendLine("• Repayment: ${if (repaymentMethod == LoanRepaymentMethod.EQUAL_PRINCIPAL) "Equal Principal" else "Equal Principal & Interest"}")
                            appendLine("• Principal: ${currencyFormat.format(principal)}")
                            appendLine("• Interest: $annualRate %")
                            appendLine("• Term: $termMonths months")
                            appendLine("-------------------")
                            appendLine("• Total Payment: $totalStr")
                            appendLine("• Monthly Payment: $monthlyPaymentStr")
                            appendLine("• Total Interest: $totalInterestStr")
                        }
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share Loan Calculation"))
                    }
                },
                onResetClick = {
                    principalInput = ""
                    interestInput = ""
                    termInput = ""
                    activeField = null
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    activeField = null
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // 1. Repayment Method
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = stringResource(R.string.loan_calculator_repayment_method),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextDark
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .background(BoxBackground)
                                .border(1.dp, BoxBorder, RoundedCornerShape(15.dp))
                                .clickable {
                                    activeField = null
                                    methodDropdownExpanded = !methodDropdownExpanded
                                }
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = when (repaymentMethod) {
                                    LoanRepaymentMethod.EQUAL_PRINCIPAL -> stringResource(R.string.loan_calculator_equal_principal)
                                    LoanRepaymentMethod.EQUAL_PRINCIPAL_AND_INTEREST -> stringResource(R.string.loan_calculator_equal_principal_interest)
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextDark
                            )
                            Icon(
                                imageVector = if (methodDropdownExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Select Repayment Method",
                                tint = TextDark
                            )
                        }

                        DropdownMenu(
                            expanded = methodDropdownExpanded,
                            onDismissRequest = { methodDropdownExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.loan_calculator_equal_principal)) },
                                onClick = {
                                    repaymentMethod = LoanRepaymentMethod.EQUAL_PRINCIPAL
                                    methodDropdownExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.loan_calculator_equal_principal_interest)) },
                                onClick = {
                                    repaymentMethod = LoanRepaymentMethod.EQUAL_PRINCIPAL_AND_INTEREST
                                    methodDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // 2. Loan Principal
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = stringResource(R.string.loan_calculator_principal),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextDark
                    )
                    InputBox(
                        value = if (principalInput.isEmpty()) "--" else formatDisplayNumber(principalInput),
                        placeholder = "--",
                        isActive = activeField == LoanActiveField.PRINCIPAL,
                        onClick = { activeField = LoanActiveField.PRINCIPAL },
                        alignment = Alignment.CenterEnd
                    )
                }

                // 3. Row: Interest & Term (Months)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Interest
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.loan_calculator_interest),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextDark
                        )
                        InputBox(
                            value = if (interestInput.isEmpty()) "-- %" else "$interestInput %",
                            placeholder = "-- %",
                            isActive = activeField == LoanActiveField.INTEREST,
                            onClick = { activeField = LoanActiveField.INTEREST },
                            alignment = Alignment.CenterEnd
                        )
                    }

                    // Term (Months)
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.loan_calculator_term),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextDark
                        )
                        InputBox(
                            value = if (termInput.isEmpty()) "--" else termInput,
                            placeholder = "--",
                            isActive = activeField == LoanActiveField.TERM,
                            onClick = { activeField = LoanActiveField.TERM },
                            alignment = Alignment.CenterEnd
                        )
                    }
                }

                // 4. Results Section
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.loan_calculator_results),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextDark
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(15.dp),
                        colors = CardDefaults.cardColors(containerColor = BoxBackground),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BoxBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            LoanResultRow(
                                label = stringResource(R.string.loan_calculator_total),
                                value = totalStr,
                                isPrimary = true
                            )
                            LoanResultRow(
                                label = stringResource(R.string.loan_calculator_monthly_payment),
                                value = monthlyPaymentStr,
                                isPrimary = true
                            )
                            if (hasValidInput) {
                                LoanResultRow(
                                    label = stringResource(R.string.loan_calculator_total_interest),
                                    value = totalInterestStr,
                                    isPrimary = false
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(380.dp))
            }

            // Bottom Keypad
            AnimatedVisibility(
                visible = activeField != null,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                UnitKeypad(
                    onKeyPress = { key ->
                        val field = activeField ?: return@UnitKeypad
                        when (key) {
                            "C" -> {
                                when (field) {
                                    LoanActiveField.PRINCIPAL -> principalInput = ""
                                    LoanActiveField.INTEREST -> interestInput = ""
                                    LoanActiveField.TERM -> termInput = ""
                                }
                            }
                            "⌫" -> {
                                when (field) {
                                    LoanActiveField.PRINCIPAL -> principalInput = principalInput.dropLast(1)
                                    LoanActiveField.INTEREST -> interestInput = interestInput.dropLast(1)
                                    LoanActiveField.TERM -> termInput = termInput.dropLast(1)
                                }
                            }
                            "⇅" -> {
                                activeField = null
                            }
                            "+/-" -> {
                                // No negative numbers in loans
                            }
                            "." -> {
                                when (field) {
                                    LoanActiveField.PRINCIPAL -> if (!principalInput.contains(".")) principalInput += "."
                                    LoanActiveField.INTEREST -> if (!interestInput.contains(".")) interestInput += "."
                                    LoanActiveField.TERM -> {} // Term is integer only
                                }
                            }
                            else -> {
                                // Digits 0-9
                                when (field) {
                                    LoanActiveField.PRINCIPAL -> {
                                        if (principalInput.length < 12) principalInput += key
                                    }
                                    LoanActiveField.INTEREST -> {
                                        if (interestInput.length < 5) interestInput += key
                                    }
                                    LoanActiveField.TERM -> {
                                        if (termInput.length < 4) termInput += key
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
private fun LoanTopBar(
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onResetClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF1D1C1C)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.loan_calculator_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1D1C1C)
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onShareClick) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = Color(0xFF1D1C1C)
                )
            }
            IconButton(onClick = onResetClick) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    tint = Color(0xFF1D1C1C)
                )
            }
        }
    }
}

@Composable
private fun InputBox(
    value: String,
    placeholder: String,
    isActive: Boolean,
    onClick: () -> Unit,
    alignment: Alignment = Alignment.CenterStart
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(BoxBackground)
            .border(
                width = if (isActive) 2.dp else 1.dp,
                color = if (isActive) PrimaryBlue else BoxBorder,
                shape = RoundedCornerShape(15.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = alignment
    ) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (value == placeholder) TextGray else TextDark
        )
    }
}

@Composable
private fun LoanResultRow(
    label: String,
    value: String,
    isPrimary: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = TextDark
        )
        Text(
            text = value,
            fontSize = if (isPrimary && value != "--") 18.sp else 16.sp,
            fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.Medium,
            color = if (value == "--") TextGray else PrimaryBlue
        )
    }
}

private fun formatDisplayNumber(raw: String): String {
    if (raw.isEmpty()) return ""
    return try {
        if (raw.contains(".")) {
            val parts = raw.split(".")
            val intPart = parts[0].toLongOrNull() ?: 0L
            val formattedInt = DecimalFormat("#,###").format(intPart)
            "$formattedInt.${parts[1]}"
        } else {
            val num = raw.toLongOrNull() ?: 0L
            DecimalFormat("#,###").format(num)
        }
    } catch (_: Exception) {
        raw
    }
}
