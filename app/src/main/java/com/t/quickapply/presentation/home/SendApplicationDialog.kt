package com.t.quickapply.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t.quickapply.data.local.CvEntry

@Composable
fun SendApplicationDialog(
    state: SendApplicationDialogState,
    onHrNameChange: (String) -> Unit,
    onHrCompanyChange: (String) -> Unit,
    onHrEmailChange: (String) -> Unit,
    onAttachCvToggle: (Boolean) -> Unit,
    onCvSelected: (String) -> Unit,
    onUseEnhancedVersionChange: (Boolean) -> Unit,  // ← new
    onDismiss: () -> Unit,
    onSend: () -> Unit
) {
    if (!state.isVisible) return

    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(
                text = "Send Application",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())  // ← add this
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // Error banner
                if (state.sendError != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            text = state.sendError,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                // HR Name
                OutlinedTextField(
                    value = state.hrName,
                    onValueChange = onHrNameChange,
                    label = { Text("HR Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.hrNameError != null,
                    enabled = !state.isSending,
                    shape = RoundedCornerShape(10.dp)
                )
                state.hrNameError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall)
                }

                // Company
                OutlinedTextField(
                    value = state.hrCompany,
                    onValueChange = onHrCompanyChange,
                    label = { Text("Company") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.hrCompanyError != null,
                    enabled = !state.isSending,
                    shape = RoundedCornerShape(10.dp)
                )
                state.hrCompanyError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall)
                }

                // HR Email
                OutlinedTextField(
                    value = state.hrEmail,
                    onValueChange = onHrEmailChange,
                    label = { Text("HR Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.hrEmailError != null,
                    enabled = !state.isSending,
                    shape = RoundedCornerShape(10.dp)
                )
                state.hrEmailError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall)
                }

                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                // Attach CV toggle row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Attach resume",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Include a CV with this email",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = state.attachCv,
                        onCheckedChange = onAttachCvToggle,
                        enabled = !state.isSending,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }

                // CV error (no CVs saved)
                if (state.cvError != null) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            text = state.cvError,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                // CV picker — only shown when toggle is on and CVs exist
                if (state.attachCv && state.savedCvList.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Select resume",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        state.savedCvList.forEach { cv ->
                            CvSelectItem(
                                cv = cv,
                                isSelected = cv.id == state.selectedCvId,
                                enabled = !state.isSending,
                                onClick = { onCvSelected(cv.id) }
                            )
                        }

                        // Version toggle — only shown when selected CV has an enhanced version
                        val selectedCv = state.savedCvList.find { it.id == state.selectedCvId }
                        if (selectedCv?.enhancedFilePath != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.06f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "Resume version to send",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Original
                                        FilterChip(
                                            selected = !state.useEnhancedVersion,
                                            onClick = { onUseEnhancedVersionChange(false) },
                                            enabled = !state.isSending,
                                            label = { Text("Original", fontSize = 11.sp) },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Rounded.Description,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        )
                                        // Enhanced
                                        FilterChip(
                                            selected = state.useEnhancedVersion,
                                            onClick = { onUseEnhancedVersionChange(true) },
                                            enabled = !state.isSending,
                                            label = { Text("Enhanced", fontSize = 11.sp) },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Rounded.AutoAwesome,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSend,
                enabled = !state.isSending
            ) {
                if (state.isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sending...")
                } else {
                    Text("Send")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !state.isSending) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun CvSelectItem(
    cv: CvEntry,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.outlineVariant

    val bgColor = if (isSelected)
        MaterialTheme.colorScheme.primary.copy(alpha = 0.07f)
    else
        MaterialTheme.colorScheme.surface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(
                width = if (isSelected) 1.5.dp else 0.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Description,
            contentDescription = null,
            tint = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = cv.fileName,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        if (isSelected) {
            RadioButton(
                selected = true,
                onClick = null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}