package hr.unipu.musclestore.views

import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import hr.unipu.musclestore.R
import hr.unipu.musclestore.composables.CustomCard
import hr.unipu.musclestore.utils.Base64Manager.drawableToBitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen() {
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }

    // Convert drawable resource to Drawable
    val lifterDrawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.lifter)

    // Convert Drawable to Bitmap and then to ImageBitmap
    val imageBitmap = lifterDrawable?.let { drawableToBitmap(it)?.asImageBitmap() }
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
        ) {
            // non-active card
            CustomCard(
                imageBitmap = imageBitmap, // Pass the ImageBitmap here
                headerText = "Header Text",
                createdAt = "28 Feb 2024",
                postedBy = "Dominik Ruzic",
            )
        }
    }
}
