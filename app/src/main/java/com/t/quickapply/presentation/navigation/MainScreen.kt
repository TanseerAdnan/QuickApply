package com.t.quickapply.presentation.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.t.quickapply.domain.usecase.AddDraftUseCase
import com.t.quickapply.domain.usecase.AddSentApplicationUseCase
import com.t.quickapply.domain.usecase.GetSentApplicationsUseCase
import com.t.quickapply.domain.usecase.DeleteDraftUseCase
import com.t.quickapply.domain.usecase.GetDraftsUseCase
import com.t.quickapply.domain.usecase.UpdateDraftUseCase
import com.t.quickapply.presentation.applications.ApplicationsScreen
import com.t.quickapply.presentation.applications.ApplicationsViewModel
import com.t.quickapply.presentation.applications.ApplicationsViewModelFactory
import com.t.quickapply.presentation.components.BottomNavBar
import com.t.quickapply.presentation.cv.CvScreen
import com.t.quickapply.presentation.home.HomeScreen
import com.t.quickapply.presentation.home.HomeViewModel
import com.t.quickapply.presentation.home.HomeViewModelFactory
import com.t.quickapply.presentation.profile.ProfileScreen
import com.t.quickapply.presentation.profile.ProfileViewModel
import com.t.quickapply.presentation.theme.ThemeViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MainScreen(
    themeViewModel: ThemeViewModel,
    profileViewModel: ProfileViewModel,
    getDraftsUseCase: GetDraftsUseCase,
    addDraftUseCase: AddDraftUseCase,
    deleteDraftUseCase: DeleteDraftUseCase,
    updateDraftUseCase: UpdateDraftUseCase,
    addSentApplicationUseCase: AddSentApplicationUseCase,
    getApplicationsUseCase: GetSentApplicationsUseCase,
    onSignedOut: () -> Unit,
    onOpenDraftEditor: (String) -> Unit
) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute ?: "home",
                onItemClick = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(BottomNavItem.Home.route) {
                val homeViewModel = viewModel<HomeViewModel>(
                    factory = HomeViewModelFactory(getDraftsUseCase, addDraftUseCase, deleteDraftUseCase, updateDraftUseCase,  addSentApplicationUseCase
                    )
                )
                HomeScreen(
                    userName = profileViewModel.uiState.value.userName,
                    viewModel = homeViewModel,
                    onOpenDraftEditor = onOpenDraftEditor
                )
            }
            composable(BottomNavItem.CV.route) {
                CvScreen()
            }
            composable(BottomNavItem.Applications.route) {

                val applicationsViewModel = viewModel<ApplicationsViewModel>(
                    factory = ApplicationsViewModelFactory(
                        getApplicationsUseCase
                    )
                )

                ApplicationsScreen(
                    viewModel = applicationsViewModel
                )
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    themeViewModel = themeViewModel,
                    profileViewModel = profileViewModel,
                    onSignedOut = onSignedOut
                )
            }
        }
    }
}