package io.jadu.pages.presentation.screens.introScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import io.jadu.pages.R
import io.jadu.pages.presentation.navigation.NavigationItem

@Composable
fun IntroScreenTwo(navHostController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.todos))
        LottieAnimation(
            composition,
            iterations = LottieConstants.IterateForever
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Manages Your Notes & Todo Lists",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Keep track of your thoughts, ideas, and important tasks in a simple and intuitive way.",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(100.dp))
        OutlinedButton(
            onClick = {
                navHostController.navigate(NavigationItem.Home.route) {
                    popUpTo(0) {
                        inclusive = true                    }
                    launchSingleTop = true
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth().height(48.dp).padding(horizontal = 16.dp)
            ,
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.elevatedButtonColors(
            )
        ) {
            Text(
                text = "Get Started",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview
@Composable
fun PreviewIntroScreenTwo(){
    val context = LocalContext.current
    IntroScreenTwo(navHostController = NavHostController(context))
}