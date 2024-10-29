package io.jadu.pages.presentation.navigation

    enum class Screen {
        Notes,
        Todo,
        EditNotes,
        CreateNotes,
        ProfilePage,
        AboutPage
    }
    sealed class NavigationItem(val route: String) {
        object Home : NavigationItem(Screen.Notes.name)
        object Todo : NavigationItem(Screen.Todo.name)
        object EditNotes : NavigationItem(Screen.EditNotes.name)
        object CreateNotes : NavigationItem(Screen.CreateNotes.name)
        object ProfilePage : NavigationItem(Screen.ProfilePage.name)
        object AboutPage : NavigationItem(Screen.AboutPage.name)
    }
