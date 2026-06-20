package com.t.quickapply.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.t.quickapply.data.remote.firebase.DraftRepositoryImpl
import com.t.quickapply.data.remote.firebase.GoogleAuthClient
import com.t.quickapply.data.repository.AuthRepositoryImpl
import com.t.quickapply.domain.usecase.AddDraftUseCase
import com.t.quickapply.domain.usecase.AuthUseCase
import com.t.quickapply.domain.usecase.DeleteDraftUseCase
import com.t.quickapply.domain.usecase.GetDraftsUseCase
import com.t.quickapply.domain.usecase.UpdateDraftUseCase
import com.t.quickapply.presentation.auth.AuthScreen
import com.t.quickapply.presentation.draft.DraftEditorScreen
import com.t.quickapply.presentation.draft.DraftEditorViewModel
import com.t.quickapply.presentation.draft.DraftEditorViewModelFactory
import com.t.quickapply.presentation.profile.ProfileViewModel
import com.t.quickapply.presentation.profile.ProfileViewModelFactory
import com.t.quickapply.presentation.splash.SplashScreen
import com.t.quickapply.presentation.theme.ThemeViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.t.quickapply.data.remote.ai.AiRepository
import com.t.quickapply.data.repository.ApplicationsRepositoryImpl
import com.t.quickapply.domain.usecase.AddSentApplicationUseCase
import com.t.quickapply.domain.usecase.GetSentApplicationsUseCase

@Composable
fun AppNavGraph(themeViewModel: ThemeViewModel) {

    val navController = rememberNavController()
    val context = LocalContext.current
    val googleAuth = remember { GoogleAuthClient(context) }

    val authRepository = remember { AuthRepositoryImpl(googleAuth) }
    val authUseCase = remember { AuthUseCase(authRepository) }

    val draftRepository = remember { DraftRepositoryImpl() }

    val getDraftsUseCase = remember {
        GetDraftsUseCase(draftRepository)
    }

    val addDraftUseCase = remember {
        AddDraftUseCase(draftRepository)
    }

    val updateDraftUseCase = remember {
        UpdateDraftUseCase(draftRepository)
    }

    val deleteDraftUseCase = remember {
        DeleteDraftUseCase(draftRepository)
    }

    val applicationsRepository = remember {
        ApplicationsRepositoryImpl(
            FirebaseFirestore.getInstance()
        )
    }

    val addSentApplicationUseCase = remember {
        AddSentApplicationUseCase(
            applicationsRepository
        )
    }

    val getApplicationsUseCase = remember {
        GetSentApplicationsUseCase(applicationsRepository)
    }

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigate = {
                    if (googleAuth.isUserLoggedIn()) {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.SPLASH) {
                                inclusive = true
                            }
                        }
                    } else {
                        navController.navigate(Routes.AUTH) {
                            popUpTo(Routes.SPLASH) {
                                inclusive = true
                            }
                        }
                    }
                }
            )
        }

        composable(Routes.AUTH) {
            AuthScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.AUTH) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Routes.HOME) {

            val profileViewModel = viewModel<ProfileViewModel>(
                factory = ProfileViewModelFactory(authUseCase)
            )

            MainScreen(
                themeViewModel = themeViewModel,
                profileViewModel = profileViewModel,
                getDraftsUseCase = getDraftsUseCase,
                addDraftUseCase = addDraftUseCase,
                deleteDraftUseCase = deleteDraftUseCase,
                updateDraftUseCase = updateDraftUseCase,
                addSentApplicationUseCase = addSentApplicationUseCase,
                getApplicationsUseCase = getApplicationsUseCase,
                onSignedOut = {
                    navController.navigate(Routes.AUTH) {
                        popUpTo(Routes.HOME) {
                            inclusive = true
                        }
                    }
                },
                onOpenDraftEditor = { draftId ->
                    navController.navigate(
                        Routes.draftEditor(draftId)
                    )
                }
            )
        }

        composable(Routes.DRAFT_EDITOR) { backStackEntry ->

            val draftId =
                backStackEntry.arguments?.getString("draftId") ?: ""

            val editorViewModel = viewModel<DraftEditorViewModel>(
                factory = DraftEditorViewModelFactory(
                    addDraftUseCase,
                    updateDraftUseCase,
                    getDraftsUseCase,
                    aiRepository = AiRepository()
                )
            )

            DraftEditorScreen(
                draftId = draftId,
                onBack = {
                    navController.popBackStack()
                },
                viewModel = editorViewModel
            )
        }
    }
}