package dhn.intern.smart_ai_caculator_app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dhn.intern.smart_ai_caculator_app.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.math.abs

private val PrimaryBlue = Color(0xFF0B57D0)
private val TextDark = Color(0xFF272727)
private val TextGray = Color(0xFF6B7588)
private val BoxBackground = Color(0xFFF9FAFD)
private val BoxBorder = Color(0xFFE8EFF7)
private val DividerColor = Color(0xFFE7E7E7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateCalculatorScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.systemBars,
        containerColor = Color.White,
        topBar = {
            DateTopBar(onBackClick = { navController.popBackStack() })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = PrimaryBlue,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        height = 2.dp,
                        color = PrimaryBlue
                    )
                },
                divider = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(DividerColor)
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = stringResource(R.string.date_calculator_tab_start_date),
                            fontSize = 14.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (selectedTab == 0) PrimaryBlue else TextGray
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = stringResource(R.string.date_calculator_tab_from_to),
                            fontSize = 14.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (selectedTab == 1) PrimaryBlue else TextGray
                        )
                    }
                )
            }

            // Tab Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                if (selectedTab == 0) {
                    StartDateDurationTab()
                } else {
                    FromToTab()
                }
            }
        }
    }
}

@Composable
private fun DateTopBar(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF1D1C1C)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = stringResource(R.string.date_calculator_title),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1D1C1C)
        )
    }
}

// -----------------------------------------------------------------------------
// TAB 1: Start Date (Calculate Duration between two dates)
// -----------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StartDateDurationTab() {
    val today = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
    val defaultEnd = remember {
        Calendar.getInstance().apply {
            timeInMillis = today
            add(Calendar.DAY_OF_YEAR, 30)
        }.timeInMillis
    }

    var startDateMillis by remember { mutableLongStateOf(today) }
    var endDateMillis by remember { mutableLongStateOf(defaultEnd) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    var hasCalculated by remember { mutableStateOf(true) }

    if (showStartPicker) {
        DateModalPicker(
            initialMillis = startDateMillis,
            onDismiss = { showStartPicker = false },
            onDateSelected = { selected ->
                startDateMillis = selected
                showStartPicker = false
            }
        )
    }

    if (showEndPicker) {
        DateModalPicker(
            initialMillis = endDateMillis,
            onDismiss = { showEndPicker = false },
            onDateSelected = { selected ->
                endDateMillis = selected
                showEndPicker = false
            }
        )
    }

    // Calculations
    val earlierMillis = minOf(startDateMillis, endDateMillis)
    val laterMillis = maxOf(startDateMillis, endDateMillis)
    val totalDays = TimeUnit.MILLISECONDS.toDays(laterMillis - earlierMillis)

    val weeks = totalDays / 7
    val remDaysWeeks = totalDays % 7

    val cStart = Calendar.getInstance().apply { timeInMillis = earlierMillis }
    val cEnd = Calendar.getInstance().apply { timeInMillis = laterMillis }
    var years = cEnd.get(Calendar.YEAR) - cStart.get(Calendar.YEAR)
    val tempCal = (cStart.clone() as Calendar).apply { add(Calendar.YEAR, years) }
    if (tempCal.after(cEnd)) {
        years--
        tempCal.timeInMillis = cStart.timeInMillis
        tempCal.add(Calendar.YEAR, years)
    }
    val remDaysYears = TimeUnit.MILLISECONDS.toDays(cEnd.timeInMillis - tempCal.timeInMillis)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Start Date Input
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = stringResource(R.string.date_calculator_start_date),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark
            )
            DateBox(
                displayDate = formatDateDisplay(startDateMillis),
                onClick = { showStartPicker = true }
            )
        }

        // End Date Input
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = stringResource(R.string.date_calculator_end_date),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark
            )
            DateBox(
                displayDate = formatDateDisplay(endDateMillis),
                onClick = { showEndPicker = true }
            )
        }

        // Calculate Button
        Button(
            onClick = { hasCalculated = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlue,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(15.dp)
        ) {
            Text(
                text = stringResource(R.string.date_calculator_calculate_duration),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .height(1.dp)
                .background(DividerColor)
        )

        if (hasCalculated) {
            // Card 1: Days
            ResultCard(
                title = stringResource(R.string.date_calculator_results_days),
                primaryText = "$totalDays",
                unitText = " ${stringResource(R.string.date_calculator_days)}"
            )

            // Card 2: Weeks
            ResultCardCompound(
                title = stringResource(R.string.date_calculator_results_weeks),
                mainPart = "$weeks ${stringResource(R.string.date_calculator_weeks)},",
                subPart = " $remDaysWeeks ${stringResource(R.string.date_calculator_days)}"
            )

            // Card 3: Years
            ResultCardCompound(
                title = stringResource(R.string.date_calculator_results_years),
                mainPart = "$years ${stringResource(R.string.date_calculator_years)},",
                subPart = " $remDaysYears ${stringResource(R.string.date_calculator_days)}"
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// -----------------------------------------------------------------------------
// TAB 2: From/To (Add / Subtract Time from Base Date)
// -----------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FromToTab() {
    val today = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    var baseDateMillis by remember { mutableLongStateOf(today) }
    var showPicker by remember { mutableStateOf(false) }
    var isAdd by remember { mutableStateOf(true) }

    var years by remember { mutableIntStateOf(0) }
    var months by remember { mutableIntStateOf(1) }
    var weeks by remember { mutableIntStateOf(0) }
    var days by remember { mutableIntStateOf(0) }

    if (showPicker) {
        DateModalPicker(
            initialMillis = baseDateMillis,
            onDismiss = { showPicker = false },
            onDateSelected = { selected ->
                baseDateMillis = selected
                showPicker = false
            }
        )
    }

    // Calculate Target Date
    val targetCal = Calendar.getInstance().apply {
        timeInMillis = baseDateMillis
        val sign = if (isAdd) 1 else -1
        if (years != 0) add(Calendar.YEAR, sign * years)
        if (months != 0) add(Calendar.MONTH, sign * months)
        val totalExtraDays = weeks * 7 + days
        if (totalExtraDays != 0) add(Calendar.DAY_OF_MONTH, sign * totalExtraDays)
    }
    val targetDateDisplay = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.ENGLISH).format(targetCal.time)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Base Date
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = stringResource(R.string.date_calculator_start_date),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark
            )
            DateBox(
                displayDate = formatDateDisplay(baseDateMillis),
                onClick = { showPicker = true }
            )
        }

        // Operation Selector: Add / Subtract
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OperationPill(
                title = stringResource(R.string.date_calculator_add),
                isSelected = isAdd,
                onClick = { isAdd = true },
                modifier = Modifier.weight(1f)
            )
            OperationPill(
                title = stringResource(R.string.date_calculator_subtract),
                isSelected = !isAdd,
                onClick = { isAdd = false },
                modifier = Modifier.weight(1f)
            )
        }

        // Inputs: Years, Months, Weeks, Days
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StepperUnitField(
                label = stringResource(R.string.date_calculator_years),
                value = years,
                onValueChange = { years = it.coerceAtLeast(0) },
                modifier = Modifier.weight(1f)
            )
            StepperUnitField(
                label = stringResource(R.string.date_calculator_months),
                value = months,
                onValueChange = { months = it.coerceAtLeast(0) },
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StepperUnitField(
                label = stringResource(R.string.date_calculator_weeks),
                value = weeks,
                onValueChange = { weeks = it.coerceAtLeast(0) },
                modifier = Modifier.weight(1f)
            )
            StepperUnitField(
                label = stringResource(R.string.date_calculator_days),
                value = days,
                onValueChange = { days = it.coerceAtLeast(0) },
                modifier = Modifier.weight(1f)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .height(1.dp)
                .background(DividerColor)
        )

        // Target Date Result Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(15.dp),
            colors = CardDefaults.cardColors(containerColor = BoxBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, BoxBorder)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.date_calculator_target_date),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDark
                )
                Text(
                    text = targetDateDisplay,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// -----------------------------------------------------------------------------
// UI COMPONENTS
// -----------------------------------------------------------------------------

@Composable
private fun DateBox(
    displayDate: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(BoxBackground)
            .border(1.dp, BoxBorder, RoundedCornerShape(15.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = displayDate,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = TextDark
        )
        Icon(
            painter = painterResource(id = R.drawable.date_caculator),
            contentDescription = "Select Date",
            modifier = Modifier.size(22.dp),
            tint = PrimaryBlue
        )
    }
}

@Composable
private fun ResultCard(
    title: String,
    primaryText: String,
    unitText: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = BoxBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, BoxBorder)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDark
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = PrimaryBlue, fontSize = 26.sp, fontWeight = FontWeight.SemiBold)) {
                        append(primaryText)
                    }
                    withStyle(SpanStyle(color = PrimaryBlue, fontSize = 26.sp, fontWeight = FontWeight.SemiBold)) {
                        append(unitText)
                    }
                }
            )
        }
    }
}

@Composable
private fun ResultCardCompound(
    title: String,
    mainPart: String,
    subPart: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = BoxBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, BoxBorder)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDark
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = PrimaryBlue, fontSize = 26.sp, fontWeight = FontWeight.SemiBold)) {
                        append(mainPart)
                    }
                    withStyle(SpanStyle(color = TextGray, fontSize = 16.sp, fontWeight = FontWeight.Normal)) {
                        append(subPart)
                    }
                }
            )
        }
    }
}

@Composable
private fun OperationPill(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) PrimaryBlue else BoxBackground)
            .border(1.dp, if (isSelected) PrimaryBlue else BoxBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color.White else TextDark
        )
    }
}

@Composable
private fun StepperUnitField(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BoxBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, BoxBorder)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Minus
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, BoxBorder, CircleShape)
                        .clickable { onValueChange(value - 1) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "−",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                }

                Text(
                    text = "$value",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBlue
                )

                // Plus
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, BoxBorder, CircleShape)
                        .clickable { onValueChange(value + 1) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase",
                        modifier = Modifier.size(16.dp),
                        tint = PrimaryBlue
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateModalPicker(
    initialMillis: Long,
    onDismiss: () -> Unit,
    onDateSelected: (Long) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { utcMillis ->
                        val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                            timeInMillis = utcMillis
                        }
                        val localCal = Calendar.getInstance().apply {
                            set(Calendar.YEAR, utcCal.get(Calendar.YEAR))
                            set(Calendar.MONTH, utcCal.get(Calendar.MONTH))
                            set(Calendar.DAY_OF_MONTH, utcCal.get(Calendar.DAY_OF_MONTH))
                            set(Calendar.HOUR_OF_DAY, 12)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        onDateSelected(localCal.timeInMillis)
                    }
                }
            ) {
                Text("OK", color = PrimaryBlue, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextGray)
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

private fun formatDateDisplay(millis: Long): String {
    val cal = Calendar.getInstance().apply { timeInMillis = millis }
    return SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).format(cal.time)
}
