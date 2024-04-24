package hr.unipu.musclestore.Views

import android.graphics.Paint
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.unipu.musclestore.data.CalendarInput
import kotlinx.coroutines.launch
import java.time.YearMonth


private const val CALENDAR_ROWS = 5
private const val CALENDAR_COLUMNS = 7
class CalendarView {


    fun createCalendarList(year: Int, month: Int): List<CalendarInput> {
        val daysInMonth = YearMonth.of(year, month).lengthOfMonth()
        val calendarInputs = mutableListOf<CalendarInput>()
        for (i in 1..daysInMonth) {
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

        var canvasSize by remember {
            mutableStateOf(Size.Zero)
        }
        var clickedAnimationOffset by remember {
            mutableStateOf(Offset.Zero)
        }

        var animationRadius by remember {
            mutableStateOf(0f)
        }

        val scope = rememberCoroutineScope()

        Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = month,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                fontSize = 40.sp
            )
            Canvas(modifier = Modifier.
                        fillMaxSize().
                        pointerInput(true){
                            detectTapGestures (
                                onTap = {offset ->
                                    val column = (offset.x / canvasSize.width * CALENDAR_COLUMNS).toInt() + 1
                                    val row = (offset.y / canvasSize.height * CALENDAR_ROWS).toInt() + 1
                                    val day = column + (row -1) * CALENDAR_COLUMNS
                                    if (day <= calendarInput.size ){
                                        onDayClick(day)
                                        scope.launch {
                                            animate(0f, 225f, animationSpec = tween(300)){
                                                value, _ ->
                                                    animationRadius = value
                                            }
                                        }
                                    }
                                }
                            )
                        }) {
                val canvasHeight = size.height
                val canvasWidth = size.width
                canvasSize = Size(canvasWidth, canvasHeight)
                val ySteps = canvasHeight / CALENDAR_ROWS
                val xSteps = canvasWidth / CALENDAR_COLUMNS

                val column = (clickedAnimationOffset.x / canvasSize.width * CALENDAR_COLUMNS + 1)
                val row = (clickedAnimationOffset.y / canvasSize.height * CALENDAR_ROWS + 1)

                val path = Path().apply {
                    moveTo((column-1)*xSteps, (row-1) * ySteps)
                    lineTo(column*xSteps, (row-1)*ySteps)
                    lineTo(column*xSteps, row*ySteps)
                    lineTo((column*1) * xSteps, row*ySteps)
                    close()
                }

                 clipPath(path){
                     drawCircle(
                         brush = Brush.radialGradient(
                             listOf(Color.Green.copy(0.8f), Color.Green.copy(0.2f)),
                             center = clickedAnimationOffset,
                             radius = animationRadius + 0.1f
                         ),
                         radius = animationRadius + 0.1f,
                         center = clickedAnimationOffset
                     )
                 }

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
                    drawContext.canvas.nativeCanvas.apply {
                        drawText(
                            "${i + 1}",
                            textPositionx,
                            textPositionY,
                            Paint().apply {
                                textSize = textHeight
                                color = Color.Black.toArgb()
                                isFakeBoldText = true
                            }

                        )
                    }
                }

            }
        }
    }
}