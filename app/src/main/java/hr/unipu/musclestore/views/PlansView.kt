package hr.unipu.musclestore.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import hr.unipu.musclestore.R
import hr.unipu.musclestore.composables.CustomCard
import hr.unipu.musclestore.data.User
import hr.unipu.musclestore.data.WorkoutPlan
import hr.unipu.musclestore.utils.Base64Manager.decodeBase64ToBitmap
import hr.unipu.musclestore.utils.TimestampManager
import hr.unipu.musclestore.viewmodel.WorkoutPlanViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlansScreen(navController: NavController) {
    val workoutPlanViewModel: WorkoutPlanViewModel = viewModel()
    val context = LocalContext.current

    var text by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }
    var workoutPlans by remember { mutableStateOf<List<WorkoutPlan>>(emptyList()) }
    var activeWorkoutPlan by remember { mutableStateOf<WorkoutPlan?>(null) }
    var user by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        // Fetch all workout plans for the user
        workoutPlanViewModel.getWorkoutPlansForUser(context) { plans, fetchedUser, _ ->
            workoutPlans = plans ?: emptyList()
            user = fetchedUser
        }

        // Fetch the active workout plan separately
        workoutPlanViewModel.getActiveWorkoutPlan(context) { activePlan, _ ->
            activeWorkoutPlan = activePlan
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
    ) {
        // Active Workout Plan Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(8.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SearchBar(
                        query = text,
                        onQueryChange = { text = it },
                        onSearch = { /* Handle search action */ },
                        active = false,
                        onActiveChange = { /* Handle active state change */ },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search icon") },
                        modifier = Modifier.weight(1f),
                        content = {
                            // Define the content to be displayed when the search bar is active
                        }
                    )

                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Filter")
                    }
                }

                Text(
                    text = "Active",
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.headlineMedium
                )

                // Display the active workout plan if it exists, otherwise show a placeholder
                if (activeWorkoutPlan != null) {
                    val imageBitmap = user?.profilePicture?.let { profilePictureBase64 ->
                        decodeBase64ToBitmap(profilePictureBase64)?.asImageBitmap()
                    } ?: ImageBitmap.imageResource(id = R.drawable.lifter) // Fallback image if null

                    val section = activeWorkoutPlan?.sections?.firstOrNull()
                    val exercise = section?.exercises?.firstOrNull()
                    val formattedTimestamp = TimestampManager.formatTimestamp(activeWorkoutPlan!!.timestamp)

                    CustomCard(
                        imageBitmap = imageBitmap, // Use the decoded user profile image or fallback
                        headerText = exercise?.title ?: "No Exercise", // Use exercise title or default
                        createdAt = formattedTimestamp, // Display the formatted timestamp
                        postedBy = "${user?.firstName} ${user?.lastName}", // User's name
                        onClick = {
                            // Navigate to DetailedPlansView with the active plan
                            navController.navigate("DetailedPlansView/${activeWorkoutPlan!!.planId}")
                        }
                    )
                } else {
                    // Show a placeholder if there's no active workout plan
                    CustomCard(
                        imageBitmap = ImageBitmap.imageResource(id = R.drawable.lifter), // Placeholder image
                        headerText = "No Active Workout Plan",
                        createdAt = "",
                        postedBy = "",
                        onClick = {}
                    )
                }
            }
        }

        if (showFilterDialog) {
            AlertDialog(
                onDismissRequest = { showFilterDialog = false },
                shape = RoundedCornerShape(12.dp),
                containerColor = Color.White,
                text = {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Filter",
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                        Button(
                            onClick = { showFilterDialog = false },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Option 1")
                        }
                        Button(
                            onClick = { showFilterDialog = false },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Option 2")
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {}
            )
        }

        Box(
            modifier = Modifier
                .background(Color.LightGray)
                .fillMaxWidth()
                .fillMaxHeight()
                .weight(1f)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // Filter out the active workout plan from the list
                val filteredPlans = workoutPlans.filter { it.planId != activeWorkoutPlan?.planId }

                items(filteredPlans) { plan ->
                    // Decode the user's profile picture from Base64 to Bitmap or use fallback
                    val imageBitmap = user?.profilePicture?.let { profilePictureBase64 ->
                        decodeBase64ToBitmap(profilePictureBase64)?.asImageBitmap()
                    } ?: ImageBitmap.imageResource(id = R.drawable.lifter) // Fallback image if null

                    // Get the first exercise and its title
                    val section = plan.sections.firstOrNull()
                    val exercise = section?.exercises?.firstOrNull()

                    // Use formatted timestamp
                    val formattedTimestamp = TimestampManager.formatTimestamp(plan.timestamp)

                    CustomCard(
                        imageBitmap = imageBitmap, // Use the decoded user profile image or fallback
                        headerText = exercise?.title ?: "No Exercise", // Use exercise title or default
                        createdAt = formattedTimestamp, // Display the formatted timestamp
                        postedBy = "${user?.firstName} ${user?.lastName}", // User's name
                        onClick = {
                            // Navigate to DetailedPlansView with the selected plan
                            navController.navigate("DetailedPlansView/${plan.planId}")
                        }
                    )
                }
            }

            FloatingActionButton(
                onClick = { navController.navigate("AddPlanScreen") },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    }
}
