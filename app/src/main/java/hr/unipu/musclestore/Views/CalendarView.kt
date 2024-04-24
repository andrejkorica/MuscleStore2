package hr.unipu.musclestore.Views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.unipu.musclestore.data.CalendarInput
import androidx.compose.ui.unit.dp


private const val CALENDAR_ROWS = 5
private const val CALENDAR_COLUMNS = 7
class CalendarView {


    fun createCalendarList(): List<CalendarInput> {
        val calendarInputs = mutableListOf<CalendarInput>()
        for (i in 1..31) {
            calendarInputs.add(
                CalendarInput(
                    i,
                    notes = listOf(
                        "gass",
                        "hoe",
                        "aaaaaa"
                    )
                )
            )
        }
        return calendarInputs
    }


    @Composable
    fun CalendarScreen(
        modifier: Modifier = Modifier,
        calendarInput: List<CalendarInput>,
        onDayClick: (Int) -> Unit,
        strokeWidth: Float = 5f,
        month: String
    ) {

        Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = month,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                fontSize = 40.sp
            )
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasHeight = size.height
                val canvasWidth = size.width
                val ySteps = canvasHeight / CALENDAR_ROWS
                val xSteps = canvasWidth / CALENDAR_COLUMNS

                drawRoundRect(
                    color = Color.Gray,
                    cornerRadius = CornerRadius(15f, 15f),
                    style = Stroke(
                        width = strokeWidth
                    )
                )

                for (i in 1 until CALENDAR_ROWS) {
                    drawLine(
                        color = Color.Gray,
                        start = Offset(0f, ySteps * i),
                        end = Offset(canvasWidth, ySteps * i),
                        strokeWidth = strokeWidth
                    )
                }

                for (i in 1 until CALENDAR_COLUMNS) {
                    drawLine(
                        color = Color.Gray,
                        start = Offset(xSteps * i, 0f),
                        end = Offset(xSteps * i, canvasHeight),
                        strokeWidth = strokeWidth
                    )
                }
                val textHeight =  17.dp.toPx()
                for (i in calendarInput.indices) {
                    val textPositionx = xSteps * (i% CALENDAR_COLUMNS) + strokeWidth
                    val textPositionY = (i / CALENDAR_COLUMNS) * ySteps + textHeight + strokeWidth/2
                }

            }
        }
    }
}