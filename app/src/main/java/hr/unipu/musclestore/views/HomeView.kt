package hr.unipu.musclestore.views

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.unipu.musclestore.R
import hr.unipu.musclestore.data.WorkoutPlan
import hr.unipu.musclestore.utils.StreakManager
import hr.unipu.musclestore.utils.TimestampManager
import hr.unipu.musclestore.viewmodel.WorkoutPlanViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(viewModel: WorkoutPlanViewModel = viewModel()) {
    val (activeWorkoutPlan, setActiveWorkoutPlan) = remember { mutableStateOf<WorkoutPlan?>(null) }
    val (workoutRecordedToday, setWorkoutRecordedToday) = remember { mutableStateOf(false) }
    val (streak, setStreak) = remember { mutableStateOf(0) }
    val (weeklyAverage, setWeeklyAverage) = remember { mutableStateOf(0.0) }
    val (monthlyAverage, setMonthlyAverage) = remember { mutableStateOf(0.0) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        // Fetch the active workout plan
        viewModel.getActiveWorkoutPlan(context) { plan, _ ->
            setActiveWorkoutPlan(plan)
        }

        viewModel.getAllWorkoutNotations(context) { notations, _ ->
            val today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")

            val recordedToday = notations?.any { notation ->
                try {
                    val dateTime = LocalDateTime.parse(notation.timestamp, formatter).toLocalDate()
                    val formattedDate = dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                    formattedDate == today
                } catch (e: Exception) {
                    Log.e("HomeScreen", "Error parsing timestamp: ${e.message}")
                    false
                }
            } ?: false

            setWorkoutRecordedToday(recordedToday)

            // Calculate streak, weekly average, and monthly average
            notations?.let {
                setStreak(StreakManager.calculateStreak(it))
                setWeeklyAverage(StreakManager.calculateWeeklyAverage(it))
                setMonthlyAverage(StreakManager.calculateMonthlyAverage(it))
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (activeWorkoutPlan == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
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
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(bottom = 72.dp) // Leave space for the button
            ) {
                item {
                    // Header for the workout table
                    Text(
                        text = "Active Workout",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                item {
                    // Display the workout plan table
                    WorkoutTable(workoutPlan = activeWorkoutPlan)
                }



                item {
                    // Streak section

                    Column(modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)) {
                        Text(
                            text = "Streak",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$streak days",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                item {
                    // Weekly Average section
                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        Text(
                            text = "Weekly Average",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${weeklyAverage.toInt()} workouts",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                item {
                    // Monthly Average section
                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        Text(
                            text = "Monthly Average",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${monthlyAverage.toInt()} workouts",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Add a spacer to push the button to the bottom
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    // Button to record workout
                    Button(
                        onClick = {
                            val timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                            viewModel.createWorkoutNotation(context, timestamp) { success, response ->
                                if (success) {
                                    Log.d("HomeScreen", "Workout notation created successfully")
                                    setWorkoutRecordedToday(true) // Disable the button after recording
                                } else {
                                    Log.e("HomeScreen", "Error creating workout notation: $response")
                                }
                            }
                        },
                        enabled = !workoutRecordedToday, // Disable if workout recorded today
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text(text = "Record Workout")
                    }

                    // Show a message if the workout is already recorded today
                    if (workoutRecordedToday) {
                        Text(
                            text = "Workout already recorded for today",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
