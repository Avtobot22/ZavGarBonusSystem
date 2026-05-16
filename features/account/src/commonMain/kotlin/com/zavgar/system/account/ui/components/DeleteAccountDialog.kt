package com.zavgar.system.account.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zavgar.system.account.presentation.AccountIntent
import com.zavgar.system.account.presentation.AccountState
import com.zavgar.system.designsystem.components.dialog.AppDialog
import com.zavgar.system.designsystem.theme.ZavGarThemePreview
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.account_delete_cancel
import com.zavgar.system.resources.account_delete_confirm
import com.zavgar.system.resources.account_delete_description
import com.zavgar.system.resources.account_delete_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun DeleteAccountDialog(state: AccountState, onIntent: (AccountIntent) -> Unit, modifier: Modifier = Modifier) {
    AppDialog(
        title = stringResource(Res.string.account_delete_title),
        message = stringResource(Res.string.account_delete_description),
        confirmText = stringResource(Res.string.account_delete_confirm),
        cancelText = stringResource(Res.string.account_delete_cancel),
        onConfirm = { onIntent(AccountIntent.ConfirmDeleteAccount) },
        isDialogOpen = state.confirmDeleteDialog,
        onDismissRequest = { onIntent(AccountIntent.DismissDeleteAccountDialog) },
        modifier = modifier,
    )
}

@Preview
@Composable
private fun DeleteAccountDialogPreview() {
    ZavGarThemePreview {
        DeleteAccountDialog(
            state = AccountState(confirmDeleteDialog = true),
            onIntent = {}
        )
    }
}
