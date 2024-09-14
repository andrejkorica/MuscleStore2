package hr.unipu.musclestore.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.unipu.musclestore.data.WorkoutPlan

@Composable
fun WorkoutTable(workoutPlan: WorkoutPlan) {
    Column(modifier = Modifier.fillMaxWidth()) {
        workoutPlan.sections.forEach { section ->

            Text(
                text = "Section: ${section.title}",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 24.sp), // Increased font size
                modifier = Modifier
                    .padding(vertical = 4.dp) // Reduced vertical padding
                    .background(Color(0xFFE1E2EC), shape = RoundedCornerShape(4.dp)) // Applied the new color
                    .fillMaxWidth()
                    .padding(8.dp), // Reduced padding inside the header
                color = Color.Black
            )

            // Table header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE1E2EC), shape = RoundedCornerShape(4.dp)) // Applied the new color
                    .padding(vertical = 4.dp), // Reduced vertical padding
                verticalAlignment = Alignment.CenterVertically // Center contents vertically
            ) {
                TableCell(
                    text = "Exercise",
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(2f).background(Color.Transparent),
                    color = Color.Black, // Changed text color for better visibility
                    fontSize = 20.sp // Increased font size
                )
                Spacer(modifier = Modifier.width(4.dp)) // Reduced space between cells
                VerticalDivider()
                Spacer(modifier = Modifier.width(4.dp)) // Reduced space between cells
                TableCell(
                    text = "Reps",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f).background(Color.Transparent),
                    color = Color.Black, // Changed text color for better visibility
                    fontSize = 20.sp // Increased font size
                )
            }

            // Table content
            section.exercises.forEach { exercise ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp) // Increased height of table rows
                        .padding(vertical = 2.dp) // Reduced vertical padding
                        .background(Color(0xFFE1E2EC), shape = RoundedCornerShape(4.dp)), // Applied the new color
                    verticalAlignment = Alignment.CenterVertically // Center contents vertically
                ) {
                    TableCell(
                        text = exercise.title,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(2f),
                        color = Color.Black, // Changed text color for better visibility
                        fontSize = 18.sp // Increased font size
                    )
                    Spacer(modifier = Modifier.width(4.dp)) // Reduced space between cells
                    VerticalDivider()
                    Spacer(modifier = Modifier.width(4.dp)) // Reduced space between cells
                    TableCell(
                        text = exercise.reps,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                        color = Color.Black, // Changed text color for better visibility
                        fontSize = 18.sp // Increased font size
                    )
                }
            }
        }
    }
}
