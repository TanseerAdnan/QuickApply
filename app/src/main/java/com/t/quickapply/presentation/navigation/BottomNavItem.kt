package com.t.quickapply.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.Description

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home", "Home", Icons.Default.Home)

    object CV : BottomNavItem(
        "cv",
        "CV",
        Icons.Default.Description
    )

    object Applications : BottomNavItem(
        "applications",
        "Applications",
        Icons.Default.List
    )

    object Profile : BottomNavItem(
        "profile",
        "Profile",
        Icons.Default.Person
    )
}