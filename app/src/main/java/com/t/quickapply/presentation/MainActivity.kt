package com.t.quickapply.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import com.t.quickapply.data.remote.config.RemoteConfigProvider
import com.t.quickapply.presentation.theme.QuickApplyTheme
import com.t.quickapply.presentation.theme.ThemeViewModel
import androidx.activity.enableEdgeToEdge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        CoroutineScope(Dispatchers.IO).launch {
            RemoteConfigProvider.init()
        }

        setContent {
            val isDark by themeViewModel.isDarkTheme.collectAsState()

            QuickApplyTheme(darkTheme = isDark) {
                App(themeViewModel = themeViewModel)
            }
        }
    }
}