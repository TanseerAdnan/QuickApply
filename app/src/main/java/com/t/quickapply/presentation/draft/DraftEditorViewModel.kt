package com.t.quickapply.presentation.draft


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.t.quickapply.data.model.Draft
import com.t.quickapply.domain.usecase.AddDraftUseCase
import com.t.quickapply.domain.usecase.GetDraftsUseCase
import com.t.quickapply.domain.usecase.UpdateDraftUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.t.quickapply.BuildConfig
import com.t.quickapply.data.remote.ai.AiRepository
import com.t.quickapply.data.remote.config.RemoteConfigProvider

data class DraftEditorUiState(
    val title: String = "",
    val body: String = "",
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val isEditMode: Boolean = false,
    val snackbarMessage: String? = null,
    val wordCount: Int = 0,
    val showMissingPlaceholderWarning: Boolean = false,
    val missingPlaceholders: List<String> = emptyList(),

    // AI enhance
    val isEnhancing: Boolean = false,
    val showEnhanceConfirmDialog: Boolean = false,
    val showEnhancedPreviewDialog: Boolean = false,
    val enhancedBody: String? = null
)

class DraftEditorViewModel(
    private val addDraftUseCase: AddDraftUseCase,
    private val updateDraftUseCase: UpdateDraftUseCase,
    private val getDraftsUseCase: GetDraftsUseCase,
    private val aiRepository: AiRepository
) : ViewModel() {

    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val _uiState = MutableStateFlow(DraftEditorUiState())
    val uiState: StateFlow<DraftEditorUiState> = _uiState.asStateFlow()

    private var draftId: String = ""
    private var isEditMode: Boolean = false

    fun init(draftId: String) {
        this.draftId = draftId
        loadExistingDraft(draftId)
    }

    private fun loadExistingDraft(id: String) {
        viewModelScope.launch {
            val drafts = getDraftsUseCase(userId).first()
            val existing = drafts.find { it.id == id }
            if (existing != null) {
                isEditMode = true
                _uiState.update {
                    it.copy(
                        isEditMode = true,
                        title = existing.title,
                        body = existing.body,
                        wordCount = countWords(existing.body)
                    )
                }
            } else {
                isEditMode = false
            }
        }
    }

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value, snackbarMessage = null) }
    }

    fun onBodyChange(value: String) {
        val words = countWords(value)
        if (words > 250) return
        _uiState.update { it.copy(body = value, wordCount = words, snackbarMessage = null) }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }


    fun requestSave() {
        val state = _uiState.value

        if (state.title.isBlank()) {
            _uiState.update { it.copy(snackbarMessage = "Subject cannot be empty") }
            return
        }
        if (state.body.isBlank()) {
            _uiState.update { it.copy(snackbarMessage = "Message body cannot be empty") }
            return
        }

        val missing = mutableListOf<String>()
        if (!state.body.contains("[HR Name]")) missing.add("[HR Name]")
        if (!state.body.contains("[Company Name]")) missing.add("[Company Name]")

        if (missing.isNotEmpty()) {
            _uiState.update {
                it.copy(
                    showMissingPlaceholderWarning = true,
                    missingPlaceholders = missing
                )
            }
            return
        }


        performSave()
    }

    fun dismissPlaceholderWarning() {
        _uiState.update {
            it.copy(showMissingPlaceholderWarning = false, missingPlaceholders = emptyList())
        }
    }

    private fun performSave() {
        val state = _uiState.value
        _uiState.update { it.copy(isSaving = true, snackbarMessage = null) }

        val draft = Draft(
            id = draftId,
            userId = userId,
            title = state.title,
            body = state.body,
            updatedAt = System.currentTimeMillis()
        )

        viewModelScope.launch {
            val result = if (isEditMode) updateDraftUseCase(draft)
            else addDraftUseCase(draft)

            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isSaving = false, isSaved = true) }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isSaving = false, snackbarMessage = e.message ?: "Failed to save")
                    }
                }
            )
        }
    }

    private fun countWords(text: String): Int {
        return text.trim().split("\\s+".toRegex()).count { it.isNotBlank() }
    }

    fun requestEnhance() {
        _uiState.update { it.copy(showEnhanceConfirmDialog = true) }
    }

    fun dismissEnhanceConfirm() {
        _uiState.update { it.copy(showEnhanceConfirmDialog = false) }
    }

    fun confirmEnhance() {
        _uiState.update { it.copy(showEnhanceConfirmDialog = false, isEnhancing = true) }
        viewModelScope.launch {
            try {
                val enhanced = aiRepository.enhanceDraft(
                    apiKey = RemoteConfigProvider.getGroqApiKey(),
                    draftText = _uiState.value.body
                )
                _uiState.update {
                    it.copy(
                        isEnhancing = false,
                        enhancedBody = enhanced,
                        showEnhancedPreviewDialog = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isEnhancing = false,
                        snackbarMessage = "Enhancement failed: ${e.message}"
                    )
                }
            }
        }
    }

    fun acceptEnhanced() {
        val enhanced = _uiState.value.enhancedBody ?: return
        _uiState.update {
            it.copy(
                body = enhanced,
                wordCount = countWords(enhanced),
                enhancedBody = null,
                showEnhancedPreviewDialog = false
            )
        }
    }

    fun discardEnhanced() {
        _uiState.update {
            it.copy(enhancedBody = null, showEnhancedPreviewDialog = false)
        }
    }
}

class DraftEditorViewModelFactory(
    private val addDraftUseCase: AddDraftUseCase,
    private val updateDraftUseCase: UpdateDraftUseCase,
    private val getDraftsUseCase: GetDraftsUseCase,
    private val aiRepository: AiRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return DraftEditorViewModel(addDraftUseCase, updateDraftUseCase, getDraftsUseCase, aiRepository
        ) as T
    }
}