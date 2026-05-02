package com.zavgar.system.designsystem.components.textfield

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zavgar.system.designsystem.components.text.AppTextMain
import com.zavgar.system.designsystem.components.text.AppTextSecondary
import com.zavgar.system.designsystem.theme.ZavGarThemePreview
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.icon_check
import org.jetbrains.compose.resources.vectorResource

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    Column(modifier = modifier) {
        AppTextMain(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            isError = isError,
            placeholder = {
                AppTextSecondary(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                // фон внутри
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
                errorContainerColor = MaterialTheme.colorScheme.surface,

                // Цвет обводки
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                errorBorderColor = MaterialTheme.colorScheme.error,

                // Цвет текста
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            ),
            shape = MaterialTheme.shapes.small,
            singleLine = true,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            trailingIcon = trailingIcon,
            readOnly = readOnly,
            interactionSource = interactionSource ?: remember { MutableInteractionSource() }
        )

        if (isError && !errorMessage.isNullOrBlank()) {
            AppTextSecondary(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun AppPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Пароль",
    placeholder: String = "Введите пароль",
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.Password
    ),
    readOnly: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    AppTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = placeholder,
        modifier = modifier,
        enabled = enabled,
        isError = isError,
        errorMessage = errorMessage,
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = keyboardOptions,
        readOnly = readOnly,
        interactionSource = interactionSource,
        trailingIcon = {
            val icon =
                if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility

            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}

@Composable
fun AppValidatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    isValid: Boolean,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    AppTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = placeholder,
        modifier = modifier,
        enabled = enabled,
        isError = isError,
        errorMessage = errorMessage,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        trailingIcon = if (isValid && !isError) {
            {
                Icon(
                    imageVector = vectorResource(Res.drawable.icon_check),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
        } else null
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun AppTextFieldPreview() {
    ZavGarThemePreview {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // --- 1. ОБЫЧНОЕ ПОЛЕ (Email/Имя) ---
            var simpleText by remember { mutableStateOf("") }

            AppTextField(
                label = "Имя пользователя",
                placeholder = "Введите ваше имя",
                value = simpleText,
                onValueChange = { simpleText = it }
            )

            // --- 2. ПОЛЕ С ОШИБКОЙ ---
            // Пример: показываем ошибку, если поле пустое (isBlank)
            var errorText by remember { mutableStateOf("") }
            val isError = errorText.isBlank() // Ошибка, если текст пустой или пробелы

            AppTextField(
                label = "Номер телефона (с ошибкой)",
                placeholder = "+7 (999) 000-00-00",
                value = errorText,
                onValueChange = { errorText = it },
                isError = isError,
                errorMessage = "Поле не может быть пустым"
            )

            // --- 3. ПОЛЕ ПАРОЛЯ (С логикой глаза) ---
            AppPasswordField(
                value = simpleText,
                onValueChange = { simpleText = it }
            )

            // --- 4. ОТКЛЮЧЕННОЕ ПОЛЕ (Disabled) ---
            AppTextField(
                label = "Неактивное поле",
                placeholder = "Сюда нельзя писать",
                value = "Заблокировано",
                onValueChange = {},
                enabled = false
            )
        }
    }
}