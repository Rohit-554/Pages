package io.jadu.pages

import AddNewPage
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.navigation.NavHostController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
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
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import dagger.hilt.android.AndroidEntryPoint
import io.jadu.pages.core.Constants
import io.jadu.pages.core.PreferencesManager
import io.jadu.pages.domain.model.BottomNavigationItem
import io.jadu.pages.presentation.components.BottomNavigationBar
import io.jadu.pages.presentation.navigation.NavigationItem
import io.jadu.pages.presentation.screens.AboutPage
import io.jadu.pages.presentation.screens.HomePage
import io.jadu.pages.presentation.screens.ProfilePage
import io.jadu.pages.presentation.screens.SettingsPage
import io.jadu.pages.presentation.screens.TodoPage
import io.jadu.pages.presentation.screens.introScreens.IntroPager
import io.jadu.pages.presentation.screens.introScreens.IntroScreenTwo
import io.jadu.pages.presentation.viewmodel.NotesViewModel
import io.jadu.pages.presentation.viewmodel.TodoViewModel
import io.jadu.pages.ui.theme.PagesTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
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
    startDestination: String = NavigationItem.IntroPagerScreen.route
) {
    val viewModel: NotesViewModel = hiltViewModel()
    val todoViewModel: TodoViewModel = hiltViewModel()
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val isIntroScreen = preferencesManager.getBoolean(Constants.IS_INTRO_SHOWN)
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.story))
    NavHost(
        modifier = Modifier,
        navController = navHostController,
        startDestination = startDestination
    ) {
        composable(NavigationItem.Home.route) {
            NotesApp(navHostController, viewModel, todoViewModel)
        }
        composable(NavigationItem.CreateNotes.route) {
            AddNewPage(viewModel, navHostController)
        }
        composable(
            "note/{nodeId}",
            arguments = listOf(
                navArgument("nodeId") { type = NavType.LongType }
            )
        ) { navBackStackEntry ->
            val nodeId = navBackStackEntry.arguments?.getLong("nodeId")
            AddNewPage(viewModel, navHostController, nodeId)
        }

        composable(NavigationItem.ProfilePage.route) {
            SettingsPage(navHostController)
            //ProfilePage(PaddingValues(8.dp), navHostController)
        }

        composable(NavigationItem.AboutPage.route) {
            AboutPage(navHostController)
        }

       /* composable(NavigationItem.IntroScreenOne.route) {
            IntroScreenOne(navHostController, pagerState)
        }*/

        composable(NavigationItem.IntroScreenTwo.route){
            IntroScreenTwo(navHostController)
        }

        composable(NavigationItem.IntroPagerScreen.route){
            IntroPager(navHostController)
        }

        composable(NavigationItem.Todo.route) {
            TodoPage(todoViewModel, navHostController)
        }

        composable(NavigationItem.Home2.route) {
            HomePage(viewModel, navHostController)
        }
    }

    if(!isIntroScreen){
        navHostController.navigate(NavigationItem.IntroPagerScreen.route)
    }else{
        navHostController.navigate(NavigationItem.Home.route) {
            popUpTo(NavigationItem.IntroPagerScreen.route) { inclusive = true }
        }
    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NotesApp(
    navHostController: NavHostController,
    viewModel: NotesViewModel,
    todoViewModel: TodoViewModel
) {
    var selectedItemIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    val items = listOf(
        BottomNavigationItem(
            title = "Notes",
            selectedIcon = Icons.Filled.AutoStories,
            unselectedIcon = Icons.Outlined.AutoStories,
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
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            AnimatedContent(
                targetState = selectedItemIndex,
                transitionSpec = {
                    fadeIn(animationSpec = tween(100)) togetherWith fadeOut(animationSpec = tween(300))
                }, label = ""
            ) { targetIndex ->
                when (targetIndex) {
                    0 -> HomePage(viewModel, navHostController)
                    1 -> TodoPage(todoViewModel, navHostController)
                }
            }

        }

    }
}

