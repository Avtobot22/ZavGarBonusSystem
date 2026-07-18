package com.zavgar.system.wallet.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.lifecycle.compose.LifecycleResumeEffect

@Composable
internal actual fun MaximumScreenBrightnessEffect() {
    val context = LocalContext.current
    val view = LocalView.current
    val window = remember(view, context) {
        (view.parent as? DialogWindowProvider)?.window ?: context.findActivity()?.window
    } ?: return

    LifecycleResumeEffect(window) {
        val previousBrightness = window.attributes.screenBrightness
        window.attributes = window.attributes.apply {
            screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
        }

        onPauseOrDispose {
            window.attributes = window.attributes.apply {
                screenBrightness = previousBrightness
            }
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper if baseContext !== this -> baseContext.findActivity()
    else -> null
}
