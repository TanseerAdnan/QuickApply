package com.t.quickapply.presentation.navigation

object Routes {
    const val SPLASH = "splash"
    const val AUTH = "auth"
    const val HOME = "home"
    const val APPLICATIONS = "applications"
    const val PROFILE = "profile"
    const val CV = "cv"

    const val DRAFT_EDITOR = "draft_editor/{draftId}"

    fun draftEditor(draftId: String) = "draft_editor/$draftId"

}