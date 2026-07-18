package com.zavgar.system.wallet.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.LifecycleResumeEffect
import platform.UIKit.UIScreen

@Composable
internal actual fun MaximumScreenBrightnessEffect() {
    val screen = UIScreen.mainScreen

    LifecycleResumeEffect(screen) {
        val previousBrightness = screen.brightness
        screen.brightness = MAXIMUM_BRIGHTNESS

        onPauseOrDispose {
            screen.brightness = previousBrightness
        }
    }
}

private const val MAXIMUM_BRIGHTNESS = 1.0
