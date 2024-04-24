package hr.unipu.musclestore.Views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.unipu.musclestore.R

@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val painter: Painter = painterResource(id = R.drawable.lifter)
            Image(
                painter = painter,
                contentDescription = "Lifter Image",
                modifier = Modifier.size(350.dp)
            )
            Text(
                text = "Create a workout plan or download a plan from the store",
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 36.dp, start = 26.dp, end = 26.dp)
            )
        }
    }
}