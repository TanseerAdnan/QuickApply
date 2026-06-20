package com.t.quickapply.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DraftCardGrid(
    drafts: List<DraftCard>,
    onAddDraft: () -> Unit,
    onEdit: (DraftCard) -> Unit,
    onSend: (DraftCard) -> Unit,
    onDelete: (DraftCard) -> Unit
) {
    val rows = (MAX_DRAFTS + 1) / 2

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (col in 0..1) {
                    val index = row * 2 + col
                    val draft = drafts.getOrNull(index)
                    val isNextEmpty = index == drafts.size

                    Box(modifier = Modifier.weight(1f)) {
                        when {
                            draft != null -> DraftCardItem(
                                draft = draft,
                                onEdit = onEdit,
                                onSend = onSend,
                                onDelete = onDelete
                            )
                            isNextEmpty -> AddDraftCard(onClick = onAddDraft)
                            else -> AddDraftCard(onClick = {}, dimmed = true)
                        }
                    }
                }
            }
        }
    }
}