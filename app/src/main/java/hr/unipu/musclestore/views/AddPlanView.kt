package hr.unipu.musclestore.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.navigation.NavController
import hr.unipu.musclestore.composables.Table

@Composable
fun AddPlanScreen(navController: NavController) {
    var planName by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val scrollState = rememberScrollState()

    // State to manage the list of tables
    var tables by remember { mutableStateOf(listOf(listOf("", ""))) }
    var headers by remember { mutableStateOf(listOf("")) }

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
                        .focusRequester(focusRequester)
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
            onClick = { /* Handle save action */ },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Filled.Check, contentDescription = "Save")
        }
    }
}
