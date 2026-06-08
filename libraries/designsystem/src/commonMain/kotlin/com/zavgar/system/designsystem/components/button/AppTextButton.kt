package com.zavgar.system.designsystem.components.button

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.zavgar.system.designsystem.theme.ZavGarThemePreview

@Composable
fun AppTextButton(
    textGray: String,
    textOrange: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onBackground,
            disabledContentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
        ),
    ) {
        val orangeColor = if (enabled) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        }
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = LocalContentColor.current,
                    ),
                ) {
                    append(textGray)
                }
                append(" ")
                withStyle(
                    style = SpanStyle(
                        color = orangeColor,
                        fontWeight = FontWeight.Bold,
                    ),
                ) {
                    append(textOrange)
                }
            },
            style = MaterialTheme.typography.titleSmall,
        )
    }
}

@Preview
@Composable
private fun AppTextButtonPreview() {
    ZavGarThemePreview {
        AppTextButton(
            textGray = "Нет аккаунта?",
            textOrange = "Зарегистрироваться",
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun AppTextButtonNotEnabledPreview() {
    ZavGarThemePreview {
        AppTextButton(
            textGray = "Нет аккаунта?",
            textOrange = "Зарегистрироваться",
            onClick = {},
            enabled = false,
        )
    }
}
