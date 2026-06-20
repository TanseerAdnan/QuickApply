package com.t.quickapply.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val headerSubtitles = listOf(
    "Your career, simplified",
    "Send your CV in seconds",
    "Draft. Apply. Get hired.",
    "Reach HRs with one tap",
    "Your job search, organized",
    "Applications made effortless"
)

@Composable
fun AppHeader(
    userName: String = ""
) {
    val infiniteTransition = rememberInfiniteTransition(label = "header_shimmer")

    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -300f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    // Subtitle rotation state
    var subtitleIndex by remember { mutableIntStateOf(0) }
    var subtitleVisible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            subtitleVisible = false
            delay(300) // wait for fade out
            subtitleIndex = (subtitleIndex + 1) % headerSubtitles.size
            subtitleVisible = true
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent,
        tonalElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (userName.isNotBlank()) 130.dp else 115.dp)
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.85f),
                            MaterialTheme.colorScheme.primary
                        ),
                        start = Offset(shimmerOffset, 0f),
                        end = Offset(shimmerOffset + 600f, 200f)
                    )
                )
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {

            // Decorative circle — top right
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 24.dp, y = (-24).dp)
                    .background(Color.White.copy(alpha = 0.07f), RoundedCornerShape(50))
            )

            // Decorative circle — bottom left
            Box(
                modifier = Modifier
                    .size(55.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = (-14).dp, y = 14.dp)
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(50))
            )

            // Main content
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // Left: Title + rotating subtitle
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "QuickApply",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    AnimatedVisibility(
                        visible = subtitleVisible,
                        enter = fadeIn(tween(300)) + slideInVertically(
                            tween(300),
                            initialOffsetY = { it / 2 }
                        ),
                        exit = fadeOut(tween(250)) + slideOutVertically(
                            tween(250),
                            targetOffsetY = { -it / 2 }
                        )
                    ) {
                        Text(
                            text = headerSubtitles[subtitleIndex],
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White.copy(alpha = 0.75f),
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                // Right: Greeting chip
                if (userName.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.White.copy(alpha = 0.15f),
                        tonalElevation = 0.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Avatar initial circle
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .background(
                                        color = Color.White.copy(alpha = 0.25f),
                                        shape = RoundedCornerShape(50)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = userName.first().uppercaseChar().toString(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Text(
                                text = userName.split(" ").first(), // first name only
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }
        }
    }
}