package io.jadu.pages.presentation.home_widget


import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Icon
import android.os.Build
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.IconImageProvider
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.NoRippleOverride
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.background
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.size
import androidx.glance.layout.wrapContentWidth
import androidx.glance.material3.ColorProviders
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.text.FontFamily
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import io.jadu.pages.R
import io.jadu.pages.domain.model.TodoModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object TodoWidget : GlanceAppWidget() {

    @OptIn(ExperimentalMaterial3Api::class)
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val activityId = ActionParameters.Key<String>("activityId")
        val myCustomFontFamily = FontFamily(
            family = "sans-serif"
        )
        provideContent {
            val fetchedTodos: List<TodoModel> = currentState<List<TodoModel>>()
            val todos = fetchedTodos.filter { !it.isTaskCompleted }.sortedByDescending { it.date }
            var textColor by remember { mutableStateOf(ColorProvider(Color(0xFF000000))) }
            val isDarkTheme = isSystemInDarkMode(context)
            textColor = getColorForTheme(isDarkTheme, context)

            GlanceTheme() {
                Scaffold(
                    backgroundColor = GlanceTheme.colors.widgetBackground,
                    modifier = GlanceModifier.fillMaxSize(),
                ) {
                    if (todos.isEmpty()) {
                        Column(
                            modifier = GlanceModifier
                                .padding(horizontal = 8.dp)
                                .fillMaxSize()

                        ) {
                            Row(
                                modifier = GlanceModifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "",
                                    style = TextStyle(
                                        color = textColor,
                                        fontSize = 12.sp,
                                        fontFamily = myCustomFontFamily,
                                    ),
                                    maxLines = 1,
                                    modifier = GlanceModifier
                                )
                                Box(
                                    modifier = GlanceModifier.fillMaxWidth(),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    AddTodo(activityId, myCustomFontFamily)
                                }
                            }

                            Box(
                                modifier = GlanceModifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = GlanceModifier
                                            .size(80.dp)
                                            .clickable(
                                                rippleOverride = R.drawable.no_ripple,
                                                onClick =
                                                actionRunCallback<UpdateTodoAction>(
                                                    actionParametersOf(activityId to "addTodo")
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ){
                                        Image(
                                            provider = androidx.glance.ImageProvider(R.drawable.baseline_sticky_note_2_24),
                                            modifier = GlanceModifier
                                                .size(76.dp)
                                                .padding(bottom = 8.dp),
                                            contentDescription = "Add new task",
                                            colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurface)
                                        )
                                    }
                                    Text(
                                        text = "No TO-DOs",
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            fontFamily = myCustomFontFamily,
                                            color = GlanceTheme.colors.onSurface
                                        ),
                                        modifier = GlanceModifier.padding(bottom = 8.dp)
                                    )

                                }
                            }
                        }

                        return@Scaffold
                    }

                    Column(
                        modifier = GlanceModifier
                            .padding(horizontal = 4.dp, vertical = 4.dp)
                            .fillMaxSize()

                    ) {
                        Row(
                            modifier = GlanceModifier.fillMaxWidth().padding(top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "${todos.size} " + if (todos.size == 1) "TO-DO" else "TO-DOs",
                                style = TextStyle(
                                    color = MyAppWidgetGlanceColorScheme.colors.onSurface,
                                    fontSize = 12.sp,
                                    fontFamily = myCustomFontFamily,
                                ),
                                maxLines = 1,
                                modifier = GlanceModifier
                            )
                            Box(
                                modifier = GlanceModifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                AddTodo(activityId, myCustomFontFamily)
                            }
                        }

                        Spacer(modifier = GlanceModifier.height(8.dp))

                        LazyColumn(
                            modifier = GlanceModifier.fillMaxWidth().padding(bottom = 8.dp),
                        ) {
                            items(todos) { todo ->
                                Column {
                                    Row(
                                        modifier = GlanceModifier
                                            .padding(bottom = 6.dp, top = 6.dp)
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        val todoIdKey = ActionParameters.Key<Long>("todoId")
                                        Image(
                                            provider = if (todo.isTaskCompleted) {
                                                androidx.glance.ImageProvider(R.drawable.baseline_check_circle_24)
                                            } else {
                                                androidx.glance.ImageProvider(R.drawable.baseline_radio_button_unchecked_24)
                                            },
                                            contentDescription = if (todo.isTaskCompleted) "Checked" else "Unchecked",
                                            modifier = GlanceModifier
                                                .size(24.dp)
                                                .padding(end = 8.dp)
                                                .clickable(
                                                    rippleOverride = R.drawable.no_ripple,
                                                    onClick = actionRunCallback<UpdateTodoAction>(
                                                        actionParametersOf(todoIdKey to todo.id)
                                                    )
                                                )
                                        )
                                        Text(
                                            text = todo.task ?: "",
                                            style = TextStyle(
                                                color = MyAppWidgetGlanceColorScheme.colors.onSurface,
                                                fontSize = 12.sp,
                                                fontFamily = myCustomFontFamily
                                            )
                                        )
                                    }
                                    Box(
                                        modifier = GlanceModifier
                                            .fillMaxWidth()
                                            .height(1.dp)
                                            .padding(top = 4.dp, bottom = 4.dp)
                                            .background(
                                                color = Color(0xFFE0E0E0).copy(alpha = 0.6f),
                                            )
                                    ) {}
                                }

                            }

                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun AddTodo(
        activityId: ActionParameters.Key<String>,
        myCustomFontFamily: FontFamily
    ) {
        Box(
            modifier = GlanceModifier
                .size(30.dp)
                .clickable(
                    rippleOverride = R.drawable.no_ripple,
                    onClick =
                    actionRunCallback<UpdateTodoAction>(
                        actionParametersOf(activityId to "addTodo")
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                provider = androidx.glance.ImageProvider(R.drawable.baseline_add_24),
                contentDescription = "Unchecked",
                modifier = GlanceModifier
                    .size(24.dp),
                colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurface)
            )
           /* Text(
                text = "+",
                style = TextStyle(
                    color = MyAppWidgetGlanceColorScheme.colors.onSurface,
                    fontSize = 24.sp,
                    fontFamily = myCustomFontFamily,
                ),
                maxLines = 1
            )*/
        }
    }

    override val stateDefinition: GlanceStateDefinition<List<TodoModel>>
        get() = object : GlanceStateDefinition<List<TodoModel>> {
            override suspend fun getDataStore(
                context: Context,
                fileKey: String
            ): DataStore<List<TodoModel>> {
                return TodoDataStore(context)
            }

            override fun getLocation(context: Context, fileKey: String): File {
                throw NotImplementedError("Not implemented for Todo App Widget State Definition")
            }
        }

    suspend fun update(context: Context) {
        GlanceAppWidgetManager(context).getGlanceIds(TodoWidget::class.java).forEach { glanceId ->
            provideGlance(context, glanceId)
            withContext(Dispatchers.Main) {
                //updateAll(context)
            }
        }
    }

    suspend fun updateTodos(context: Context) {
        updateAll(context)
    }
}


object MyAppWidgetGlanceColorScheme {

    private val LightColors = ColorScheme(
        primary = Color(0xFF6200EE),    // Purple primary color
        onPrimary = Color(0xFFFFFFFF),  // White text on primary
        primaryContainer = Color(0xFFBB86FC),
        onPrimaryContainer = Color(0xFF6200EE),
        inversePrimary = Color(0xFF3700B3),
        secondary = Color(0xFF03DAC5),  // Teal secondary color
        onSecondary = Color(0xFF000000),// Black text on secondary
        secondaryContainer = Color(0xFF018786),
        onSecondaryContainer = Color(0xFF03DAC5),
        tertiary = Color(0xFF018786),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFF03DAC5),
        onTertiaryContainer = Color(0xFF018786),
        background = Color(0xFFFFFFFF), // White background
        onBackground = Color(0xFF000000),// Black text on background
        surface = Color(0xFFF8F8F8),    // Light grey surface
        onSurface = Color(0xFF000000),  // Black text on surface
        surfaceVariant = Color(0xFFE0E0E0),
        onSurfaceVariant = Color(0xFF000000),
        surfaceTint = Color(0xFF6200EE),
        inverseSurface = Color(0xFF121212),
        inverseOnSurface = Color(0xFFFFFFFF),
        error = Color(0xFFB00020),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFCF6679),
        onErrorContainer = Color(0xFFB00020),
        outline = Color(0xFF000000),
        outlineVariant = Color(0xFF6200EE),
        scrim = Color(0xFF000000),
        surfaceBright = Color(0xFFFFFFFF),
        surfaceDim = Color(0xFFE0E0E0),
        surfaceContainer = Color(0xFFF8F8F8),
        surfaceContainerHigh = Color(0xFFE0E0E0),
        surfaceContainerHighest = Color(0xFFB0B0B0),
        surfaceContainerLow = Color(0xFFFFFFFF),
        surfaceContainerLowest = Color(0xFFFFFFFF),
    )

    // Define the color scheme for Dark Mode
    private val DarkColors = ColorScheme(
        primary = Color(0xFFBB86FC),    // Lighter purple for primary
        onPrimary = Color(0xFF000000),  // Black text on primary
        primaryContainer = Color(0xFF3700B3),
        onPrimaryContainer = Color(0xFFBB86FC),
        inversePrimary = Color(0xFF6200EE),
        secondary = Color(0xFF03DAC5),  // Teal for secondary
        onSecondary = Color(0xFF000000),// Black text on secondary
        secondaryContainer = Color(0xFF018786),
        onSecondaryContainer = Color(0xFF03DAC5),
        tertiary = Color(0xFF018786),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFF03DAC5),
        onTertiaryContainer = Color(0xFF018786),
        background = Color(0xFF121212), // Dark background
        onBackground = Color(0xFFFFFFFF),// White text on background
        surface = Color(0xFF1E1E1E),    // Darker surface
        onSurface = Color(0xFFFFFFFF),  // White text on surface
        surfaceVariant = Color(0xFF2C2C2C),
        onSurfaceVariant = Color(0xFFFFFFFF),
        surfaceTint = Color(0xFFBB86FC),
        inverseSurface = Color(0xFFFFFFFF),
        inverseOnSurface = Color(0xFF000000),
        error = Color(0xFFCF6679),
        onError = Color(0xFF000000),
        errorContainer = Color(0xFFB00020),
        onErrorContainer = Color(0xFFCF6679),
        outline = Color(0xFFBB86FC),
        outlineVariant = Color(0xFFBB86FC),
        scrim = Color(0xFF000000),
        surfaceBright = Color(0xFF121212),
        surfaceDim = Color(0xFF2C2C2C),
        surfaceContainer = Color(0xFF1E1E1E),
        surfaceContainerHigh = Color(0xFF2C2C2C),
        surfaceContainerHighest = Color(0xFF3A3A3A),
        surfaceContainerLow = Color(0xFF121212),
        surfaceContainerLowest = Color(0xFF121212),
    )


    val colors = ColorProviders(
        light = LightColors,
        dark = DarkColors
    )
}


fun getColorForTheme(isDarkTheme: Boolean, context: Context): ColorProvider {
    return if (isDarkTheme) {

        ColorProvider(Color(0xFFFFFFFF))
    } else {

        ColorProvider(Color(0xFF000000))
    }
}

fun isSystemInDarkMode(context: Context): Boolean {
    val nightModeFlags = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

    Log.d("nightModeFlags", "nightModeFlags: $nightModeFlags")
    return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
}

class TodoWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = TodoWidget
}


