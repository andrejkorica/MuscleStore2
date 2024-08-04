package hr.unipu.musclestore.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun Table(header: String, rows: List<String>, onHeaderChange: (String) -> Unit, onAddRow: () -> Unit, onDeleteRow: (Int) -> Unit) {
    val cellValues = remember { mutableStateListOf(*rows.toTypedArray()) }
    var rowCount by remember { mutableIntStateOf(rows.size / 2) }
    val focusManager = LocalFocusManager.current

    Column {
        // Header
        TextField(
            value = header,
            onValueChange = onHeaderChange,
            label = { Text("Header") },
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
                .padding(8.dp),
            textStyle = MaterialTheme.typography.headlineSmall
        )

        // Rows
        for (i in 0 until rowCount) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                val exerciseIndex = i * 2
                val setsRepsIndex = exerciseIndex + 1

                // Left TextField (Exercise Name)
                TextField(
                    value = cellValues[exerciseIndex],
                    onValueChange = { cellValues[exerciseIndex] = it },
                    label = { Text("Exercise") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done // Set the IME action to Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus() // Clear focus to dismiss the keyboard
                        }
                    ),
                    modifier = Modifier
                        .weight(2f) // Exercise column takes twice as much space as Sets/Reps
                        .padding(8.dp)
                )

                // Right TextField (Sets/Reps)
                TextField(
                    value = cellValues[setsRepsIndex],
                    onValueChange = { cellValues[setsRepsIndex] = it },
                    label = { Text("Sets/Reps") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done // Set the IME action to Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus() // Clear focus to dismiss the keyboard
                        }
                    ),
                    modifier = Modifier
                        .weight(1.3f) // Sets/Reps column takes half the space of Exercise
                        .padding(8.dp)
                )
            }
        }

        // Row for Add and Delete Row Buttons
        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Add Row Button
            Button(
                onClick = {
                    onAddRow()
                    rowCount++
                    cellValues.addAll(listOf("", "")) // Add empty values for the new row cells
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Add Row")
            }

            // Delete Row Button
            Button(
                onClick = {
                    if (rowCount > 1) {
                        onDeleteRow(rowCount - 1)
                        rowCount--
                    }
                },
                enabled = rowCount > 1,
                colors = ButtonDefaults.buttonColors(
                    if (rowCount > 1) MaterialTheme.colorScheme.primary else Color.Red
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Delete Row")
            }
        }
    }
}
