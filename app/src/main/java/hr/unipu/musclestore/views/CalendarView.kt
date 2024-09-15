package hr.unipu.musclestore.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.unipu.musclestore.data.CalendarInput
import hr.unipu.musclestore.data.WorkoutNotation
import hr.unipu.musclestore.viewmodel.WorkoutPlanViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val CALENDAR_COLUMNS = 5
@Composable
fun CalendarScreen(
    workoutPlanViewModel: WorkoutPlanViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var month by remember { mutableStateOf(LocalDate.now().monthValue) }
    var year by remember { mutableStateOf(LocalDate.now().year) }
    var clickedDay by remember { mutableStateOf<CalendarInput?>(null) }
    var workoutNotations by remember { mutableStateOf<List<WorkoutNotation>>(emptyList()) }

    // Fetch workout notations and update calendar input list
    LaunchedEffect(Unit) {
        workoutPlanViewModel.getAllWorkoutNotations(context) { notations, _ ->
            workoutNotations = notations ?: emptyList()
        }
    }

    val calendarInputList = remember(month, year) {
        createCalendarList(year, month)
    }
    val daysInMonth = calendarInputList.size
    val currentMonthString = remember(month) {
        LocalDate.now().withMonth(month).month.toString()
            .lowercase(Locale.getDefault())
            .replaceFirstChar { it.titlecase(Locale.getDefault()) }
    }

    Column(modifier = modifier) {
        Spacer(modifier = Modifier.padding(8.dp))
        // Month and Year Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = {
                month = if (month == 1) 12 else month - 1
                if (month == 12) year-- // Adjust year when going from January to December
            }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Previous Month")
            }
            Text(
                text = "$currentMonthString $year",
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                fontSize = 40.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            IconButton(onClick = {
                month = if (month == 12) 1 else month + 1
                if (month == 1) year++ // Adjust year when going from December to January
            }) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next Month")
            }
        }

        // Calendar Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(CALENDAR_COLUMNS),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // Add padding around the whole calendar
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(daysInMonth) { day ->
                val hasWorkout = workoutNotations.any {
                    val notationDate = LocalDate.parse(it.timestamp, DateTimeFormatter.ISO_DATE_TIME)
                    notationDate.monthValue == month && notationDate.year == year && notationDate.dayOfMonth == day
                }
                CalendarCell(
                    day = day+1,
                    isSelected = clickedDay?.day == day,
                    hasWorkout = hasWorkout, // Pass hasWorkout parameter
                    onClick = {
                        clickedDay = calendarInputList.firstOrNull { it.day == day }
                    }
                )
            }
        }
    }
}




fun createCalendarList(year: Int, month: Int): List<CalendarInput> {
    val yearMonth = YearMonth.of(year, month)
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfMonth = yearMonth.atDay(1).dayOfWeek

    // Compute zero-based index for the first day of the month
    val daysBeforeMonthStart = (firstDayOfMonth.value % 7)

    val calendarInputList = mutableListOf<CalendarInput>()

    // Add actual days of the month
    for (day in 1..daysInMonth) {
        calendarInputList.add(CalendarInput(day = day))
    }

    return calendarInputList
}