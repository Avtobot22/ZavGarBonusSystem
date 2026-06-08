package com.zavgar.system.designsystem.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zavgar.system.designsystem.components.button.AppPrimaryButton
import com.zavgar.system.designsystem.modifiers.ShakingState
import com.zavgar.system.designsystem.modifiers.rememberShakingState
import com.zavgar.system.designsystem.theme.ZavGarThemePreview
import com.zavgar.system.designsystem.theme.danger
import com.zavgar.system.designsystem.theme.dangerContainer
import com.zavgar.system.designsystem.theme.foreground
import com.zavgar.system.designsystem.theme.foregroundSecondary
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_unknown_error
import com.zavgar.system.resources.unknown_error_description
import com.zavgar.system.resources.update_button
import org.jetbrains.compose.resources.stringResource

@Composable
fun ErrorScreen(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    shakingState: ShakingState,
) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(colors.dangerContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                tint = colors.danger,
                modifier = Modifier.size(72.dp),
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = stringResource(Res.string.error_unknown_error),
            color = colors.foreground,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 32.sp,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = stringResource(Res.string.unknown_error_description),
            color = colors.foregroundSecondary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 24.sp,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(20.dp))

        AppPrimaryButton(
            text = stringResource(Res.string.update_button),
            onClick = onRetry,
            shakingState = shakingState,
            modifier = Modifier.fillMaxWidth().widthIn(max = 300.dp),
        )
    }
}

@Preview
@Composable
private fun ErrorScreenPreview() {
    ZavGarThemePreview {
        Screen {
            ErrorScreen(
                onRetry = {},
                shakingState = rememberShakingState(),
            )
        }
    }
}
