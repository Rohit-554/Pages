package io.jadu.pages

import AddNewPage
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.navigation.NavHostController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import io.jadu.pages.domain.model.BottomNavigationItem
import io.jadu.pages.presentation.components.BottomNavigationBar
import io.jadu.pages.presentation.navigation.NavigationItem
import io.jadu.pages.presentation.screens.HomePage
import io.jadu.pages.presentation.screens.TodoPage
import io.jadu.pages.presentation.viewmodel.NotesViewModel
import io.jadu.pages.ui.theme.ButtonBlue
import io.jadu.pages.ui.theme.PagesTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val requestPermissions =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->

            }
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            PagesTheme {
                Scaffold { padding ->
                    Spacer(modifier = Modifier.padding(padding))
                    AppNavHost(navHostController = navController)
                }
            }
        }
    }
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    startDestination: String = NavigationItem.Home.route
) {
    val viewModel: NotesViewModel = hiltViewModel()
    NavHost(
        modifier = Modifier,
        navController = navHostController,
        startDestination = startDestination
    ) {
        composable(NavigationItem.Home.route) {
            NotesApp(navHostController, viewModel)
        }
        composable(NavigationItem.CreateNotes.route) {
            AddNewPage(viewModel, navHostController)
        }
        composable(
            "note/{nodeId}",
            arguments = listOf(
                navArgument("nodeId") {type = NavType.LongType}
            )
        ) { navBackStackEntry ->
            val nodeId = navBackStackEntry.arguments?.getLong("nodeId")
            AddNewPage(viewModel, navHostController, nodeId)
        }
    }

}

@Composable
fun NotesApp(navHostController: NavHostController, viewModel: NotesViewModel) {
    var selectedItemIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    val items = listOf(
        BottomNavigationItem(
            title = "Notes",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            isTodo = false,
        ),
        BottomNavigationItem(
            title = "To-dos",
            selectedIcon = Icons.Filled.CheckCircle,
            unselectedIcon = Icons.Outlined.CheckCircle,
            isTodo = false,
        ),
    )
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(items, selectedItemIndex) {
                selectedItemIndex = it
            }
        },
        contentWindowInsets = WindowInsets(
            top = 0.dp,
            bottom = 0.dp
        ),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navHostController.navigate(NavigationItem.CreateNotes.route)
                },
                content = {
                    Icon(Icons.Default.Add, contentDescription = "Add Note")
                },
                containerColor = ButtonBlue
            )
        }
    ) { innerPadding ->
        Column(
            Modifier.padding(innerPadding)
        ) {
            when (selectedItemIndex) {
                0 -> HomePage(viewModel, navHostController)
                1 -> TodoPage()
            }
        }
    }
}

