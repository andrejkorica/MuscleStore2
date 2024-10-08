package hr.unipu.musclestore.views

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import hr.unipu.musclestore.data.WorkoutPlan
import hr.unipu.musclestore.viewmodel.WorkoutPlanViewModel
import androidx.compose.ui.platform.LocalContext

@Composable
fun DetailedStoreView(
    planId: String?,
    navController: NavController
) {
    val context = LocalContext.current
    val workoutPlanViewModel: WorkoutPlanViewModel = viewModel()
    var workoutPlan by remember { mutableStateOf<WorkoutPlan?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showAddToWorkoutPlansConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(planId) {
        planId?.let {
            workoutPlanViewModel.getWorkoutPlanById(context, it) { plan, _ ->
                workoutPlan = plan
                isLoading = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Loading...", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            workoutPlan?.let { plan ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(bottom = 72.dp) // Leave space for the buttons
                ) {
                    item {
                        // Centered and bold header
                        Text(
                            text = plan.title,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                    }

                    item {
                        WorkoutTable(plan)
                    }

                    // Add a spacer to push the buttons to the bottom
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .height(56.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { showAddToWorkoutPlansConfirmation = true },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Add to Workout Plans")
                            }
                        }
                    }
                }
            } ?: run {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No data available", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }

    // Add to Workout Plans confirmation dialog
    if (showAddToWorkoutPlansConfirmation) {
        AlertDialog(
            onDismissRequest = { showAddToWorkoutPlansConfirmation = false },
            title = { Text("Add to Workout Plans") },
            text = { Text("Are you sure you want to add this workout plan to your workout plans?") },
            confirmButton = {
                Button(
                    onClick = {
                        workoutPlan?.let { plan ->
                            workoutPlanViewModel.addWorkoutFromStore(context, plan.planId) { success, _, _ ->
                                if (success) {
                                    navController.popBackStack() // Navigate back after successful addition
                                }
                            }
                        }
                        showAddToWorkoutPlansConfirmation = false
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showAddToWorkoutPlansConfirmation = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
