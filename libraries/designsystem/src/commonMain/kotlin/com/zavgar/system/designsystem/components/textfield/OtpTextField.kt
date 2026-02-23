package com.zavgar.system.designsystem.components.textfield

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zavgar.system.designsystem.components.text.AppTextSecondary
import com.zavgar.system.designsystem.theme.ZavGarThemePreview

@Composable
fun OtpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    length: Int = 6,
    isError: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BasicTextField(
            value = value,
            onValueChange = {
                if (enabled && it.length <= length && it.all { char -> char.isDigit() }) {
                    onValueChange(it.take(length))
                }
            },
            enabled = enabled,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            decorationBox = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(length) { index ->
                        val char = when {
                            index < value.length -> value[index].toString()
                            else -> ""
                        }
                        val isFocused = index == value.length && enabled

                        OtpCell(
                            char = char,
                            isFocused = isFocused,
                            isError = isError,
                            enabled = enabled
                        )
                    }
                }
            },
            cursorBrush = SolidColor(Color.Transparent),
            modifier = Modifier,
        )

        if (isError && !errorMessage.isNullOrBlank()) {
            AppTextSecondary(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun OtpCell(
    char: String,
    isFocused: Boolean,
    isError: Boolean,
    enabled: Boolean = true,
) {
    val borderColor = when {
        !enabled -> MaterialTheme.colorScheme.outline.copy(alpha = 0.38f)
        isError -> MaterialTheme.colorScheme.error
        isFocused -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline
    }

    val backgroundColor = if (enabled) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.38f)
    }

    val textColor = if (enabled) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    }

    Box(
        modifier = Modifier
            .width(50.dp)
            .height(58.dp)
            .background(
                color = backgroundColor,
                shape = MaterialTheme.shapes.small
            )
            .border(
                width = if (isFocused && enabled) 2.dp else 1.dp,
                color = borderColor,
                shape = MaterialTheme.shapes.small
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (char.isNotEmpty()) {
            Text(
                text = char,
                style = MaterialTheme.typography.titleLarge,
                color = textColor,
                textAlign = TextAlign.Center,
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun OtpTextFieldPreview() {
    ZavGarThemePreview {
        var otpValue by remember { mutableStateOf("123") }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Обычное состояние
            OtpTextField(
                value = otpValue,
                onValueChange = { otpValue = it },
                length = 6
            )

            // Состояние с ошибкой
            OtpTextField(
                value = "1234",
                onValueChange = { },
                length = 6,
                isError = true,
                errorMessage = "Поле не может быть пустым",
                modifier = Modifier
            )

//            // Отключенное состояние
//            OtpTextField(
//                value = "12345",
//                onValueChange = { },
//                length = 6,
//                enabled = false
//            )
//
//            // Полностью заполненное поле
//            OtpTextField(
//                value = "123456",
//                onValueChange = { },
//                length = 6
//            )
        }
    }
}

