package com.t.quickapply.presentation.home

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.t.quickapply.data.local.CvRepository
import com.t.quickapply.domain.usecase.AddDraftUseCase
import com.t.quickapply.domain.usecase.DeleteDraftUseCase
import com.t.quickapply.domain.usecase.GetDraftsUseCase
import com.t.quickapply.domain.usecase.UpdateDraftUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import com.t.quickapply.data.remote.gmail.GmailSender
import com.t.quickapply.domain.usecase.AddSentApplicationUseCase
import com.t.quickapply.domain.model.SentApplication

class HomeViewModel(
    private val getDraftsUseCase: GetDraftsUseCase,
    private val addDraftUseCase: AddDraftUseCase,
    private val deleteDraftUseCase: DeleteDraftUseCase,
    private val updateDraftUseCase: UpdateDraftUseCase,
    private val addSentApplicationUseCase: AddSentApplicationUseCase
) : ViewModel() {

    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDrafts()
    }

    private fun loadDrafts() {
        viewModelScope.launch {
            getDraftsUseCase(userId).collect { drafts ->
                _uiState.update {
                    it.copy(
                        drafts = drafts.map { d -> d.toDraftCard() },
                        isLoading = false
                    )
                }
            }
        }
    }

    fun getNewDraftId(): String = UUID.randomUUID().toString()

    fun deleteDraft(draftId: String) {
        viewModelScope.launch {
            deleteDraftUseCase(userId, draftId)
        }
    }

    fun openSendDialog(draftId: String, context: Context) {
        viewModelScope.launch {
            val cvList = CvRepository(context).cvList.first()
            _uiState.update {
                it.copy(
                    sendDialogState = SendApplicationDialogState(
                        isVisible = true,
                        draftId = draftId,
                        savedCvList = cvList
                    )
                )
            }
        }
    }
    fun updateAttachCv(value: Boolean) {
        val state = _uiState.value.sendDialogState
        val error = if (value && state.savedCvList.isEmpty())
            "No resume saved. Go to the CV tab to upload one."
        else null
        _uiState.update {
            it.copy(
                sendDialogState = state.copy(
                    attachCv = value,
                    cvError = error,
                    // auto-select first CV if toggling on and none selected
                    selectedCvId = if (value && state.selectedCvId == null)
                        state.savedCvList.firstOrNull()?.id
                    else state.selectedCvId
                )
            )
        }
    }

    fun updateSelectedCv(cvId: String) {
        _uiState.update {
            it.copy(
                sendDialogState = it.sendDialogState.copy(
                    selectedCvId = cvId,
                    cvError = null
                )
            )
        }
    }
    fun closeSendDialog() {
        _uiState.update { it.copy(sendDialogState = SendApplicationDialogState()) }
    }

    fun updateHrName(value: String) {
        _uiState.update {
            it.copy(sendDialogState = it.sendDialogState.copy(hrName = value, hrNameError = null))
        }
    }

    fun updateHrCompany(value: String) {
        _uiState.update {
            it.copy(sendDialogState = it.sendDialogState.copy(hrCompany = value, hrCompanyError = null))
        }
    }

    fun updateHrEmail(value: String) {
        _uiState.update {
            it.copy(sendDialogState = it.sendDialogState.copy(hrEmail = value, hrEmailError = null))
        }
    }

    fun sendApplication(context: Context) {
        val dialogState = _uiState.value.sendDialogState

        var hrNameError: String? = null
        var hrCompanyError: String? = null
        var hrEmailError: String? = null

        if (dialogState.hrName.isBlank()) hrNameError = "HR name is required"
        if (dialogState.hrCompany.isBlank()) hrCompanyError = "Company name is required"
        if (dialogState.hrEmail.isBlank()) hrEmailError = "Email is required"
        else if (!Patterns.EMAIL_ADDRESS.matcher(dialogState.hrEmail).matches()) {
            hrEmailError = "Enter a valid email address"
        }

        // Block send if CV toggle is on but none selected
        val cvError = if (dialogState.attachCv && dialogState.selectedCvId == null)
            "No resume saved. Go to the CV tab to upload one."
        else null

        if (hrNameError != null || hrCompanyError != null || hrEmailError != null || cvError != null) {
            _uiState.update {
                it.copy(
                    sendDialogState = dialogState.copy(
                        hrNameError = hrNameError,
                        hrCompanyError = hrCompanyError,
                        hrEmailError = hrEmailError,
                        cvError = cvError
                    )
                )
            }
            return
        }

        _uiState.update {
            it.copy(sendDialogState = dialogState.copy(isSending = true))
        }

        viewModelScope.launch {
            val drafts = getDraftsUseCase(userId).first()
            val draft = drafts.find { it.id == dialogState.draftId } ?: return@launch

            val emailBody = buildString {
                append(
                    draft.body
                        .replace("[HR Name]", dialogState.hrName)
                        .replace("[Company Name]", dialogState.hrCompany)
                )
            }

            val selectedCv = if (dialogState.attachCv)
                dialogState.savedCvList.find { it.id == dialogState.selectedCvId }
            else null

            // Pick the right version
            val cvUri = if (selectedCv != null) {
                if (dialogState.useEnhancedVersion && selectedCv.enhancedFilePath != null)
                    "file://${selectedCv.enhancedFilePath}"
                else
                    selectedCv.uriString
            } else null

            val gmailSender = GmailSender(context)

            val result = gmailSender.sendEmail(
                toEmail = dialogState.hrEmail,
                subject = draft.title,
                body = emailBody,
                cvFileUri = cvUri,           // ← was selectedCv?.uriString
                cvFileName = selectedCv?.fileName
            )

            result.fold(
                onSuccess = {
                    addSentApplicationUseCase(
                        SentApplication(
                            id = UUID.randomUUID().toString(),
                            userId = userId,
                            hrName = dialogState.hrName,
                            companyName = dialogState.hrCompany,
                            hrEmail = dialogState.hrEmail,
                            subject = draft.title
                        )
                    )
                    updateDraftUseCase(draft.copy(isSent = true))
                    _uiState.update {
                        it.copy(
                            sendDialogState = SendApplicationDialogState(),
                            showEmailSentDialog = true
                        )
                    }
                },
                onFailure = { e ->
                    val errorMessage = e.message.orEmpty()
                    val friendlyMessage = when {
                        errorMessage.contains("No internet connection", true) ||
                                errorMessage.contains("Unable to resolve host", true) ||
                                errorMessage.contains("hostname", true) -> {
                            "No internet connection. Please check your network and try again."
                        }
                        errorMessage.contains("Permission Denial", true) ||
                                errorMessage.contains("opening provider", true) ||
                                errorMessage.contains("ACTION_OPEN_DOCUMENT", true) ||
                                errorMessage.contains("SecurityException", true) -> {
                            "CV permission expired. Please re-upload it from the CV tab."
                        }
                        else -> errorMessage.ifBlank { "Failed to send email" }
                    }
                    _uiState.update {
                        it.copy(
                            sendDialogState = dialogState.copy(
                                isSending = false,
                                sendError = friendlyMessage
                            )
                        )
                    }
                }
            )
        }
    }

    fun dismissEmailSentDialog() {
        _uiState.update { it.copy(showEmailSentDialog = false) }
    }

    fun updateUseEnhancedVersion(value: Boolean) {
        _uiState.update {
            it.copy(sendDialogState = it.sendDialogState.copy(useEnhancedVersion = value))
        }
    }
}

class HomeViewModelFactory(

    private val getDraftsUseCase: GetDraftsUseCase,

    private val addDraftUseCase: AddDraftUseCase,

    private val deleteDraftUseCase: DeleteDraftUseCase,

    private val updateDraftUseCase: UpdateDraftUseCase,

    private val addSentApplicationUseCase: AddSentApplicationUseCase

) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        @Suppress("UNCHECKED_CAST")

        return HomeViewModel(

            getDraftsUseCase = getDraftsUseCase,

            addDraftUseCase = addDraftUseCase,

            deleteDraftUseCase = deleteDraftUseCase,

            updateDraftUseCase = updateDraftUseCase,

            addSentApplicationUseCase = addSentApplicationUseCase

        ) as T
    }
}