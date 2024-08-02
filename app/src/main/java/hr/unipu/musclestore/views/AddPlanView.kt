package hr.unipu.musclestore.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.text.KeyboardActions

@Composable
fun AddPlanScreen() {
    var planName by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
            )
        }

        // Card for the first table
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Table(
                    header = "Header 1",
                    rows = listOf(
                        listOf("Row 1, Col 1", "Row 1, Col 2"),
                        listOf("Row 2, Col 1", "Row 2, Col 2")
                    )
                )
            }
        }

        // Card for the second table
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Table(
                    header = "Header A",
                    rows = listOf(
                        listOf("Row A1, Col 1", "Row A1, Col 2"),
                        listOf("Row A2, Col 1", "Row A2, Col 2")
                    )
                )
            }
        }
    }
}

@Composable
fun Table(header: String, rows: List<List<String>>) {
    Column {
        // Header
        Text(
            text = header,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall
        )

        // Ensure exactly two rows
        require(rows.size == 2) { "Table must have exactly two rows" }

        // Rows
        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                row.forEach { cell ->
                    Text(
                        text = cell,
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
