package hr.unipu.musclestore.views

import TokenManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import hr.unipu.musclestore.R

@Composable
fun ProfileView(navController: NavController) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                Modifier
                    .padding(start = 42.dp, top = 42.dp, end = 42.dp, bottom = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Image on the left
                    Image(
                        painter = painterResource(id = R.drawable.me), // Replace with your image resource
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(168.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.weight(1f).fillMaxWidth()
                    ) {
                        // Text with name
                        Text(
                            text = "Kora",
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Logout button
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

            // New Box for Statistics
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
                        Text(text = "Streak: 57 days", fontSize = 16.sp)
                        Text(text = "Median per week: 3 days", fontSize = 16.sp)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Separator Line
                        Divider(color = Color.Gray, thickness = 1.dp)

                        Spacer(modifier = Modifier.height(16.dp))

                        // More Stats
                        Text(text = "Current Plan: Ulul v3", fontSize = 16.sp)
                        Text(text = "Used plan for: 10 days", fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Card(
                Modifier
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Button(
                        onClick = { /* Do nothing for now */ },
                        shape = RoundedCornerShape(8.dp) // Less rounded corners
                    ) {
                        Text(text = "Change credentials", fontSize = 16.sp, color = Color.White)
                    }
                    Button(
                        onClick = { /* Do nothing for now */ },
                        colors = ButtonDefaults.buttonColors(Color.Red.copy(alpha = 0.7f)),
                        shape = RoundedCornerShape(8.dp) // Less rounded corners
                    ) {
                        Text(text = "Delete data", fontSize = 16.sp)
                    }
                    Button(
                        onClick = { /* Do nothing for now */ },
                        colors = ButtonDefaults.buttonColors(Color.Red.copy(alpha = 0.7f)),
                        shape = RoundedCornerShape(8.dp) // Less rounded corners
                    ) {
                        Text(text = "Delete account", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
