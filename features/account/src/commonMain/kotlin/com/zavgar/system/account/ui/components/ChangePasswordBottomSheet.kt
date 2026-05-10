package com.zavgar.system.account.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zavgar.system.account.presentation.AccountIntent
import com.zavgar.system.account.presentation.AccountState
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.designsystem.components.button.AppPrimaryButton
import com.zavgar.system.designsystem.components.textfield.AppPasswordField
import androidx.compose.material3.MaterialTheme
import com.zavgar.system.designsystem.theme.*
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.account_change_confirm
import com.zavgar.system.resources.account_change_password
import com.zavgar.system.resources.account_new_password_label
import com.zavgar.system.resources.account_old_password_label
import com.zavgar.system.resources.password_placeholder
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordBottomSheet(
    state: AccountState,
    onIntent: (AccountIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!state.isPasswordDialogOpen) return
    val colors = MaterialTheme.colorScheme
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = {
            if (!state.isPasswordDialogLoading) onIntent(AccountIntent.DismissPasswordDialog)
        },
        sheetState = sheetState,
        containerColor = colors.card,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 4.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(Res.string.account_change_password),
                color = colors.foreground,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            AppPasswordField(
                value = state.oldPassword,
                onValueChange = { onIntent(AccountIntent.EnterOldPassword(it)) },
                label = stringResource(Res.string.account_old_password_label),
                placeholder = stringResource(Res.string.password_placeholder),
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isPasswordDialogLoading,
                isError = state.oldPasswordError != null,
                errorMessage = state.oldPasswordError?.asString(),
            )

            AppPasswordField(
                value = state.newPassword,
                onValueChange = { onIntent(AccountIntent.EnterNewPassword(it)) },
                label = stringResource(Res.string.account_new_password_label),
                placeholder = stringResource(Res.string.password_placeholder),
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isPasswordDialogLoading,
                isError = state.newPasswordError != null,
                errorMessage = state.newPasswordError?.asString(),
            )

            Spacer(Modifier.height(4.dp))

            AppPrimaryButton(
                text = stringResource(Res.string.account_change_confirm),
                onClick = { onIntent(AccountIntent.ClosePasswordDialog) },
                enabled = !state.isPasswordDialogLoading &&
                    state.oldPassword.isNotBlank() &&
                    state.newPassword.isNotBlank(),
                isLoading = state.isPasswordDialogLoading,
            )
        }
    }
}

@Preview
@Composable
private fun ChangePasswordBottomSheetPreview() {
    ZavGarThemePreview {
        ChangePasswordBottomSheet(
            state = AccountState(
                isPasswordDialogOpen = true,
                oldPassword = "",
                newPassword = "",
                isPasswordDialogLoading = false,
            ),
            onIntent = {},
        )
    }
}

@Preview
@Composable
private fun ChangePasswordBottomSheetWithErrorsPreview() {
    ZavGarThemePreview {
        ChangePasswordBottomSheet(
            state = AccountState(
                isPasswordDialogOpen = true,
                oldPassword = "123",
                newPassword = "abc",
                oldPasswordError = UiText.DynamicString("Неверный пароль"),
                newPasswordError = UiText.DynamicString("Пароль слишком короткий"),
                isPasswordDialogLoading = false,
            ),
            onIntent = {},
        )
    }
}
