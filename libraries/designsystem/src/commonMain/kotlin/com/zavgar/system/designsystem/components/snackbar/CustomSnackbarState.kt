package com.zavgar.system.designsystem.components.snackbar

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import com.zavgar.system.core.presentation.util.SnackBarType

class CustomSnackbarVisuals(
    val type: SnackBarType,
    val title: String? = null,
    override val message: String,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    override val duration: SnackbarDuration =
        if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite,
) : SnackbarVisuals

suspend fun SnackbarHostState.showCustomSnackbar(
    type: SnackBarType,
    message: String,
    title: String? = null,
    actionLabel: String? = null,
    withDismissAction: Boolean = false,
    duration: SnackbarDuration = if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Long,
): SnackbarResult {
    currentSnackbarData?.dismiss()

    val visuals = CustomSnackbarVisuals(
        type = type,
        title = title,
        message = message,
        actionLabel = actionLabel,
        withDismissAction = withDismissAction,
        duration = duration,
    )

    return showSnackbar(visuals)
}
