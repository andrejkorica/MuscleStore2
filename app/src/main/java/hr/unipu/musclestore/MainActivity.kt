package hr.unipu.musclestore

import LoginScreen
import TokenManager
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import hr.unipu.musclestore.data.CalendarInput
import hr.unipu.musclestore.ui.theme.MuscleStoreTheme
import hr.unipu.musclestore.views.AuthScreen
import hr.unipu.musclestore.views.CalendarView
import hr.unipu.musclestore.views.HomeScreen
import hr.unipu.musclestore.views.PlansScreen
import hr.unipu.musclestore.views.ProfileView
import hr.unipu.musclestore.views.SignUpScreen
import hr.unipu.musclestore.views.StoreScreen
import java.time.LocalDate
import java.util.Locale

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

                // Check for token and determine the starting route
                val token = TokenManager.getToken(context = this)
                val startDestination by rememberUpdatedState(if (token != null) "HomeView" else "InitView")

                val items = listOf(
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
                    )
                )

                var selectedItemIndex by rememberSaveable {
                    mutableIntStateOf(0)
                }
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Scaffold(
                        bottomBar = {
                            if (currentRoute != "InitView" && currentRoute != "SignUpView" && currentRoute != "LoginView") {
                                NavigationBar {
                                    items.forEachIndexed { index, item ->
                                        NavigationBarItem(
                                            selected = selectedItemIndex == index,
                                            onClick = {
                                                selectedItemIndex = index
                                                navController.navigate(item.title.replaceFirstChar { it.uppercase() } + "View") {
                                                    // Ensure to avoid multiple copies of the same destination
                                                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                                }
                                            },
                                            label = {
                                                Text(text = item.title)
                                            },
                                            icon = {
                                                BadgedBox(badge = {
                                                    // Badge logic can be added here
                                                }) {
                                                    Icon(
                                                        imageVector = if (index == selectedItemIndex) {
                                                            item.selectedIcon
                                                        } else item.unselectedIcon,
                                                        contentDescription = item.title
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    ) {
                        Box {
                            NavHost(
                                navController = navController,
                                startDestination = startDestination,
                            ) {
                                composable("InitView") {
                                    AuthScreen(navController = navController)
                                }
                                composable("HomeView") {
                                    HomeScreen()
                                }
                                composable("CalendarView") {
                                    val calendarView = CalendarView()
                                    val currentYear = LocalDate.now().year
                                    var month by remember { mutableIntStateOf(LocalDate.now().monthValue) }
                                    val currentMonthString = remember(month) {
                                        LocalDate.now().withMonth(month).month.toString()
                                            .lowercase(Locale.getDefault())
                                            .replaceFirstChar { it.titlecase(Locale.getDefault()) }
                                    }
                                    var calendarInputList by remember(month) {
                                        mutableStateOf(calendarView.createCalendarList(currentYear, month))
                                    }
                                    var clickedCalendarElem by remember {
                                        mutableStateOf<CalendarInput?>(null)
                                    }
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.TopCenter
                                    ) {
                                        calendarView.CalendarScreen(
                                            month = currentMonthString,
                                            calendarInput = calendarInputList,
                                            onDayClick = { day -> clickedCalendarElem = calendarInputList.first { it.day == day } },
                                            modifier = Modifier
                                                .padding(10.dp)
                                                .fillMaxWidth()
                                                .aspectRatio(1.1f),
                                            onPreviousMonthClick = {
                                                month = if (month == 1) 12 else month - 1
                                                calendarInputList = calendarView.createCalendarList(currentYear, month)
                                            },
                                            onNextMonthClick = {
                                                month = if (month == 12) 1 else month + 1
                                                calendarInputList = calendarView.createCalendarList(currentYear, month)
                                            }
                                        )
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp)
                                                .align(Alignment.CenterEnd)
                                        ) {
                                            Spacer(modifier = Modifier.height(60.dp))
                                            clickedCalendarElem?.notes?.forEach {
                                                Text(
                                                    if (it.contains("Day")) it else "- $it",
                                                    color = Color.Black,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = if (it.contains("Day")) 25.sp else 18.sp
                                                )
                                            }
                                        }
                                    }
                                }
                                composable("PlansView") {
                                    PlansScreen()
                                }
                                composable("StoreView") {
                                    StoreScreen()
                                }
                                composable("ProfileView") {
                                    ProfileView(navController = navController)
                                }
                                composable("SignUpView") {
                                    SignUpScreen(navController = navController)
                                }
                                composable("LoginView") {
                                    LoginScreen(navController = navController)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Extension function to get shared view model
@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return viewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return viewModel(parentEntry)
}
