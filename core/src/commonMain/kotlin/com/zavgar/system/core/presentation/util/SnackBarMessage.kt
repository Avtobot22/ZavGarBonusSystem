package com.zavgar.system.core.presentation.util

data class SnackBarMessage(
    val message: UiText,
    val type: SnackBarType,
) {
    companion object {
        fun error(message: UiText) = SnackBarMessage(message = message, type = SnackBarType.ERROR)
        fun info(message: UiText) = SnackBarMessage(message = message, type = SnackBarType.INFO)
        fun success(message: UiText) = SnackBarMessage(message = message, type = SnackBarType.SUCCESS)
        fun warning(message: UiText) = SnackBarMessage(message = message, type = SnackBarType.WARNING)
    }
}
