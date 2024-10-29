package io.jadu.pages.presentation.screens.introScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.jadu.pages.core.Constants
import io.jadu.pages.core.PreferencesManager


@Composable
fun IntroPager(navHostController: NavHostController) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }

    LaunchedEffect(true) {
        preferencesManager.putBoolean(Constants.IS_INTRO_SHOWN,true)
    }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = {2}
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> IntroScreenOne()
                1 -> IntroScreenTwo(navHostController)
            }
        }
    }
}

@Preview
@Composable
fun PreviewIntroPager(){
    val context = LocalContext.current
    IntroPager(navHostController = NavHostController(context))
}

