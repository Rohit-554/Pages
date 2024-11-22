package io.jadu.pages.presentation.navigation

    enum class Screen {
        Notes,
        Todo,
        EditNotes,
        CreateNotes,
        ProfilePage,
        AboutPage,
        IntroScreenOne,
        IntroScreenTwo,
        IntroPagerScreen,
        Home,
        CreateTodo,
        SettingsPage,
        DrawPage,
        createTodo
    }
    sealed class NavigationItem(val route: String) {
        object Home : NavigationItem(Screen.Notes.name)
        object Todo : NavigationItem(Screen.Todo.name)
        object EditNotes : NavigationItem(Screen.EditNotes.name)
        object CreateNotes : NavigationItem(Screen.CreateNotes.name)
        object ProfilePage : NavigationItem(Screen.ProfilePage.name)
        object AboutPage : NavigationItem(Screen.AboutPage.name)
        object IntroScreenOne : NavigationItem(Screen.IntroScreenOne.name)
        object IntroScreenTwo : NavigationItem(Screen.IntroScreenTwo.name)
        object IntroPagerScreen : NavigationItem(Screen.IntroPagerScreen.name)
        object CreateTodo : NavigationItem(Screen.CreateTodo.name)
        object Home2 : NavigationItem(Screen.Home.name)
        object SettingsPage : NavigationItem(Screen.SettingsPage.name)
        object DrawPage : NavigationItem(Screen.DrawPage.name)
        object createTodo : NavigationItem(Screen.createTodo.name)
    }
