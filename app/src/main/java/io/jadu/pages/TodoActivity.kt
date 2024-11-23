package io.jadu.pages

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.jadu.pages.presentation.screens.ParentComposable
import io.jadu.pages.presentation.viewmodel.TodoViewModel
import io.jadu.pages.ui.theme.PagesTheme

@AndroidEntryPoint
class TodoActivity:ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            val todoViewModel: TodoViewModel = hiltViewModel()
            PagesTheme {
                ParentComposable(todoViewModel)
            }
        }
    }
}