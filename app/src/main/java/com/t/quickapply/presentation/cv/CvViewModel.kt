package com.t.quickapply.presentation.cv

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.t.quickapply.BuildConfig
import com.t.quickapply.data.local.CvEntry
import com.t.quickapply.data.local.CvRepository
import com.t.quickapply.data.remote.ai.AiRepository
import com.t.quickapply.data.extractor.ResumeTextExtractor
import com.t.quickapply.data.extractor.ResumeWriter
import com.t.quickapply.data.remote.config.RemoteConfigProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

const val MAX_CVS = 5

data class CvUiState(
    val cvList: List<CvEntry> = emptyList(),
    val isLoading: Boolean = true,
    val cvToDelete: CvEntry? = null,
    val isEnhancing: Boolean = false,
    val enhancedText: String? = null,
    val enhancingForCvId: String? = null,   // which CV is being enhanced
    val errorMessage: String? = null
)

class CvViewModel(
    private val repository: CvRepository,
    private val aiRepository: AiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CvUiState())
    val uiState: StateFlow<CvUiState> = _uiState.asStateFlow()

    // Temporarily hold the CV being enhanced so we can save later
    private var pendingEnhanceCv: CvEntry? = null

    init {
        viewModelScope.launch {
            repository.cvList.collect { list ->
                _uiState.update { it.copy(cvList = list, isLoading = false) }
            }
        }
    }

    fun onCvPicked(context: Context, uri: Uri, fileName: String) {
        if (_uiState.value.cvList.size >= MAX_CVS) return
        try {
            context.contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (_: Exception) {}

        viewModelScope.launch {
            repository.addCv(
                CvEntry(
                    id = UUID.randomUUID().toString(),
                    fileName = fileName,
                    uriString = uri.toString(),
                    addedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun requestDelete(cv: CvEntry) {
        _uiState.update { it.copy(cvToDelete = cv) }
    }

    fun confirmDelete() {
        val cv = _uiState.value.cvToDelete ?: return
        viewModelScope.launch {
            // Also delete the enhanced file from disk if it exists
            cv.enhancedFilePath?.let { path ->
                try { File(path).delete() } catch (_: Exception) {}
            }
            repository.deleteCv(cv.id)
            _uiState.update { it.copy(cvToDelete = null) }
        }
    }

    fun cancelDelete() {
        _uiState.update { it.copy(cvToDelete = null) }
    }

    fun openCv(context: Context, cv: CvEntry) {
        try {
            val uri = when {
                cv.showingEnhanced && cv.enhancedFilePath != null -> {
                    val file = File(cv.enhancedFilePath)
                    androidx.core.content.FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        file
                    )
                }
                else -> Uri.parse(cv.uriString)
            }

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Open with"))
        } catch (_: Exception) {}
    }

    fun enhanceCv(context: Context, cv: CvEntry) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isEnhancing = true, enhancingForCvId = cv.id, errorMessage = null) }
                pendingEnhanceCv = cv

                val uri = Uri.parse(cv.uriString)
                val resumeText = ResumeTextExtractor.extract(context, uri)

                if (resumeText.isBlank()) {
                    _uiState.update {
                        it.copy(
                            isEnhancing = false,
                            enhancingForCvId = null,
                            errorMessage = "Could not extract text from this CV."
                        )
                    }
                    return@launch
                }

                val enhancedText = aiRepository.enhanceResume(
                    apiKey = RemoteConfigProvider.getGroqApiKey(),
                    resumeText = resumeText
                )

                _uiState.update {
                    it.copy(
                        isEnhancing = false,
                        enhancingForCvId = null,
                        enhancedText = enhancedText
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isEnhancing = false,
                        enhancingForCvId = null,
                        errorMessage = "Enhancement failed: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Saves the enhanced text as a .txt file in internal storage,
     * then updates the CvEntry so we remember the path.
     * The original CV is kept untouched.
     */
    fun saveEnhancedResume(context: Context) {
        val cv = pendingEnhanceCv ?: return
        val text = _uiState.value.enhancedText ?: return

        viewModelScope.launch {
            try {
                val path = ResumeWriter.writeEnhancedPdf(
                    context = context,
                    cvId = cv.id,
                    text = text
                )

                // Original uriString is UNTOUCHED — only store the enhanced path
                val updated = cv.copy(
                    enhancedFilePath = path,
                    showingEnhanced = true
                )
                repository.updateCv(updated)

                pendingEnhanceCv = null
                _uiState.update { it.copy(enhancedText = null) }

            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Save failed: ${e.message}") }
            }
        }
    }

    fun toggleEnhanced(cv: CvEntry) {
        viewModelScope.launch {
            repository.updateCv(cv.copy(showingEnhanced = !cv.showingEnhanced))
        }
    }

    fun discardEnhancedResume() {
        pendingEnhanceCv = null
        _uiState.update { it.copy(enhancedText = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

class CvViewModelFactory(
    private val repository: CvRepository,
    private val aiRepository: AiRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return CvViewModel(repository, aiRepository) as T
    }
}