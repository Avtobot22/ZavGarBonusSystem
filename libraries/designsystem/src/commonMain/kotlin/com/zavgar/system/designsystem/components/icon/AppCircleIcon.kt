package com.zavgar.system.designsystem.components.icon

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zavgar.system.designsystem.theme.ZavGarThemePreview

@Composable
fun AppCircleIcon(
    painter: Painter,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    iconScale: Float = 0.9f,
) {
    Box(
        modifier = modifier
            .drawBehind {
                drawCircle(color = backgroundColor)
            },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.fillMaxSize(fraction = iconScale),
        )
    }
}

@Composable
fun AppCircleIcon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    iconScale: Float = 0.9f,
) {
    AppCircleIcon(
        painter = rememberVectorPainter(imageVector),
        modifier = modifier,
        tint = tint,
        backgroundColor = backgroundColor,
        iconScale = iconScale,
    )
}

@Preview
@Composable
private fun AppCircleIconOrangePreview() {
    ZavGarThemePreview {
        AppCircleIcon(
            imageVector = Icons.Default.Home,
            modifier = Modifier.size(48.dp),
        )
    }
}

@Preview
@Composable
private fun AppCircleIconGrayPreview() {
    ZavGarThemePreview {
        AppCircleIcon(
            imageVector = Icons.Default.Home,
            modifier = Modifier
                .size(48.dp),
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
        )
    }
}
