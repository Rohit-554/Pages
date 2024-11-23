package io.jadu.pages

import AddNewPage
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Scaffold
import androidx.navigation.NavHostController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.paging.compose.collectAsLazyPagingItems
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import dagger.hilt.android.AndroidEntryPoint
import io.jadu.pages.core.Constants
import io.jadu.pages.core.PreferencesManager
import io.jadu.pages.core.Utils
import io.jadu.pages.domain.model.BottomNavigationItem
import io.jadu.pages.domain.model.Notes
import io.jadu.pages.domain.model.PathProperties
import io.jadu.pages.presentation.components.BottomNavigationBar
import io.jadu.pages.presentation.home_widget.ThemeChangeReceiver
import io.jadu.pages.presentation.navigation.NavigationItem
import io.jadu.pages.presentation.screens.AboutPage
import io.jadu.pages.presentation.screens.HomePage
import io.jadu.pages.presentation.screens.SettingsPage
import io.jadu.pages.presentation.screens.TodoPage
import io.jadu.pages.presentation.screens.draw.DrawingApp
import io.jadu.pages.presentation.screens.introScreens.IntroPager
import io.jadu.pages.presentation.screens.introScreens.IntroScreenOne
import io.jadu.pages.presentation.screens.introScreens.IntroScreenTwo
import io.jadu.pages.presentation.viewmodel.NotesViewModel
import io.jadu.pages.presentation.viewmodel.TodoViewModel
import io.jadu.pages.ui.theme.PagesTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val themeChangeReceiver = ThemeChangeReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val filter = IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED)
        registerReceiver(themeChangeReceiver, filter)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            PagesTheme {
                Scaffold { padding ->
                    Spacer(modifier = Modifier.padding(padding))
                    AppNavHost(
                        navHostController = navController,
                        shouldOpenTodoPage = false
                    )
                }
            }

        }
    }
}


@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    startDestination: String = NavigationItem.IntroPagerScreen.route,
    shouldOpenTodoPage: Boolean = false
) {
    val coroutineScope = rememberCoroutineScope()
    val viewModel: NotesViewModel = hiltViewModel()
    val todoViewModel: TodoViewModel = hiltViewModel()
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val isIntroScreen = preferencesManager.getBoolean(Constants.IS_INTRO_SHOWN)
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.story))
    var drawPath by remember { mutableStateOf(List(0) { Pair(Path(), PathProperties()) }) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val pagingNotes = viewModel.notesFlow.collectAsLazyPagingItems()
    val notes = pagingNotes.itemSnapshotList.items

    /*LaunchedEffect(shouldOpenTodoPage) {
        if(shouldOpenTodoPage){
            NavigationItem.createTodo.route
        }
    }*/
    NavHost(
        modifier = Modifier,
        navController = navHostController,
        startDestination = startDestination
    ) {

        composable(NavigationItem.Home.route) {
            DisposableEffect(Unit) {
                drawPath = emptyList()
                viewModel.clearImageUriList()
                viewModel.removeNotesStates()
                onDispose {}
            }
            NotesApp(navHostController, viewModel, todoViewModel, notes, shouldOpenTodoPage)
        }
        composable(NavigationItem.CreateNotes.route) {
            AddNewPage(viewModel, navHostController, drawPath = drawPath, bitmap = bitmap)
        }
        composable(
            "note/{nodeId}",
            arguments = listOf(
                navArgument("nodeId") { type = NavType.LongType }
            )
        ) { navBackStackEntry ->
            val nodeId = navBackStackEntry.arguments?.getLong("nodeId")
            AddNewPage(viewModel, navHostController, nodeId, drawPath, bitmap)
        }

        composable(NavigationItem.SettingsPage.route) {
            SettingsPage(navHostController, viewModel,todoViewModel)
        }

        composable(NavigationItem.AboutPage.route) {
            AboutPage(navHostController)
        }

        /*composable(NavigationItem.createTodo.route) {
            ParentComposable()
        }*/

        /* composable(NavigationItem.IntroScreenOne.route) {
             IntroScreenOne(navHostController, pagerState)
         }*/

        composable(NavigationItem.IntroScreenTwo.route) {
            IntroScreenTwo(navHostController)
        }

        composable(NavigationItem.IntroPagerScreen.route) {
            IntroPager(navHostController)
        }

        composable(NavigationItem.Todo.route) {
            TodoPage(todoViewModel, navHostController, shouldOpenTodoPage, 0.dp)
        }

        composable(NavigationItem.Home2.route) {
            HomePage(viewModel, navHostController, onCardSelected = {}, notes = notes)
        }

        composable(NavigationItem.IntroScreenOne.route){
            IntroScreenOne(PagerState { 0 })
        }

        composable(NavigationItem.DrawPage.route) {
            DrawingApp(
                PaddingValues(8.dp), navHostController,
                pathClick = { drawing ->
                    bitmap = Utils().captureDrawingCompose(drawing).asAndroidBitmap()
                    val uri = bitmap?.let {
                        Utils().saveBitmapToUri(
                            context,
                            it,
                            "drawing${System.currentTimeMillis()}.png"
                        )
                    }
                    if (uri != null) {
                        viewModel.addImageUris(uri)
                    }
                }
            )
        }

    }

    // Use this snippet in a debug mode to inspect your back stack.



    if (!isIntroScreen) {
        navHostController.navigate(NavigationItem.IntroPagerScreen.route)
    } else {
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
    todoViewModel: TodoViewModel,
    notes: List<Notes>,
    shouldOpenTodoPage: Boolean
) {
    var selectedItemIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    var bottomBarHeight by remember { mutableStateOf(0.dp) }

    if (shouldOpenTodoPage) {
        selectedItemIndex = 1
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
            BottomNavigationBar(
                items,
                selectedItemIndex,
                onItemSelected = { selectedItemIndex = it },
                bottomBarHeight = {
                    bottomBarHeight = it
                }
            )
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
                    fadeIn(animationSpec = tween(100)) togetherWith fadeOut(
                        animationSpec = tween(
                            300
                        )
                    )
                }, label = ""
            ) { targetIndex ->
                when (targetIndex) {
                    0 -> HomePage(viewModel, navHostController, onCardSelected = {}, notes)
                    1 -> TodoPage(todoViewModel, navHostController, shouldOpenTodoPage,bottomBarHeight)
                }
            }

        }
    }
}

@Composable
fun ComposableLifecycle(
    lifeCycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onEvent: (LifecycleOwner, Lifecycle.Event) -> Unit
) {
    DisposableEffect(lifeCycleOwner) {
        val observer = LifecycleEventObserver { source, event ->
            onEvent(source, event)
        }
        lifeCycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
