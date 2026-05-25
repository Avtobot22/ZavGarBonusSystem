package com.zavgar.system.designsystem.components.datepicker

import androidx.compose.runtime.Composable
import kotlinx.datetime.LocalDate

@Composable
expect fun AppDatePicker(
    initialDate: LocalDate?,
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit,
)
