package com.zavgar.system.designsystem.background

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GlowBackground(
    color: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier,
    radius: Dp = 380.dp,
    content: @Composable () -> Unit,
) {
    val radiusPx = with(LocalDensity.current) { radius.toPx() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawWithCache {
                val brush = Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = 0.25f),
                        Color.Transparent,
                    ),
                    radius = radiusPx,
                )
                onDrawBehind {
                    drawRect(brush = brush)
                }
            },
    ) {
        content()
    }
}
