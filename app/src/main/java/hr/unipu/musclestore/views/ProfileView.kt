package hr.unipu.musclestore.views

import TokenManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import hr.unipu.musclestore.R
import hr.unipu.musclestore.data.User
import hr.unipu.musclestore.utils.StreakManager
import hr.unipu.musclestore.viewmodel.ProfileViewModel
import hr.unipu.musclestore.viewmodel.WorkoutPlanViewModel
import kotlinx.coroutines.launch
import java.io.IOException

@Composable
fun ProfileView(navController: NavController) {
    val context = LocalContext.current
    val profileViewModel: ProfileViewModel = viewModel()
    val workoutPlanViewModel: WorkoutPlanViewModel = viewModel()

    var userData by remember { mutableStateOf<User?>(null) }
    var profileBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var currentPlanTitle by remember { mutableStateOf<String?>(null) }
    var streak by remember { mutableStateOf(0) }
    var weeklyAverage by remember { mutableStateOf(0.0) }
    var monthlyAverage by remember { mutableStateOf(0.0) }

    val coroutineScope = rememberCoroutineScope()

    // Fetch user data and workout plan details
    LaunchedEffect(Unit) {
        // Fetch user data
        profileViewModel.fetchUserData(context) { user, error ->
            if (user != null) {
                userData = user
                if (user.profilePicture != null) {
                    user.profilePicture.let { base64 ->
                        profileBitmap = profileViewModel.decodeBase64Image(base64)
                    }
                }
            } else {
                Log.e("ProfileView", "Failed to fetch user data: $error")
                Toast.makeText(context, "Failed to fetch user data.", Toast.LENGTH_SHORT).show()
            }
        }

        // Fetch active workout plan ID and title
        workoutPlanViewModel.getActiveWorkoutPlan(context) { workoutPlan, _ ->
            val activeWorkoutPlanId = workoutPlan?.planId
            Log.d("ProfileView", "Active Workout Plan ID: $activeWorkoutPlanId")
            activeWorkoutPlanId?.let { planId ->
                workoutPlanViewModel.getWorkoutPlanById(context, planId.toString()) { workoutPlanDetails, _ ->
                    val title = workoutPlanDetails?.title
                    Log.d("ProfileView", "Fetched Plan Title: $title")
                    currentPlanTitle = title
                }
            } ?: run {
                Log.d("ProfileView", "No active workout plan found")
            }
        }

        // Fetch workout notations to calculate statistics
        workoutPlanViewModel.getAllWorkoutNotations(context) { notations, _ ->
            if (notations != null) {
                streak = StreakManager.calculateStreak(notations)
                weeklyAverage = StreakManager.calculateWeeklyAverage(notations)
                monthlyAverage = StreakManager.calculateMonthlyAverage(notations)
            }
        }
    }

    // Handle image selection and upload
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                coroutineScope.launch {
                    profileViewModel.uploadProfilePicture(context, bitmap) { success, response ->
                        if (success) {
                            // Refresh user data after successful upload
                            profileViewModel.fetchUserData(context) { user, error ->
                                if (user != null) {
                                    userData = user
                                    user.profilePicture?.let { base64 ->
                                        profileBitmap = profileViewModel.decodeBase64Image(base64)
                                    }
                                }
                            }
                            Toast.makeText(context, "Profile picture updated successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, response ?: "Failed to upload profile picture.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(context, "Error processing image.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            userData?.let { user ->
                Box(
                    Modifier
                        .padding(start = 42.dp, top = 42.dp, end = 42.dp, bottom = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                launcher.launch("image/*")
                            }
                        ) {
                            // Display profile picture or default image
                            val imageModifier = Modifier
                                .size(168.dp)
                                .clip(RoundedCornerShape(16.dp))

                            if (profileBitmap != null) {
                                Image(
                                    bitmap = profileBitmap!!.asImageBitmap(),
                                    contentDescription = "Profile Picture",
                                    modifier = imageModifier
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.me), // Default image resource
                                    contentDescription = "Profile Picture",
                                    modifier = imageModifier
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = user.firstName,
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    // Clear the token and navigate to InitView
                                    TokenManager.clearToken(context)
                                    navController.navigate("InitView") {
                                        // Clear backstack to prevent navigation back to any previous screen
                                        popUpTo("InitView") {
                                            inclusive = true
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(Color.Red.copy(alpha = 0.7f)),
                                shape = RoundedCornerShape(8.dp) // Less rounded corners
                            ) {
                                Text(text = "Logout", color = Color.White, fontSize = 16.sp)
                            }
                        }
                    }
                }

                // Box for Statistics
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Card(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 3.dp
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 42.dp, top = 16.dp, end = 42.dp, bottom = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Card Header
                            Text(
                                text = "Statistics",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier
                                    .padding(bottom = 8.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            // Stats
                            Text(text = "Streak: $streak days", fontSize = 16.sp)
                            Text(text = "Median per week: ${weeklyAverage.toInt()} days", fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            // Separator Line
                            Divider(color = Color.Gray, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(16.dp))
                            // More Stats
                            Text(text = "Current Plan: ${currentPlanTitle ?: "Loading..."}", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
