package com.zavgar.system.wallet.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zavgar.system.designsystem.components.qrcode.ZavGarQrImage
import com.zavgar.system.designsystem.theme.card
import com.zavgar.system.designsystem.theme.foreground
import com.zavgar.system.designsystem.theme.foregroundSecondary
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.wallet_qr_close
import com.zavgar.system.resources.wallet_qr_content_description
import com.zavgar.system.resources.wallet_qr_dialog_description
import com.zavgar.system.resources.wallet_qr_dialog_title
import com.zavgar.system.resources.wallet_qr_enlarge
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun WalletQrCode(
    card: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val enlargeLabel = stringResource(Res.string.wallet_qr_enlarge)
    val contentDescription = stringResource(Res.string.wallet_qr_content_description)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ZavGarQrImage(
            card = card,
            contentDescription = contentDescription,
            modifier = modifier.clickable(
                role = Role.Button,
                onClickLabel = enlargeLabel,
                onClick = onClick,
            ),
        )
        Text(
            text = enlargeLabel,
            color = MaterialTheme.colorScheme.foregroundSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
internal fun WalletQrDialog(
    card: String,
    onDismissRequest: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val contentDescription = stringResource(Res.string.wallet_qr_content_description)
    val closeLabel = stringResource(Res.string.wallet_qr_close)
    val dismissInteractionSource = remember { MutableInteractionSource() }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false,
        ),
    ) {
        MaximumScreenBrightnessEffect()

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = dismissInteractionSource,
                    indication = null,
                    onClickLabel = closeLabel,
                    onClick = onDismissRequest,
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            val dialogWidth = minOf(maxWidth, 420.dp)
            val qrSize = minOf(
                320.dp,
                dialogWidth - 40.dp,
                maxHeight - 184.dp,
            ).coerceAtLeast(120.dp)

            Surface(
                modifier = Modifier
                    .width(dialogWidth)
                    .pointerInput(Unit) { detectTapGestures(onTap = {}) },
                shape = RoundedCornerShape(28.dp),
                color = colors.card,
                tonalElevation = 6.dp,
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Spacer(Modifier.width(48.dp))
                        Text(
                            text = stringResource(Res.string.wallet_qr_dialog_title),
                            color = colors.foreground,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f),
                        )
                        IconButton(onClick = onDismissRequest) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = closeLabel,
                                tint = colors.foreground,
                            )
                        }
                    }

                    Text(
                        text = stringResource(Res.string.wallet_qr_dialog_description),
                        color = colors.foregroundSecondary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp),
                    )

                    ZavGarQrImage(
                        card = card,
                        contentDescription = contentDescription,
                        modifier = Modifier.size(qrSize),
                    )

                    TextButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.padding(top = 12.dp),
                    ) {
                        Text(text = closeLabel)
                    }
                }
            }
        }
    }
}
