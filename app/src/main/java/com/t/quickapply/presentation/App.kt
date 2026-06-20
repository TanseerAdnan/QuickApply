package com.t.quickapply.presentation

import androidx.compose.runtime.Composable
import com.t.quickapply.presentation.navigation.AppNavGraph
import com.t.quickapply.presentation.theme.ThemeViewModel

@Composable
fun App(themeViewModel: ThemeViewModel) {
    AppNavGraph(themeViewModel = themeViewModel)
}