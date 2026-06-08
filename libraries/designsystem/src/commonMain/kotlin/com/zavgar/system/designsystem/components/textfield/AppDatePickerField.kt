package com.zavgar.system.designsystem.components.textfield

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager

@Composable
fun AppDatePickerField(
    value: String,
    onClick: () -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
) {
    val focusManager = LocalFocusManager.current

    Box(modifier = modifier) {
        AppTextField(
            value = value,
            onValueChange = {},
            label = label,
            placeholder = placeholder,
            modifier = Modifier,
            isError = isError,
            errorMessage = errorMessage,
            enabled = enabled,
            trailingIcon = {
                Icon(Icons.Default.DateRange, contentDescription = null)
            },
            readOnly = true,
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = enabled,
                ) {
                    focusManager.clearFocus()
                    onClick()
                },
        )
    }
}
