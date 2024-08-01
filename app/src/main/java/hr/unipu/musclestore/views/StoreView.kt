package hr.unipu.musclestore.views

import hr.unipu.musclestore.composables.CustomCard
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import hr.unipu.musclestore.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen() {
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
        Box(modifier = Modifier.background(Color.LightGray).padding(8.dp).fillMaxHeight()) {
            // non-active card
            CustomCard(
                imageUrl = R.drawable.lifter, // Replace with your actual drawable resource id
                headerText = "Header Text",
                createdAt = "28 Feb 2024",
                postedBy = "Dominik Ruzic",
                downloads = 156
            )
        }


    }
}
