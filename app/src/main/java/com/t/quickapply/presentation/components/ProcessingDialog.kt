package com.t.quickapply.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProcessingDialog(
    isVisible: Boolean,
    message: String = "Processing..."
) {
    if (!isVisible) return

    // Smooth entrance animation
    val transition = rememberInfiniteTransition(label = "loader")

    val dot1 by transition.animateFloat(
        0.2f, 1f,
        animationSpec = infiniteRepeatable(
            tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )

    val dot2 by transition.animateFloat(
        0.2f, 1f,
        animationSpec = infiniteRepeatable(
            tween(600, delayMillis = 150),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )

    val dot3 by transition.animateFloat(
        0.2f, 1f,
        animationSpec = infiniteRepeatable(
            tween(600, delayMillis = 300),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f)), // softer overlay
        contentAlignment = Alignment.Center
    ) {

        Surface(
            shape = RoundedCornerShape(28.dp),
            tonalElevation = 14.dp,
            shadowElevation = 20.dp,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            modifier = Modifier
                .padding(24.dp)
                .scale(1f)
        ) {

            Column(
                modifier = Modifier
                    .padding(horizontal = 28.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                //  Modern dot loader (instead of spinner)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .scale(dot1)
                            .background(
                                MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(50)
                            )
                    )

                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .scale(dot2)
                            .background(
                                MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(50)
                            )
                    )

                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .scale(dot3)
                            .background(
                                MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(50)
                            )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = message,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}