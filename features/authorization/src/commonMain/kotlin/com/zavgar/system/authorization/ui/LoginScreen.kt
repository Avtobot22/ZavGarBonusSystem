package com.zavgar.system.authorization.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zavgar.system.authorization.presentation.LoginEvent
import com.zavgar.system.authorization.presentation.LoginIntent
import com.zavgar.system.authorization.presentation.LoginState
import com.zavgar.system.authorization.presentation.LoginViewModel
import com.zavgar.system.core.presentation.ObserveAsEvents
import com.zavgar.system.core.presentation.compose.MaskVisualTransformation
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.designsystem.components.button.AppPrimaryButton
import com.zavgar.system.designsystem.components.button.AppTextButton
import com.zavgar.system.designsystem.components.logo.AppLogoColored
import com.zavgar.system.designsystem.components.snackbar.CustomSnackbarHost
import com.zavgar.system.designsystem.components.snackbar.showCustomSnackbar
import com.zavgar.system.designsystem.components.textfield.AppPasswordField
import com.zavgar.system.designsystem.components.textfield.AppValidatedTextField
import com.zavgar.system.designsystem.components.topbar.AppTopBar
import com.zavgar.system.designsystem.modifiers.ShakingState
import com.zavgar.system.designsystem.modifiers.rememberShakingState
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

private val PhoneMask = MaskVisualTransformation("+7 (###) ### ##-##")

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
                scope.launch {
                    errorShakingState.shake()
                }
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
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { CustomSnackbarHost(snackbarHostState = snackbarHostState) },
        topBar = {
            AppTopBar(
                title = stringResource(Res.string.login_top_title),
                modifier = Modifier.padding(top = 24.dp)
            )
        },
    ) { paddingValues ->
        LoginContent(
            state = state,
            onIntent = onIntent,
            errorShakingState = errorShakingState,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
internal fun LoginContent(
    state: LoginState,
    onIntent: (LoginIntent) -> Unit,
    errorShakingState: ShakingState,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        AppLogoColored(modifier = Modifier.padding(vertical = 20.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(11.dp, Alignment.CenterVertically)
        ) {

            AuthorizationForm(state, onIntent, PhoneMask)

            ForgotPasswordButton(onIntent, state)

            LoginButton(focusManager, onIntent, state, errorShakingState)

            HasNotAccountButton(onIntent, state)
        }
    }
}

@Composable
private fun LoginButton(
    focusManager: FocusManager,
    onIntent: (LoginIntent) -> Unit,
    state: LoginState,
    errorShakingState: ShakingState
) {
    AppPrimaryButton(
        text = stringResource(Res.string.login_button_text),
        onClick = {
            focusManager.clearFocus()
            onIntent(LoginIntent.Submit)
        },
        enabled = state.isLoginButtonEnabled,
        isLoading = state.screenState is LoginState.ScreenState.Submitting,
        modifier = Modifier.padding(top = 12.dp),
        shakingState = errorShakingState
    )
}

@Composable
private fun HasNotAccountButton(
    onIntent: (LoginIntent) -> Unit,
    state: LoginState
) {
    AppTextButton(
        textGray = stringResource(Res.string.login_button_text_not_account),
        textOrange = stringResource(Res.string.login_button_text_register),
        onClick = { onIntent(LoginIntent.ClickRegister) },
        enabled = state.screenState is LoginState.ScreenState.Idle
    )
}

@Composable
private fun ForgotPasswordButton(
    onIntent: (LoginIntent) -> Unit,
    state: LoginState
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        TextButton(
            onClick = { onIntent(LoginIntent.ClickForgotPassword) },
            enabled = state.screenState is LoginState.ScreenState.Idle
        ) {
            Text(
                stringResource(Res.string.login_forgot_password),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun AuthorizationForm(
    state: LoginState,
    onIntent: (LoginIntent) -> Unit,
    phoneMask: MaskVisualTransformation
) {
    AppValidatedTextField(
        value = state.phone,
        onValueChange = { onIntent(LoginIntent.EnterPhone(it)) },
        isValid = state.isPhoneValid,
        label = stringResource(Res.string.phone_label),
        placeholder = stringResource(Res.string.phone_placeholder),
        isError = state.phoneError != null,
        errorMessage = state.phoneError?.asString(),
        visualTransformation = phoneMask,
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