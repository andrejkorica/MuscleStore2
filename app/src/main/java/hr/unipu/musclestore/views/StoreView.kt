package hr.unipu.musclestore.views

import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import hr.unipu.musclestore.R
import hr.unipu.musclestore.composables.CustomCard
import hr.unipu.musclestore.data.User
import hr.unipu.musclestore.utils.Base64Manager.decodeBase64ToBitmap
import hr.unipu.musclestore.utils.Base64Manager.drawableToBitmap
import hr.unipu.musclestore.utils.TimestampManager
import hr.unipu.musclestore.viewmodel.ProfileViewModel
import hr.unipu.musclestore.viewmodels.StoreViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(
    storeViewModel: StoreViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel(),
    navController: NavController // Add NavController as a parameter
) {
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }
    var currentUser by remember { mutableStateOf<User?>(null) }

    // Fetch the current user data
    LaunchedEffect(Unit) {
        profileViewModel.fetchUserData(context) { user, error ->
            if (user != null) {
                currentUser = user
            }
        }
        storeViewModel.fetchAllWorkoutPlans(context)
    }

    // Convert drawable resource to Drawable
    val lifterDrawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.lifter)

    // Convert Drawable to Bitmap and then to ImageBitmap
    val defaultImageBitmap = lifterDrawable?.let { drawableToBitmap(it)?.asImageBitmap() }
        ?: ImageBitmap.imageResource(id = R.drawable.lifter) // Fallback if conversion fails

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
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
                        Icon(Icons.Default.List, contentDescription = "Filter")
                    }
                }
            }
        }

        // Dialog modal
        if (showFilterDialog) {
            AlertDialog(
                onDismissRequest = { showFilterDialog = false },
                shape = RoundedCornerShape(12.dp), // Rounded corners
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
                .padding(8.dp)
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            // Use LazyColumn to display workout plans
            LazyColumn {
                // Ensure currentUser is not null before filtering
                val filteredPlans = storeViewModel.workoutPlans.filter { workoutPlan ->
                    workoutPlan.user.email != currentUser?.email
                }

                // Display workout plans
                items(filteredPlans) { workoutPlan ->
                    // Decode the user's profile picture from Base64
                    val userImageBitmap = workoutPlan.user.profilePicture?.let { profilePictureBase64 ->
                        decodeBase64ToBitmap(profilePictureBase64)?.asImageBitmap()
                    }

                    // Use the decoded user image or fallback to the default image
                    val imageBitmap = userImageBitmap ?: defaultImageBitmap

                    CustomCard(
                        imageBitmap = imageBitmap, // Pass the ImageBitmap here
                        headerText = workoutPlan.title, // Set the workout title
                        createdAt = TimestampManager.formatTimestamp(workoutPlan.timestamp), // Format and display the timestamp
                        postedBy = "${workoutPlan.user.firstName} ${workoutPlan.user.lastName}",
                        onClick = {
                            // Navigate to DetailedStoreView with the selected plan ID
                            navController.navigate("DetailedStoreView/${workoutPlan.planId}")
                        }
                    )
                }
            }
        }
    }
}
