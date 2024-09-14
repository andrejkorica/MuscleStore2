package hr.unipu.musclestore.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.runtime.*
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
import hr.unipu.musclestore.data.AddFromStoreResponse
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
    var addedFromStoreRecords by remember { mutableStateOf<List<AddFromStoreResponse>>(emptyList()) }
    var activeWorkoutPlan by remember { mutableStateOf<WorkoutPlan?>(null) }
    var user by remember { mutableStateOf<User?>(null) }

    // Fetch data
    LaunchedEffect(Unit) {
        workoutPlanViewModel.getWorkoutPlansForUser(context) { plans, fetchedUser, _ ->
            workoutPlans = plans ?: emptyList()
            user = fetchedUser
        }

        workoutPlanViewModel.getActiveWorkoutPlan(context) { activePlan, _ ->
            activeWorkoutPlan = activePlan
        }

        workoutPlanViewModel.getAllAddedFromStore(context) { records, _ ->
            addedFromStoreRecords = records ?: emptyList()
        }
    }

    // Filter workout plans based on search query
    val filteredPlans = workoutPlans.filter {
        val exerciseTitle = it.sections.firstOrNull()?.exercises?.firstOrNull()?.title ?: ""
        exerciseTitle.contains(text, ignoreCase = true) && it.planId != activeWorkoutPlan?.planId
    } + addedFromStoreRecords.mapNotNull { it.workoutPlan }.filter {
        val exerciseTitle = it.sections.firstOrNull()?.exercises?.firstOrNull()?.title ?: ""
        exerciseTitle.contains(text, ignoreCase = true) && it.planId != activeWorkoutPlan?.planId
    }

    // Debug logs
    LaunchedEffect(text) {
        println("Search Query: $text")
        println("Filtered Plans Count: ${filteredPlans.size}")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
    ) {
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
                        onQueryChange = { newText -> text = newText },
                        onSearch = { /* Handle search action if needed */ },
                        active = false,
                        onActiveChange = { /* Handle active state change */ },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search icon") },
                        modifier = Modifier.weight(1f),
                        content = {
                            // Define the content to be displayed when the search bar is active
                        }
                    )
                }

                Text(
                    text = "Active",
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.headlineMedium
                )

                if (activeWorkoutPlan != null) {
                    val imageBitmap = user?.profilePicture?.let { profilePictureBase64 ->
                        decodeBase64ToBitmap(profilePictureBase64)?.asImageBitmap()
                    } ?: ImageBitmap.imageResource(id = R.drawable.lifter)

                    val section = activeWorkoutPlan?.sections?.firstOrNull()
                    val exercise = section?.exercises?.firstOrNull()
                    val formattedTimestamp = TimestampManager.formatTimestamp(activeWorkoutPlan!!.timestamp)

                    CustomCard(
                        imageBitmap = imageBitmap,
                        headerText = exercise?.title ?: "No Exercise",
                        createdAt = formattedTimestamp,
                        postedBy = "${user?.firstName} ${user?.lastName}",
                        onClick = {
                            navController.navigate("DetailedPlansView/${activeWorkoutPlan!!.planId}")
                        }
                    )
                } else {
                    CustomCard(
                        imageBitmap = ImageBitmap.imageResource(id = R.drawable.lifter),
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
                items(filteredPlans) { item ->
                    val imageBitmap = item.user.profilePicture?.let { profilePictureBase64 ->
                        decodeBase64ToBitmap(profilePictureBase64)?.asImageBitmap()
                    } ?: ImageBitmap.imageResource(id = R.drawable.lifter)

                    val section = item.sections.firstOrNull()
                    val exercise = section?.exercises?.firstOrNull()
                    val formattedTimestamp = TimestampManager.formatTimestamp(item.timestamp)

                    CustomCard(
                        imageBitmap = imageBitmap,
                        headerText = exercise?.title ?: "No Exercise",
                        createdAt = formattedTimestamp,
                        postedBy = "${item.user?.firstName} ${item.user?.lastName}",
                        onClick = {
                            navController.navigate("DetailedPlansView/${item.planId}")
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
