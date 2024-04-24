package hr.unipu.musclestore

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hr.unipu.musclestore.Views.CalendarView
import hr.unipu.musclestore.Views.HomeScreen
import hr.unipu.musclestore.Views.PlansScreen
import hr.unipu.musclestore.Views.ProfileView
import hr.unipu.musclestore.Views.StoreScreen
import hr.unipu.musclestore.ui.theme.MuscleStoreTheme

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MuscleStoreTheme {

                val navController = rememberNavController()
                

                // A surface container using the 'background' color from the theme
                val items = listOf(

                    // Moguce je dodatai extended icons set!

                    BottomNavigationItem(
                        title = "home",
                        selectedIcon = Icons.Filled.Home,
                        unselectedIcon = Icons.Outlined.Home

                    ),
                    BottomNavigationItem(
                        title = "calendar",
                        selectedIcon = Icons.Filled.DateRange,
                        unselectedIcon = Icons.Outlined.DateRange

                    ),
                    BottomNavigationItem(
                        title = "plans",
                        selectedIcon = Icons.Filled.List,
                        unselectedIcon = Icons.Outlined.List

                    ),
                    BottomNavigationItem(
                        title = "store",
                        selectedIcon = Icons.Filled.ShoppingCart,
                        unselectedIcon = Icons.Outlined.ShoppingCart

                    ),
                    BottomNavigationItem(
                        title = "profile",
                        selectedIcon = Icons.Filled.Person,
                        unselectedIcon = Icons.Outlined.Person

                    ),
                )

                // survives screen rotations and configurations
                var selectedItemIndex by rememberSaveable {
                    mutableIntStateOf(0)
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {


                    Scaffold (
                        bottomBar = {
                            NavigationBar {
                                items.forEachIndexed { index, item ->
                                    NavigationBarItem(
                                        selected = selectedItemIndex == index,
                                        onClick = {
                                            selectedItemIndex = index
                                            navController.navigate(item.title.replaceFirstChar { it.uppercase() } + "View")
                                        },
                                        label = {
                                            Text(text = item.title)
                                        },
                                        icon = { BadgedBox(badge = {

                                            // za dodati npr notifikacije neke, brojeve, tockicu
//                                            Badge {
//                                                Text(text = "Test")
//                                            }

                                        }) {
                                            Icon(imageVector = if (index == selectedItemIndex) {item.selectedIcon} else item.selectedIcon, contentDescription = item.title)
                                        } })
                                }
                            }
                        }
                    ){
                        NavHost(navController = navController, startDestination = "HomeView", route = "mainNavHost") {
                            composable("HomeView") {
                                HomeScreen()
                            }
                            composable("CalendarView") {

                                val calendarView = CalendarView()

                                val calendarInputList by remember {
                                    mutableStateOf(calendarView.createCalendarList())
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.White),
                                    contentAlignment = Alignment.TopCenter
                                ){
                                    calendarView.CalendarScreen(
                                        calendarInput = calendarInputList,
                                        month = "April", onDayClick = {},
                                        modifier = Modifier.padding(10.dp).fillMaxWidth().aspectRatio(1.4f))
                                }
                            }
                            composable("PlansView") {

                                    PlansScreen()

                            }
                            composable("StoreView") {
                                StoreScreen()
                            }
                            composable("ProfileView") {
                                ProfileView()
                            }
                        }
                    }

                }
            }
        }
    }
}

//comp za kreirati funckiju
@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return viewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return viewModel(parentEntry)
}
