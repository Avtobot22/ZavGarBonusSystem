package com.zavgar.system.designsystem.components.button

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zavgar.system.designsystem.theme.ZavGarThemePreview

@Composable
fun AppPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth(),
        enabled = enabled && !isLoading,
        shape = MaterialTheme.shapes.small,
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
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
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
            isLoading = false
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
            isLoading = true
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
            isLoading = false
        )
    }
}