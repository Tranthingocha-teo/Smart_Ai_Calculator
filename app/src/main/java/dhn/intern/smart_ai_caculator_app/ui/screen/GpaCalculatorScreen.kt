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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dhn.intern.smart_ai_caculator_app.R
import java.text.DecimalFormat

private val PrimaryBlue = Color(0xFF0B57D0)
private val TextDark = Color(0xFF272727)
private val TextGray = Color(0xFF6B7588)
private val TableBorder = Color(0xFFE8EFF7)
private val HeaderBg = Color(0xFFF9FAFD)
private val RowHover = Color(0xFFF6F9FE)

data class SubjectItem(
    val id: Long = System.currentTimeMillis() + (0..9999).random(),
    val name: String,
    val credits: Double,
    val grade: Double
)

@Composable
fun GpaCalculatorScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val subjects = remember {
        mutableStateListOf(
            SubjectItem(1, "Math", 100.0, 85.0),
            SubjectItem(2, "Science", 90.0, 78.0),
            SubjectItem(3, "English", 80.0, 88.0),
            SubjectItem(4, "History", 70.0, 74.0),
            SubjectItem(5, "Geography", 65.0, 82.0),
            SubjectItem(6, "Computer", 95.0, 91.0),
            SubjectItem(7, "Physics", 85.0, 76.0),
            SubjectItem(8, "Chemistry", 88.0, 84.0),
            SubjectItem(9, "Economics", 72.0, 80.0),
            SubjectItem(10, "Biology", 78.0, 89.0)
        )
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var editingSubject by remember { mutableStateOf<SubjectItem?>(null) }
    var showClearDialog by remember { mutableStateOf(false) }
    var sortOrder by remember { mutableStateOf(0) } // 0: default, 1: grade desc, 2: name asc

    // Weighted GPA calculation
    val totalCredits = subjects.sumOf { it.credits }
    val weightedSum = subjects.sumOf { it.credits * it.grade }
    val gpa = if (totalCredits > 0) weightedSum / totalCredits else 0.0

    val decimalFormat = remember { DecimalFormat("#,##0.0") }
    val gpaFormat = remember { DecimalFormat("#,##0.0") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.systemBars,
        containerColor = Color.White,
        topBar = {
            GpaTopBar(
                onBackClick = { navController.popBackStack() },
                onSortClick = {
                    sortOrder = (sortOrder + 1) % 3
                    val sorted = when (sortOrder) {
                        1 -> subjects.sortedByDescending { it.grade }
                        2 -> subjects.sortedBy { it.name }
                        else -> subjects.sortedBy { it.id }
                    }
                    subjects.clear()
                    subjects.addAll(sorted)
                },
                onClearClick = { showClearDialog = true }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 1. Subjects Table
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, TableBorder)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(HeaderBg)
                            .padding(vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.gpa_calculator_subject_name),
                            modifier = Modifier.weight(1.3f),
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(20.dp)
                                .background(TableBorder)
                        )
                        Text(
                            text = stringResource(R.string.gpa_calculator_credits),
                            modifier = Modifier.weight(0.85f),
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(20.dp)
                                .background(TableBorder)
                        )
                        Text(
                            text = stringResource(R.string.gpa_calculator_grades),
                            modifier = Modifier.weight(0.85f),
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )
                    }

                    HorizontalDivider(color = TableBorder, thickness = 1.dp)

                    // Data Rows
                    if (subjects.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No subjects added yet",
                                fontSize = 14.sp,
                                color = TextGray
                            )
                        }
                    } else {
                        subjects.forEachIndexed { index, item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { editingSubject = item }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.name,
                                    modifier = Modifier.weight(1.3f),
                                    textAlign = TextAlign.Center,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = TextDark
                                )
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(18.dp)
                                        .background(TableBorder)
                                )
                                Text(
                                    text = decimalFormat.format(item.credits),
                                    modifier = Modifier.weight(0.85f),
                                    textAlign = TextAlign.Center,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = TextDark
                                )
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(18.dp)
                                        .background(TableBorder)
                                )
                                Text(
                                    text = decimalFormat.format(item.grade),
                                    modifier = Modifier.weight(0.85f),
                                    textAlign = TextAlign.Center,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = TextDark
                                )
                            }
                            if (index < subjects.lastIndex) {
                                HorizontalDivider(color = TableBorder, thickness = 1.dp)
                            }
                        }
                    }
                }
            }

            // 2. Add Subject Button
            Button(
                onClick = { showAddDialog = true },
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
                    text = stringResource(R.string.gpa_calculator_add_subject),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // 3. Results Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.gpa_calculator_results),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextDark
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(15.dp),
                    colors = CardDefaults.cardColors(containerColor = HeaderBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TableBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.gpa_calculator_gpa),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )
                        Text(
                            text = decimalFormat.format(totalCredits),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextDark
                        )
                        Text(
                            text = gpaFormat.format(gpa),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Add Subject Dialog
    if (showAddDialog) {
        SubjectDialog(
            title = stringResource(R.string.gpa_calculator_add_subject),
            initialName = "",
            initialCredits = "",
            initialGrade = "",
            onDismiss = { showAddDialog = false },
            onConfirm = { name, credits, grade ->
                subjects.add(SubjectItem(name = name, credits = credits, grade = grade))
                showAddDialog = false
            }
        )
    }

    // Edit Subject Dialog
    editingSubject?.let { item ->
        SubjectDialog(
            title = stringResource(R.string.gpa_calculator_edit_subject),
            initialName = item.name,
            initialCredits = item.credits.toString(),
            initialGrade = item.grade.toString(),
            showDelete = true,
            onDismiss = { editingSubject = null },
            onDelete = {
                subjects.removeAll { it.id == item.id }
                editingSubject = null
            },
            onConfirm = { name, credits, grade ->
                val index = subjects.indexOfFirst { it.id == item.id }
                if (index != -1) {
                    subjects[index] = item.copy(name = name, credits = credits, grade = grade)
                }
                editingSubject = null
            }
        )
    }

    // Clear All Confirmation Dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(stringResource(R.string.gpa_calculator_clear_all)) },
            text = { Text(stringResource(R.string.gpa_calculator_clear_all_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        subjects.clear()
                        showClearDialog = false
                    }
                ) {
                    Text(stringResource(R.string.gpa_calculator_delete), color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(stringResource(R.string.gpa_calculator_cancel), color = TextGray)
                }
            }
        )
    }
}

@Composable
private fun GpaTopBar(
    onBackClick: () -> Unit,
    onSortClick: () -> Unit,
    onClearClick: () -> Unit
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
                text = stringResource(R.string.gpa_calculator_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1D1C1C)
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onSortClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_sort_gpa),
                    contentDescription = "Sort",
                    tint = Color(0xFF1D1C1C)
                )
            }
            IconButton(onClick = onClearClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Clear All",
                    tint = Color(0xFF1D1C1C)
                )
            }
        }
    }
}

@Composable
private fun SubjectDialog(
    title: String,
    initialName: String,
    initialCredits: String,
    initialGrade: String,
    showDelete: Boolean = false,
    onDismiss: () -> Unit,
    onDelete: (() -> Unit)? = null,
    onConfirm: (name: String, credits: Double, grade: Double) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var credits by remember { mutableStateOf(initialCredits) }
    var grade by remember { mutableStateOf(initialGrade) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.gpa_calculator_subject_name)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = credits,
                    onValueChange = { credits = it },
                    label = { Text(stringResource(R.string.gpa_calculator_credits)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = grade,
                    onValueChange = { grade = it },
                    label = { Text(stringResource(R.string.gpa_calculator_grades)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                error?.let {
                    Text(text = it, color = Color.Red, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cr = credits.toDoubleOrNull()
                    val gr = grade.toDoubleOrNull()
                    if (name.isBlank()) {
                        error = "Please enter subject name"
                    } else if (cr == null || cr <= 0) {
                        error = "Credits must be a positive number"
                    } else if (gr == null || gr < 0) {
                        error = "Grade must be a valid number"
                    } else {
                        onConfirm(name.trim(), cr, gr)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(stringResource(R.string.gpa_calculator_save))
            }
        },
        dismissButton = {
            Row {
                if (showDelete && onDelete != null) {
                    TextButton(onClick = onDelete) {
                        Text(stringResource(R.string.gpa_calculator_delete), color = Color.Red)
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.gpa_calculator_cancel), color = TextGray)
                }
            }
        }
    )
}
