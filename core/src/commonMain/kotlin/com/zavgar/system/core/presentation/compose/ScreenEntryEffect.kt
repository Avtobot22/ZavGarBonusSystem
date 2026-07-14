package com.zavgar.system.core.presentation.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState

/**
 * Reports that the calling screen entered the composition.
 *
 * [key] must identify the screen owner (normally its ViewModel), so ordinary
 * recompositions do not dispatch another entry event.
 */
@Composable
fun ScreenEntryEffect(
    key: Any?,
    onEnter: () -> Unit,
) {
    val currentOnEnter by rememberUpdatedState(onEnter)

    LaunchedEffect(key) {
        currentOnEnter()
    }
}
