package com.zavgar.system.designsystem.modifiers

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode

private const val GRADIENT_LENGTH = 500f

@Composable
fun Modifier.shimmerAnimation(shape: Shape = MaterialTheme.shapes.small): Modifier {
    val transition = rememberInfiniteTransition()
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )

    val shimmerColors = listOf(
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f),
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnimation, translateAnimation),
        end = Offset(translateAnimation + GRADIENT_LENGTH, translateAnimation),
        tileMode = TileMode.Mirror,
    )

    return background(brush, shape)
}