package hr.unipu.musclestore.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import hr.unipu.musclestore.data.AddFromStoreResponse
import hr.unipu.musclestore.data.WorkoutPlan
import hr.unipu.musclestore.viewmodel.WorkoutPlanViewModel

@Composable
fun DetailedPlansView(planId: String?, navController: NavController) {
    val workoutPlanViewModel: WorkoutPlanViewModel = viewModel()
    val context = LocalContext.current
    var workoutPlan by remember { mutableStateOf<WorkoutPlan?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showSetActiveConfirmation by remember { mutableStateOf(false) }
    var addedFromStoreRecords by remember { mutableStateOf<List<AddFromStoreResponse>>(emptyList()) }

    LaunchedEffect(planId) {
        println("Received planId in DetailedPlansView: $planId")
        planId?.let {
            workoutPlanViewModel.getWorkoutPlanById(context, it) { plan, _ ->
                workoutPlan = plan
                isLoading = false
            }
        }

        workoutPlanViewModel.getAllAddedFromStore(context) { records, _ ->
            addedFromStoreRecords = records ?: emptyList()
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
                val isFromStore = addedFromStoreRecords.any { it.workoutPlan?.planId == plan.planId }

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
                        // Buttons as part of the LazyColumn
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .height(56.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { showDeleteConfirmation = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isFromStore) Color.Gray else MaterialTheme.colorScheme.primary
                                ),
                                enabled = !isFromStore
                            ) {
                                Text("Delete")
                            }

                            Spacer(modifier = Modifier.width(8.dp)) // Space between buttons

                            Button(
                                onClick = { showSetActiveConfirmation = true },
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

    // Set Active confirmation dialog
    if (showSetActiveConfirmation) {
        AlertDialog(
            onDismissRequest = { showSetActiveConfirmation = false },
            title = { Text("Set Active Workout Plan") },
            text = { Text("Are you sure you want to set this workout plan as active?") },
            confirmButton = {
                Button(
                    onClick = {
                        workoutPlan?.let { plan ->
                            workoutPlanViewModel.setActiveWorkoutPlan(context, plan.planId) { success, message ->
                                if (success) {
                                    // Handle success (e.g., show a Toast or Snackbar)
                                    navController.popBackStack() // Optionally navigate back
                                } else {
                                    // Handle error (e.g., show a Toast or Snackbar)
                                }
                            }
                        }
                        showSetActiveConfirmation = false
                    }
                ) {
                    Text("Set as Active")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showSetActiveConfirmation = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
