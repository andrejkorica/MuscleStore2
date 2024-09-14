package hr.unipu.musclestore.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CalendarCell(
    day: Int,
    isSelected: Boolean,
    hasWorkout: Boolean, // Add this parameter
    onClick: () -> Unit
) {
    // Define the background color based on whether there is a workout
    val backgroundColor = if (hasWorkout) Color.Magenta else Color(0xFFDDE2F9) // Use hex color #DDE2F9

    Box(
        modifier = Modifier
            .size(48.dp) // Increase the size for easier clicking
            .background(backgroundColor, shape = RoundedCornerShape(12.dp)) // Increase corner radius
            .clickable(onClick = onClick)
            .padding(8.dp), // Add padding inside the cell
        contentAlignment = Alignment.Center
    ) {
        if (day != 0) { // Ensure day is not zero
            Text(
                text = day.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp, // Increase text size for better visibility
                color = Color.Black // Set text color for better contrast
            )
        }
    }
}
