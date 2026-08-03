package com.zavgar.system.confirmation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal expect fun PlatformOtpInput(
    value: String,
    onValueChange: (String) -> Unit,
    length: Int,
    enabled: Boolean,
    autoFocus: Boolean,
    modifier: Modifier,
    content: @Composable () -> Unit,
)

internal interface OtpAutofillController {
    fun start()
}

@Composable
internal expect fun rememberPlatformOtpAutofillController(
    codeLength: Int,
    onCodeReceived: (String) -> Unit,
): OtpAutofillController
