package com.zavgar.system.designsystem.components.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zavgar.system.designsystem.theme.ZavGarThemePreview

@Composable
fun AppOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled && !isLoading,
        shape = MaterialTheme.shapes.medium,
        border = null,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            disabledContentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
        ),
        contentPadding = contentPadding,
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 2.dp,
            )
        } else {
            ProvideTextStyle(value = MaterialTheme.typography.titleLarge) {
                content()
            }
        }
    }
}

@Composable
fun AppOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
) {
    AppOutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        isLoading = isLoading,
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 40.dp),
    ) {
        Text(text = text, textAlign = TextAlign.Center)
    }
}

@Preview
@Composable
private fun AppOutlinedButtonEnabledPreview() {
    ZavGarThemePreview {
        AppOutlinedButton(
            text = "Сохранить изменения",
            onClick = {},
            enabled = true,
            isLoading = false,
        )
    }
}

@Preview
@Composable
private fun AppOutlinedButtonLoadingPreview() {
    ZavGarThemePreview {
        AppOutlinedButton(
            text = "Сохранить изменения",
            onClick = {},
            enabled = true,
            isLoading = true,
        )
    }
}

@Preview
@Composable
private fun AppOutlinedButtonNotEnabledPreview() {
    ZavGarThemePreview {
        AppOutlinedButton(
            text = "Сохранить изменения",
            onClick = {},
            enabled = false,
            isLoading = false,
        )
    }
}
