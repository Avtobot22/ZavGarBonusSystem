package com.zavgar.system.designsystem.screen

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable

@Composable
fun Screen(content: @Composable () -> Unit) {
    Surface(
        contentColor = MaterialTheme.colorScheme.background,
    ) {
        content()
    }
}
