package hr.unipu.musclestore.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import hr.unipu.musclestore.data.WorkoutPlan

@Composable
fun WorkoutTable(workoutPlan: WorkoutPlan) {
    Column(modifier = Modifier.fillMaxWidth()) {
        workoutPlan.sections.forEach { section ->
            Text(
                text = "Section: ${section.title}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .background(Color.LightGray)
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            // Table header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Gray)
                    .padding(vertical = 4.dp)
            ) {
                TableCell(
                    text = "Exercise",
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(2f)
                )
                Spacer(modifier = Modifier.width(8.dp)) // Space between cells
                VerticalDivider()
                Spacer(modifier = Modifier.width(8.dp)) // Space between cells
                TableCell(
                    text = "Reps",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }

            // Table content
            section.exercises.forEach { exercise ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    TableCell(
                        text = exercise.title,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(2f)
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // Space between cells
                    VerticalDivider()
                    Spacer(modifier = Modifier.width(8.dp)) // Space between cells
                    TableCell(
                        text = exercise.reps,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}