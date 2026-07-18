package com.zavgar.system.designsystem.components.qrcode

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zavgar.system.designsystem.theme.accent
import com.zavgar.system.designsystem.theme.card
import qrgenerator.qrkitpainter.QrKitBallShape
import qrgenerator.qrkitpainter.QrKitBrush
import qrgenerator.qrkitpainter.QrKitCodeShape
import qrgenerator.qrkitpainter.QrKitColors
import qrgenerator.qrkitpainter.QrKitFrameShape
import qrgenerator.qrkitpainter.QrKitPixelShape
import qrgenerator.qrkitpainter.QrKitShapes
import qrgenerator.qrkitpainter.createRoundCorners
import qrgenerator.qrkitpainter.rememberQrKitPainter
import qrgenerator.qrkitpainter.solidBrush

@Composable
fun ZavGarQrImage(
    card: String,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme

    val painter = rememberQrKitPainter(data = card) {
        shapes = QrKitShapes(
            codeShape = QrKitCodeShape.Default,
            ballShape = QrKitBallShape.createRoundCorners(0.25f),
            darkPixelShape = QrKitPixelShape.createRoundCorners(0.25f),
            lightPixelShape = QrKitPixelShape.Default,
            frameShape = QrKitFrameShape.createRoundCorners(0.25f),
        )
        colors = QrKitColors(
            darkBrush = QrKitBrush.solidBrush(Color.Black),
            lightBrush = QrKitBrush.solidBrush(Color.White),
            ballBrush = QrKitBrush.solidBrush(colorScheme.accent),
            frameBrush = QrKitBrush.solidBrush(colorScheme.accent),
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview
@Composable
private fun ZavGarQrImagePreview() {
    ZavGarQrImage(
        card = "1234 5678 9012 3456",
        modifier = Modifier.size(220.dp),
    )
}
