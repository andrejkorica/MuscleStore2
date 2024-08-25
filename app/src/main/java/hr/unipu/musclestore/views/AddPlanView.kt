package hr.unipu.musclestore.views

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import hr.unipu.musclestore.composables.Table
import hr.unipu.musclestore.viewmodel.Exercise
import hr.unipu.musclestore.viewmodel.Section
import hr.unipu.musclestore.viewmodel.WorkoutPlanViewModel

@Composable
fun AddPlanScreen(navController: NavController) {
    val workoutPlanViewModel: WorkoutPlanViewModel = viewModel()
    var planName by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var tables by remember { mutableStateOf(listOf(listOf("", ""))) }
    var headers by remember { mutableStateOf(listOf("")) }

    fun sendData() {
        val sections = headers.mapIndexed { index, header ->
            Section(
                title = header,
                exercises = tables[index].chunked(2).map { pair ->
                    Exercise(
                        title = pair.getOrNull(0) ?: "",
                        reps = pair.getOrNull(1) ?: "" // Store reps as a string
                    )
                }

            )
        }

        workoutPlanViewModel.sendWorkoutPlan(context, planName, sections) { success, message ->
            if (success) {
                // Show success message
                Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show()
                // Navigate to the plans screen
                navController.navigate("PlansView") // Replace with your actual route
            } else {
                // Show error message
                message?.let { Toast.makeText(context, "Error: $it", Toast.LENGTH_LONG).show() }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp) // Adjust padding for FAB
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Card containing the TextField for entering the plan name
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                TextField(
                    value = planName,
                    onValueChange = { planName = it },
                    label = { Text("Name of the plan...") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done // Set the IME action to Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus() // Clear focus to dismiss the keyboard
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 12.dp)
                )
            }

            // Iterate through the list of tables
            tables.forEachIndexed { index, tableRows ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Table(
                            header = headers[index],
                            rows = tableRows,
                            onHeaderChange = { newHeader ->
                                val updatedHeaders = headers.toMutableList()
                                updatedHeaders[index] = newHeader
                                headers = updatedHeaders
                            },
                            onRowChange = { rowIndex, value ->
                                val updatedTableRows = tables.toMutableList()
                                val updatedRows = updatedTableRows[index].toMutableList()
                                updatedRows[rowIndex] = value
                                updatedTableRows[index] = updatedRows
                                tables = updatedTableRows
                            },
                            onAddRow = {
                                val updatedTableRows = tables.toMutableList()
                                updatedTableRows[index] = updatedTableRows[index] + listOf("", "")
                                tables = updatedTableRows
                            },
                            onDeleteRow = { rowIndex ->
                                if (tables[index].size > 2) {
                                    val updatedTableRows = tables.toMutableList()
                                    val updatedRows = updatedTableRows[index].toMutableList()
                                    updatedRows.removeAt(rowIndex * 2)
                                    updatedRows.removeAt(rowIndex * 2) // Remove both cells of the row
                                    updatedTableRows[index] = updatedRows
                                    tables = updatedTableRows
                                }
                            }
                        )
                    }
                }
            }

            // Container for the add and delete table buttons
            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Add Table Button
                Button(
                    onClick = {
                        tables = tables + listOf(listOf("", ""))
                        headers = headers + ""
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Add Table")
                }

                // Delete Table Button
                Button(
                    onClick = {
                        if (tables.size > 1) {
                            val updatedTables = tables.toMutableList()
                            val updatedHeaders = headers.toMutableList()
                            updatedTables.removeAt(tables.size - 1)
                            updatedHeaders.removeAt(headers.size - 1)
                            tables = updatedTables
                            headers = updatedHeaders
                        }
                    },
                    enabled = tables.size > 1,
                    colors = ButtonDefaults.buttonColors(
                        if (tables.size > 1) MaterialTheme.colorScheme.primary else Color.Red
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Delete Table")
                }
            }
        }

        // Checkmark Button
        FloatingActionButton(
            onClick = { sendData() },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Filled.Check, contentDescription = "Save")
        }
    }
}
