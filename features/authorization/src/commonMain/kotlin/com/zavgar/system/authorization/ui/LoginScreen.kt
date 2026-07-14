package com.zavgar.system.authorization.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zavgar.system.authorization.presentation.LoginEvent
import com.zavgar.system.authorization.presentation.LoginIntent
import com.zavgar.system.authorization.presentation.LoginState
import com.zavgar.system.authorization.presentation.LoginViewModel
import com.zavgar.system.core.presentation.ObserveAsEvents
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.designsystem.components.button.AppPrimaryButton
import com.zavgar.system.designsystem.components.scaffold.ZavGarAuthScaffold
import com.zavgar.system.designsystem.components.snackbar.CustomSnackbarHost
import com.zavgar.system.designsystem.components.snackbar.showCustomSnackbar
import com.zavgar.system.designsystem.components.textfield.AppPhoneTextField
import com.zavgar.system.designsystem.modifiers.ShakingState
import com.zavgar.system.designsystem.modifiers.rememberShakingState
import com.zavgar.system.designsystem.theme.ZavGarThemePreview
import com.zavgar.system.designsystem.theme.accent
import com.zavgar.system.designsystem.theme.foreground
import com.zavgar.system.designsystem.theme.foregroundSecondary
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.login_button_text
import com.zavgar.system.resources.login_button_text_not_account
import com.zavgar.system.resources.login_button_text_register
import com.zavgar.system.resources.login_sms_hint
import com.zavgar.system.resources.login_top_title
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    onNavigateToConfirmation: (phone: String) -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LoginLoader(
        modifier = modifier,
        onNavigateToConfirmation = onNavigateToConfirmation,
        onNavigateToRegister = onNavigateToRegister,
    )
}

@Composable
internal fun LoginLoader(
    onNavigateToConfirmation: (phone: String) -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val errorShakingState = rememberShakingState(power = ShakingState.ShakePower.Low)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    viewModel.event.ObserveAsEvents { event ->
        when (event) {
            is LoginEvent.NavigateToConfirmation -> onNavigateToConfirmation(event.phone)
            is LoginEvent.NavigateToRegister -> onNavigateToRegister()
            is LoginEvent.ShowSnackbar -> {
                scope.launch { errorShakingState.shake() }
                scope.launch {
                    snackbarHostState.showCustomSnackbar(
                        type = event.message.type,
                        message = event.message.message.suspendAsString(),
                        withDismissAction = true,
                    )
                }
            }
        }
    }

    LoginScaffold(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::handleIntent,
        errorShakingState = errorShakingState,
        modifier = modifier,
    )
}

@Composable
internal fun LoginScaffold(
    state: LoginState,
    snackbarHostState: SnackbarHostState,
    onIntent: (LoginIntent) -> Unit,
    errorShakingState: ShakingState,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val colors = MaterialTheme.colorScheme
    val idle = state.screenState is LoginState.ScreenState.Idle

    ZavGarAuthScaffold(
        modifier = modifier,
        snackbarHost = { CustomSnackbarHost(snackbarHostState = snackbarHostState) },
    ) {
        Text(
            text = stringResource(Res.string.login_top_title),
            color = colors.foreground,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp),
        )

        AppPhoneTextField(
            value = state.phone,
            onValueChange = { onIntent(LoginIntent.EnterPhone(it)) },
            isValid = state.isPhoneValid,
            isError = state.phoneError != null,
            errorMessage = state.phoneError?.asString(),
            enabled = idle,
        )

        Spacer(Modifier.height(16.dp))

        SmsHintDivider()

        Spacer(Modifier.height(16.dp))

        AppPrimaryButton(
            text = stringResource(Res.string.login_button_text),
            onClick = {
                focusManager.clearFocus()
                onIntent(LoginIntent.Submit)
            },
            enabled = state.isLoginButtonEnabled,
            isLoading = state.screenState is LoginState.ScreenState.Submitting,
            shakingState = errorShakingState,
        )

        Spacer(Modifier.height(16.dp))

        HasNotAccountRow(idle = idle, onClick = { onIntent(LoginIntent.ClickRegister) })
    }
}

@Composable
private fun SmsHintDivider() {
    val colors = MaterialTheme.colorScheme
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = colors.outline)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Sms,
                contentDescription = null,
                tint = colors.accent,
                modifier = Modifier.size(14.dp),
            )
            Text(
                text = stringResource(Res.string.login_sms_hint),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.foregroundSecondary,
            )
        }
        HorizontalDivider(modifier = Modifier.weight(1f), color = colors.outline)
    }
}

@Composable
private fun HasNotAccountRow(idle: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = stringResource(Res.string.login_button_text_not_account),
                color = colors.foregroundSecondary,
                fontSize = 14.sp,
            )
            Text(
                text = stringResource(Res.string.login_button_text_register),
                color = colors.accent,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable(enabled = idle, onClick = onClick)
                    .padding(vertical = 4.dp),
            )
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
private fun LoginScreenPreview() {
    val snackbarHostState = remember { SnackbarHostState() }
    ZavGarThemePreview {
        LoginScaffold(
            state = LoginState(phone = "9991234567", isPhoneValid = true),
            snackbarHostState = snackbarHostState,
            onIntent = {},
            errorShakingState = rememberShakingState(),
        )
    }
}

@Preview(name = "Error State", showBackground = true)
@Composable
private fun LoginScreenErrorPreview() {
    val snackbarHostState = remember { SnackbarHostState() }
    ZavGarThemePreview {
        LoginScaffold(
            state = LoginState(
                phone = "123",
                isPhoneValid = false,
                phoneError = UiText.DynamicString("Неверный формат"),
            ),
            snackbarHostState = snackbarHostState,
            onIntent = {},
            errorShakingState = rememberShakingState(),
        )
    }
}
