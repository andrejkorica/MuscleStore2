package hr.unipu.musclestore.views

import CustomCard
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import hr.unipu.musclestore.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlansScreen() {
    var text by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }

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
                        onQueryChange = { text = it },
                        onSearch = { /* Handle search action */ },
                        active = false, // This should be handled internally by SearchBar
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

                Text(
                    text = "Active",
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.headlineMedium
                )

                CustomCard(
                    imageUrl = R.drawable.lifter, // Replace with your actual drawable resource id
                    headerText = "Header Text",
                    createdAt = "28 Feb 2024",
                    postedBy = "Dominik Ruzic",
                    downloads = 156
                )
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
                .fillMaxWidth()
                .fillMaxHeight()
                .weight(1f)
        ) {

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(5) {
                    CustomCard(
                        imageUrl = R.drawable.lifter, // Replace with your actual drawable resource id
                        headerText = "Header Text",
                        createdAt = "28 Feb 2024",
                        postedBy = "Dominik Ruzic",
                        downloads = 156
                    )
                }
            }

            // ADD button
            FloatingActionButton(
                onClick = { /* Handle plus button click */ },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")

            }
        }
    }
}
