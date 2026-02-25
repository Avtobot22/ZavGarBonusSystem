package com.zavgar.system.designsystem.components.textfield

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager

@Composable
fun AppClickablePasswordField(
    value: String,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true
) {
    val focusManager = LocalFocusManager.current

    Box(modifier = modifier) {
        AppPasswordField(
            value = value,
            onValueChange = {},
            label = label,
            placeholder = placeholder,
            modifier = Modifier,
            isError = isError,
            errorMessage = errorMessage,
            enabled = enabled,
            readOnly = true
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = enabled
                ) {
                    focusManager.clearFocus()
                    onClick()
                }
        )
    }
}