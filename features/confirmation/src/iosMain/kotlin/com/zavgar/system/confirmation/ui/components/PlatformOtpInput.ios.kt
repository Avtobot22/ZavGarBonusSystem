@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
@file:Suppress("DEPRECATION")

package com.zavgar.system.confirmation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ObjCAction
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSSelectorFromString
import platform.UIKit.UIColor
import platform.UIKit.UIControlEventEditingChanged
import platform.UIKit.UIKeyboardTypeNumberPad
import platform.UIKit.UITextContentTypeOneTimeCode
import platform.UIKit.UITextField

@OptIn(BetaInteropApi::class)
private class NativeOtpTextField : UITextField(frame = CGRectMake(0.0, 0.0, 0.0, 0.0)) {
    var codeLength: Int = 0
    var onCodeChange: (String) -> Unit = {}

    init {
        backgroundColor = UIColor.clearColor
        textColor = UIColor.clearColor
        tintColor = UIColor.clearColor
        keyboardType = UIKeyboardTypeNumberPad
        textContentType = UITextContentTypeOneTimeCode
        addTarget(
            target = this,
            action = NSSelectorFromString("editingChanged"),
            forControlEvents = UIControlEventEditingChanged,
        )
    }

    @ObjCAction
    fun editingChanged() {
        val sanitized = text.orEmpty()
            .filter(Char::isDigit)
            .take(codeLength)
        if (text != sanitized) {
            text = sanitized
        }
        onCodeChange(sanitized)
    }
}

@Composable
internal actual fun PlatformOtpInput(
    value: String,
    onValueChange: (String) -> Unit,
    length: Int,
    enabled: Boolean,
    autoFocus: Boolean,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    var nativeTextField by remember { mutableStateOf<NativeOtpTextField?>(null) }

    Box(modifier = modifier) {
        UIKitView(
            factory = {
                NativeOtpTextField().also {
                    nativeTextField = it
                }
            },
            update = { textField ->
                textField.codeLength = length
                textField.onCodeChange = onValueChange
                textField.enabled = enabled
                textField.userInteractionEnabled = enabled
                if (textField.text != value) {
                    textField.text = value
                }
            },
            onRelease = { textField -> textField.resignFirstResponder() },
            interactive = true,
            accessibilityEnabled = true,
            modifier = Modifier.matchParentSize(),
        )
        content()
    }

    LaunchedEffect(nativeTextField, autoFocus, enabled) {
        if (autoFocus && enabled) {
            nativeTextField?.becomeFirstResponder()
        }
    }

    DisposableEffect(nativeTextField) {
        onDispose {
            nativeTextField?.resignFirstResponder()
        }
    }
}

@Composable
internal actual fun rememberPlatformOtpAutofillController(
    codeLength: Int,
    onCodeReceived: (String) -> Unit,
): OtpAutofillController = remember { NoOpOtpAutofillController }

private data object NoOpOtpAutofillController : OtpAutofillController {
    override fun start() = Unit
}
