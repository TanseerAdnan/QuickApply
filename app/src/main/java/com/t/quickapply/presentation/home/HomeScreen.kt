package com.t.quickapply.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.t.quickapply.presentation.components.AppHeader

@Composable
fun HomeScreen(
    userName: String,
    viewModel: HomeViewModel = viewModel(),
    onOpenDraftEditor: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val drafts = uiState.drafts
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppHeader(userName = userName)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
                    .padding(bottom = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Applications",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Manage your job drafts",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "${drafts.size} / $MAX_DRAFTS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (drafts.size == MAX_DRAFTS)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                DraftCardGrid(
                    drafts = drafts,
                    onAddDraft = {
                        val newId = viewModel.getNewDraftId()
                        onOpenDraftEditor(newId)
                    },
                    onEdit = { draft ->
                        onOpenDraftEditor(draft.id)
                    },
                    onSend = { draft ->
                        viewModel.openSendDialog(draft.id, context)
                    },
                    onDelete = { draft ->
                        viewModel.deleteDraft(draft.id)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = when (drafts.size) {
                        0 -> "Tap + to create your first message"
                        MAX_DRAFTS -> "All slots used — delete one to add more"
                        else -> "${MAX_DRAFTS - drafts.size} slot${if (MAX_DRAFTS - drafts.size > 1) "s" else ""} remaining"
                    },
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    color = if (drafts.size == MAX_DRAFTS)
                        MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    // Send dialog
    SendApplicationDialog(
        state = uiState.sendDialogState,
        onHrNameChange = viewModel::updateHrName,
        onHrCompanyChange = viewModel::updateHrCompany,
        onHrEmailChange = viewModel::updateHrEmail,
        onAttachCvToggle = viewModel::updateAttachCv,
        onCvSelected = viewModel::updateSelectedCv,
        onUseEnhancedVersionChange = viewModel::updateUseEnhancedVersion,  // ← new
        onDismiss = { viewModel.closeSendDialog() },
        onSend = { viewModel.sendApplication(context) }
    )

    // Email sent success dialog
    if (uiState.showEmailSentDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissEmailSentDialog() },
            icon = {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Email sent!",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = "Your application has been sent. The draft has been marked as sent.",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissEmailSentDialog() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Done")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}