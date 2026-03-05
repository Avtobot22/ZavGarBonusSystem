package com.zavgar.system.designsystem.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zavgar.system.designsystem.components.button.AppPrimaryButton
import com.zavgar.system.designsystem.components.text.AppTextMain
import com.zavgar.system.designsystem.components.text.AppTextSecondary
import com.zavgar.system.designsystem.modifiers.ShackingState
import com.zavgar.system.designsystem.modifiers.rememberShackingState
import com.zavgar.system.designsystem.theme.ZavGarThemePreview
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_unknown_error
import com.zavgar.system.resources.icon_wrong
import com.zavgar.system.resources.unknown_error_description
import com.zavgar.system.resources.update_button
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun ErrorScreen(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    shakingState: ShackingState
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.icon_wrong),
            contentDescription = null,
            modifier = Modifier.size(212.dp),
            tint = MaterialTheme.colorScheme.onBackground
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
        ) {
            AppTextMain(
                text = stringResource(Res.string.error_unknown_error),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )

            AppTextSecondary(
                text = stringResource(Res.string.unknown_error_description),
                modifier = Modifier.padding(horizontal = 80.dp),
                textAlign = TextAlign.Center
            )

            AppPrimaryButton(
                text = stringResource(Res.string.update_button),
                onClick = onRetry,
                shakingState = shakingState
            )
        }
    }
}

@Preview
@Composable
private fun ErrorScreenPreview() {
    ZavGarThemePreview {
        Screen {
            ErrorScreen(
                onRetry = {},
                shakingState = rememberShackingState()
            )
        }
    }
}