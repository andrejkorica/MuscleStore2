package hr.unipu.musclestore.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun TableCell(text: String, textAlign: TextAlign, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = textAlign,
        modifier = modifier
            .padding(horizontal = 8.dp)
            .fillMaxHeight()
    )
}