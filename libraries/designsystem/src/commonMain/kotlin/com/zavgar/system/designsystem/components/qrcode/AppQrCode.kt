package com.zavgar.system.designsystem.components.qrcode

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zavgar.system.designsystem.modifiers.shimmerAnimation
import com.zavgar.system.designsystem.theme.ZavGarThemePreview
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.points
import org.jetbrains.compose.resources.stringResource
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
fun AppQrCode(
    card: String,
    points: Int,
    modifier: Modifier = Modifier,
    showPoints: Boolean = true
) {
    val colorScheme = MaterialTheme.colorScheme

    val painter = rememberQrKitPainter(data = card) {

        shapes = QrKitShapes(
            codeShape = QrKitCodeShape.Default,
            ballShape = QrKitBallShape.createRoundCorners(0.25f),
            darkPixelShape = QrKitPixelShape.createRoundCorners(0.25f),
            lightPixelShape = QrKitPixelShape.Default,
            frameShape = QrKitFrameShape.createRoundCorners(0.25f)
        )
        colors = QrKitColors(
            darkBrush = QrKitBrush.solidBrush(Color.Black),
            lightBrush = QrKitBrush.solidBrush(Color.White),
            ballBrush = QrKitBrush.solidBrush(colorScheme.primary),
            frameBrush = QrKitBrush.solidBrush(colorScheme.primary)
        )
    }

    Card(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painter,
                    modifier = Modifier.padding(10.dp).size(230.dp),
                    contentDescription = "QR код для карты $card"
                )
            }

            if (showPoints) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$points ",
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = stringResource(Res.string.points),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingQrCode(modifier: Modifier = Modifier) {

    Card(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.padding(10.dp).size(230.dp).clip(MaterialTheme.shapes.medium).shimmerAnimation()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(width = 80.dp, height = 52.dp).clip(MaterialTheme.shapes.small)
                        .shimmerAnimation()
                )
            }
        }
    }
}

@Preview
@Composable
private fun AppQrCodePreview() {
    ZavGarThemePreview {
        AppQrCode(
            card = "89831082464", points = 1250
        )
    }
}

@Preview
@Composable
private fun LoadingQrCodePreview() {
    ZavGarThemePreview {
        LoadingQrCode()
    }
}
