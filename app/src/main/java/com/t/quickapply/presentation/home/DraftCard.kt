package com.t.quickapply.presentation.home

import com.t.quickapply.data.model.Draft

data class DraftCard(
    val id: String,
    val title: String,
    val subtitle: String,
    val isSent: Boolean = false
)

const val MAX_DRAFTS = 4

fun Draft.toDraftCard(): DraftCard {
    return DraftCard(
        id = this.id,
        title = this.title.ifBlank { "Untitled Draft" },
        subtitle = "Last edited ${formatTimestamp(this.updatedAt)}",
        isSent = this.isSent
    )
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60_000 -> "just now"
        diff < 3_600_000 -> "${diff / 60_000}m ago"
        diff < 86_400_000 -> "${diff / 3_600_000}h ago"
        else -> "${diff / 86_400_000}d ago"
    }
}