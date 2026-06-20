package com.t.quickapply.presentation.applications

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ApplicationsScreen(
    viewModel: ApplicationsViewModel = viewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    if (uiState.applications.isEmpty()) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "No applications sent yet",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

    } else {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // Header Section
            Column(
                modifier = Modifier.fillMaxWidth().padding(
                        horizontal = 18.dp,
                        vertical = 16.dp
                    )
            ) {

                Text(
                    text = "Applications History",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Showing latest 20 sent applications",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    bottom = 20.dp
                )
            ) {

                items(
                    items = uiState.applications.take(20),
                    key = { it.id }
                ) { application ->

                    ApplicationItem(application)
                }
            }
        }
    }
}