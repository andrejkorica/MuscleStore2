package hr.unipu.musclestore.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomCard(
    imageBitmap: ImageBitmap, // Explicitly set type to ImageBitmap
    headerText: String,
    createdAt: String,
    postedBy: String,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),

        ) {
            // Image and Header in a Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = null,

                    modifier = Modifier
                        .size(64.dp) // Set a small size for the image
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = headerText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Created at and Posted by
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Created at:", fontWeight = FontWeight.Bold)
                    Text(createdAt)
                }
                Spacer(modifier = Modifier.width(16.dp)) // Adjusted spacing here
                Column(modifier = Modifier.weight(1f)) {
                    Text("Posted by:", fontWeight = FontWeight.Bold)
                    Text(postedBy)
                }
            }
        }
    }
}
