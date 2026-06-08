package com.zavgar.system.designsystem.components.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zavgar.system.designsystem.components.text.AppTextMain
import com.zavgar.system.designsystem.components.text.AppTextSecondary
import com.zavgar.system.designsystem.theme.ZavGarThemePreview

/**
 * Default dialog with confirm and dismiss button.
 *
 * @param title the dialog title
 * @param message the dialog content text
 * @param confirmText the text to be used in the confirm button
 * @param cancelText the text to be used in the dismiss button
 * @param onConfirm the action to be executed when the user confirms the dialog
 * @param isDialogOpen flag to indicate if the dialog should be open
 * @param onDismissRequest function to be called user requests to dismiss the dialog
 * @param modifier the modifier to be applied to the dialog
 */
@Composable
fun AppDialog(
    title: String,
    message: String,
    confirmText: String,
    cancelText: String,
    onConfirm: () -> Unit,
    isDialogOpen: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isDialogOpen) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { AppTextMain(text = title, style = MaterialTheme.typography.titleLarge) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    AppTextSecondary(text = message)
                }
            },
            confirmButton = {
                Button(onClick = onConfirm) {
                    Text(text = confirmText)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = onDismissRequest) {
                    Text(text = cancelText)
                }
            },
            modifier = modifier,
            shape = MaterialTheme.shapes.medium,
        )
    }
}

@Preview
@Composable
private fun DialogPreview() {
    ZavGarThemePreview {
        AppDialog(
            title = "Удаление аккаунта",
            message = "Вы уверены, что хотите удалить аккаунт?",
            confirmText = "Продолжить",
            cancelText = "Отмена",
            onConfirm = {},
            isDialogOpen = true,
            onDismissRequest = {},
        )
    }
}
