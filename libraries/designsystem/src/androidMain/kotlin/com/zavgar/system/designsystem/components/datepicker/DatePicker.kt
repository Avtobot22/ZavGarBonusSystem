package com.zavgar.system.designsystem.components.datepicker

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.dialog_picker_cancel
import com.zavgar.system.resources.dialog_picker_confirm
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun AppDatePicker(
    initialDate: LocalDate?,
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit,
) {
    if (!isOpen) return

    val initialMillis = remember(initialDate) {
        initialDate?.atStartOfDayIn(TimeZone.UTC)?.toEpochMilliseconds()
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= Clock.System.now().toEpochMilliseconds()
            }
        },
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.fromEpochMilliseconds(millis)
                            .toLocalDateTime(TimeZone.UTC)
                            .date
                        onConfirm(date)
                    }
                },
            ) {
                Text(stringResource(Res.string.dialog_picker_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.dialog_picker_cancel))
            }
        },
        shape = MaterialTheme.shapes.small,
    ) {
        DatePicker(state = datePickerState)
    }
}
