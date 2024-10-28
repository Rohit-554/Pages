package io.jadu.pages.domain.model

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationItem (
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val isTodo:Boolean,
    val badgeCount: Int? = null
)