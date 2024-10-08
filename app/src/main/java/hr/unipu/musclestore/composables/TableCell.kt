package hr.unipu.musclestore.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TableCell(
    text: String,
    textAlign: TextAlign,
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
    fontSize: TextUnit = 16.sp
) {
    Box(
        modifier = modifier
            .padding(horizontal = 4.dp) // Adjust padding as needed
            .background(Color.Transparent),
        contentAlignment = Alignment.Center // Vertically center text
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = fontSize),
            color = color,
            textAlign = textAlign
        )
    }
}
