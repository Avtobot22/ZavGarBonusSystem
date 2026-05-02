package com.zavgar.system.designsystem.components.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zavgar.system.designsystem.modifiers.ShakingState
import com.zavgar.system.designsystem.modifiers.rememberShakingState
import com.zavgar.system.designsystem.modifiers.shakable
import com.zavgar.system.designsystem.theme.ZavGarThemePreview

@Composable
fun AppPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    shakingState: ShakingState = rememberShakingState(),
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth()
            .shakable(shakingState),
        enabled = enabled && !isLoading,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp,
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                leadingIcon?.invoke()
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    ),
                )
            }
        }
    }
}

@Preview
@Composable
private fun AppPrimaryButtonEnabledPreview() {
    ZavGarThemePreview {
        AppPrimaryButton(
            text = "Войти",
            onClick = {},
            enabled = true,
            isLoading = false,
            shakingState = rememberShakingState()
        )
    }
}

@Preview
@Composable
private fun AppPrimaryButtonLoadingPreview() {
    ZavGarThemePreview {
        AppPrimaryButton(
            text = "Войти",
            onClick = {},
            enabled = true,
            isLoading = true,
            shakingState = rememberShakingState()
        )
    }
}

@Preview
@Composable
private fun AppPrimaryButtonNotEnabledPreview() {
    ZavGarThemePreview {
        AppPrimaryButton(
            text = "Войти",
            onClick = {},
            enabled = false,
            isLoading = false,
            shakingState = rememberShakingState()
        )
    }
}