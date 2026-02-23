package com.zavgar.system.account.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.zavgar.system.account.presentation.AccountIntent
import com.zavgar.system.account.presentation.AccountState
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.designsystem.components.text.AppTextMain
import com.zavgar.system.designsystem.components.textfield.AppPasswordField
import com.zavgar.system.designsystem.theme.ZavGarThemePreview
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.account_change_cancel
import com.zavgar.system.resources.account_change_confirm
import com.zavgar.system.resources.account_confirm
import com.zavgar.system.resources.account_new_password_label
import com.zavgar.system.resources.account_old_password_label
import com.zavgar.system.resources.password_placeholder
import org.jetbrains.compose.resources.stringResource

@Composable
fun ChangePasswordDialog(
    state: AccountState,
    onIntent: (AccountIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.isPasswordDialogOpen) {
        Dialog(
            onDismissRequest = {
                if (!state.isPasswordDialogLoading) {
                    onIntent(AccountIntent.DismissPasswordDialog)
                }
            }
        ) {
            Surface(
                modifier = modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AppTextMain(
                        text = stringResource(Res.string.account_confirm),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    AppPasswordField(
                        value = state.oldPassword,
                        onValueChange = { onIntent(AccountIntent.EnterOldPassword(it)) },
                        label = stringResource(Res.string.account_old_password_label),
                        placeholder = stringResource(Res.string.password_placeholder),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        enabled = !state.isPasswordDialogLoading,
                        isError = state.oldPasswordError != null,
                        errorMessage = state.oldPasswordError?.asString(),
                    )

                    AppPasswordField(
                        value = state.newPassword,
                        onValueChange = { onIntent(AccountIntent.EnterNewPassword(it)) },
                        label = stringResource(Res.string.account_new_password_label),
                        placeholder = stringResource(Res.string.password_placeholder),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                        enabled = !state.isPasswordDialogLoading,
                        isError = state.newPasswordError != null,
                        errorMessage = state.newPasswordError?.asString(),
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { onIntent(AccountIntent.DismissPasswordDialog) },
                            enabled = !state.isPasswordDialogLoading,
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Text(stringResource(Res.string.account_change_cancel))
                        }

                        Button(
                            onClick = { onIntent(AccountIntent.ClosePasswordDialog) },
                            enabled = !state.isPasswordDialogLoading &&
                                    state.oldPassword.isNotBlank() &&
                                    state.newPassword.isNotBlank(),
                        ) {
                            if (state.isPasswordDialogLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(stringResource(Res.string.account_change_confirm))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ChangePasswordDialogPreview() {
    ZavGarThemePreview {
        ChangePasswordDialog(
            state = AccountState(
                isPasswordDialogOpen = true,
                oldPassword = "",
                newPassword = "",
                isPasswordDialogLoading = false
            ),
            onIntent = {}
        )
    }
}

@Preview
@Composable
private fun ChangePasswordDialogWithErrorsPreview() {
    ZavGarThemePreview {
        ChangePasswordDialog(
            state = AccountState(
                isPasswordDialogOpen = true,
                oldPassword = "123",
                newPassword = "abc",
                oldPasswordError = UiText.DynamicString("Неверный пароль"),
                newPasswordError = UiText.DynamicString("Пароль слишком короткий"),
                isPasswordDialogLoading = false
            ),
            onIntent = {}
        )
    }
}

@Preview
@Composable
private fun ChangePasswordDialogLoadingPreview() {
    ZavGarThemePreview {
        ChangePasswordDialog(
            state = AccountState(
                isPasswordDialogOpen = true,
                oldPassword = "oldpassword123",
                newPassword = "newpassword456",
                isPasswordDialogLoading = true
            ),
            onIntent = {}
        )
    }
}
