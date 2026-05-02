package com.zavgar.system.authorization.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zavgar.system.authorization.presentation.LoginEvent
import com.zavgar.system.authorization.presentation.LoginIntent
import com.zavgar.system.authorization.presentation.LoginState
import com.zavgar.system.authorization.presentation.LoginViewModel
import com.zavgar.system.core.presentation.ObserveAsEvents
import com.zavgar.system.core.presentation.compose.MaskVisualTransformation
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.designsystem.components.button.AppPrimaryButton
import com.zavgar.system.designsystem.components.scaffold.ZavGarAuthScaffold
import com.zavgar.system.designsystem.components.snackbar.CustomSnackbarHost
import com.zavgar.system.designsystem.components.snackbar.showCustomSnackbar
import com.zavgar.system.designsystem.components.textfield.AppPasswordField
import com.zavgar.system.designsystem.components.textfield.AppValidatedTextField
import com.zavgar.system.designsystem.modifiers.ShakingState
import com.zavgar.system.designsystem.modifiers.rememberShakingState
import com.zavgar.system.designsystem.theme.LocalZavGarColors
import com.zavgar.system.designsystem.theme.ZavGarThemePreview
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.login_button_text
import com.zavgar.system.resources.login_button_text_not_account
import com.zavgar.system.resources.login_button_text_register
import com.zavgar.system.resources.login_forgot_password
import com.zavgar.system.resources.login_password_label
import com.zavgar.system.resources.login_password_placeholder
import com.zavgar.system.resources.login_top_title
import com.zavgar.system.resources.phone_label
import com.zavgar.system.resources.phone_placeholder
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToWallet: () -> Unit,
    modifier: Modifier = Modifier
) {
    LoginLoader(
        modifier = modifier,
        onNavigateToRegister = onNavigateToRegister,
        onNavigateToForgotPassword = onNavigateToForgotPassword,
        onNavigateToWallet = onNavigateToWallet,
    )
}

@Composable
internal fun LoginLoader(
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToWallet: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = koinInject()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val errorShakingState = rememberShakingState(power = ShakingState.ShakePower.Low)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    viewModel.event.ObserveAsEvents { event ->
        when (event) {
            is LoginEvent.NavigateToRegister -> onNavigateToRegister()
            is LoginEvent.NavigateToForgotPassword -> onNavigateToForgotPassword()
            is LoginEvent.NavigateToWallet -> onNavigateToWallet()

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
        modifier = modifier
    )
}

@Composable
internal fun LoginScaffold(
    state: LoginState,
    snackbarHostState: SnackbarHostState,
    onIntent: (LoginIntent) -> Unit,
    errorShakingState: ShakingState,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val colors = LocalZavGarColors.current
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

        AuthorizationForm(state, onIntent)

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Text(
                text = stringResource(Res.string.login_forgot_password),
                color = colors.accent,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable(enabled = idle) { onIntent(LoginIntent.ClickForgotPassword) }
                    .padding(8.dp),
            )
        }

        Spacer(Modifier.height(8.dp))

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
private fun HasNotAccountRow(idle: Boolean, onClick: () -> Unit) {
    val colors = LocalZavGarColors.current
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

@Composable
private fun AuthorizationForm(
    state: LoginState,
    onIntent: (LoginIntent) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(11.dp)) {
        AppValidatedTextField(
            value = state.phone,
            onValueChange = { onIntent(LoginIntent.EnterPhone(it)) },
            isValid = state.isPhoneValid,
            label = stringResource(Res.string.phone_label),
            placeholder = stringResource(Res.string.phone_placeholder),
            isError = state.phoneError != null,
            errorMessage = state.phoneError?.asString(),
            visualTransformation = MaskVisualTransformation.DEFAULT_PHONE_MASK,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            enabled = state.screenState is LoginState.ScreenState.Idle
        )

        AppPasswordField(
            value = state.password,
            onValueChange = { onIntent(LoginIntent.EnterPassword(it)) },
            isError = state.passwordError != null,
            errorMessage = state.passwordError?.asString(),
            enabled = state.screenState is LoginState.ScreenState.Idle,
            label = stringResource(Res.string.login_password_label),
            placeholder = stringResource(Res.string.login_password_placeholder)
        )
    }
}

// --- ПРЕВЬЮ ---
@Preview(name = "Light Mode - Full Screen", showBackground = true)
@Composable
private fun LoginScreenPreview() {
    val previewState = LoginState(
        phone = "9991234567",
        isPhoneValid = true,
        phoneError = null,
        password = "",
        passwordError = null,
    )
    val snackbarHostState = remember { SnackbarHostState() }
    ZavGarThemePreview {
        LoginScaffold(
            state = previewState,
            snackbarHostState = snackbarHostState,
            onIntent = {},
            errorShakingState = rememberShakingState()
        )
    }
}

@Preview(name = "Loading State - Full Screen", showBackground = true)
@Composable
private fun LoginScreenLoadingPreview() {
    val loadingState = LoginState(
        phone = "9991234567",
        isPhoneValid = true,
        phoneError = null,
        password = "password",
        passwordError = null,
        screenState = LoginState.ScreenState.Submitting
    )
    val snackbarHostState = remember { SnackbarHostState() }
    ZavGarThemePreview {
        LoginScaffold(
            state = loadingState,
            snackbarHostState = snackbarHostState,
            onIntent = {},
            errorShakingState = rememberShakingState()
        )
    }
}

@Preview(name = "Error State - Full Screen", showBackground = true)
@Composable
private fun LoginScreenErrorPreview() {
    val errorState = LoginState(
        phone = "123",
        isPhoneValid = false,
        phoneError = UiText.DynamicString("Неверный формат"),
        password = "123",
        passwordError = UiText.DynamicString("Слишком короткий пароль"),
    )
    val snackbarHostState = remember { SnackbarHostState() }
    ZavGarThemePreview {
        LoginScaffold(
            state = errorState,
            snackbarHostState = snackbarHostState,
            onIntent = {},
            errorShakingState = rememberShakingState()
        )
    }
}
