package hr.unipu.musclestore.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import hr.unipu.musclestore.data.WorkoutPlan
import hr.unipu.musclestore.viewmodel.WorkoutPlanViewModel

@Composable
fun DetailedPlansView(planId: String?, navController: NavController) {
    val workoutPlanViewModel: WorkoutPlanViewModel = viewModel()
    val context = LocalContext.current
    var workoutPlan by remember { mutableStateOf<WorkoutPlan?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showDeleteConfirmation by remember { mutableStateOf(false) } // For delete confirmation dialog

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
                        Text(
                            text = plan.title,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
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
                        // Buttons as part of the LazyColumn
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .height(56.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { showDeleteConfirmation = true }, // Show confirmation dialog
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Delete")
                            }

                            Spacer(modifier = Modifier.width(8.dp)) // Space between buttons

                            Button(
                                onClick = { /* Handle set as active action */ },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Set as Active")
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

    // Delete confirmation dialog
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Workout Plan") },
            text = { Text("Are you sure you want to delete this workout plan?") },
            confirmButton = {
                Button(
                    onClick = {
                        workoutPlan?.let { plan ->
                            workoutPlanViewModel.deleteWorkoutPlanById(context, plan.planId.toString()) { success, message ->
                                if (success) {
                                    navController.popBackStack() // Navigate back after successful deletion
                                } else {
                                    // Handle error (e.g., show a Toast or Snackbar)
                                }
                            }
                        }
                        showDeleteConfirmation = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteConfirmation = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
