package com.zavgar.system.resetpassword.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zavgar.system.core.presentation.ObserveAsEvents
import com.zavgar.system.core.presentation.compose.MaskVisualTransformation
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.designsystem.components.button.AppPrimaryButton
import com.zavgar.system.designsystem.components.button.AppTextButton
import com.zavgar.system.designsystem.components.snackbar.CustomSnackbarHost
import com.zavgar.system.designsystem.components.snackbar.showCustomSnackbar
import com.zavgar.system.designsystem.components.textfield.AppPasswordField
import com.zavgar.system.designsystem.components.textfield.AppValidatedTextField
import com.zavgar.system.designsystem.components.topbar.AppTopBar
import com.zavgar.system.designsystem.modifiers.ShackingState
import com.zavgar.system.designsystem.modifiers.rememberShackingState
import com.zavgar.system.designsystem.theme.ZavGarThemePreview
import com.zavgar.system.resetpassword.presentation.ResetPasswordEvent
import com.zavgar.system.resetpassword.presentation.ResetPasswordIntent
import com.zavgar.system.resetpassword.presentation.ResetPasswordState
import com.zavgar.system.resetpassword.presentation.ResetPasswordViewModel
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_blank_phone
import com.zavgar.system.resources.error_passwords_not_match
import com.zavgar.system.resources.error_short_password
import com.zavgar.system.resources.password_label
import com.zavgar.system.resources.password_placeholder
import com.zavgar.system.resources.phone_label
import com.zavgar.system.resources.phone_placeholder
import com.zavgar.system.resources.repeat_password_label
import com.zavgar.system.resources.reset_password_button_text
import com.zavgar.system.resources.reset_password_button_text_login
import com.zavgar.system.resources.reset_password_button_text_return
import com.zavgar.system.resources.reset_top_title
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun ResetPasswordScreen(
    onNavigateToConfirm: (phone: String) -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    ResetPasswordLoader(
        onNavigateToConfirm = onNavigateToConfirm,
        onNavigateToLogin = onNavigateToLogin,
        modifier = modifier
    )
}

@Composable
internal fun ResetPasswordLoader(
    onNavigateToConfirm: (phone: String) -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ResetPasswordViewModel = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val errorShakingState = rememberShackingState(power = ShackingState.ShakePower.Low)
    val snackbarhostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.event) { event ->
        when (event) {
            is ResetPasswordEvent.NavigateToConfirm -> onNavigateToConfirm(event.phone)
            is ResetPasswordEvent.NavigateToLogin -> onNavigateToLogin()
            is ResetPasswordEvent.ShowSnackbar -> {
                launch {
                    errorShakingState.shake()
                }
                launch {
                    snackbarhostState.showCustomSnackbar(
                        type = event.message.type,
                        message = event.message.message.suspendAsString(),
                        withDismissAction = true,
                    )
                }
            }
        }
    }

    ResetPasswordScaffold(
        state = state,
        snackbarHostState = snackbarhostState,
        onIntent = viewModel::handleIntent,
        errorShakingState = errorShakingState,
        modifier = modifier
    )
}

@Composable
internal fun ResetPasswordScaffold(
    state: ResetPasswordState,
    snackbarHostState: SnackbarHostState,
    onIntent: (ResetPasswordIntent) -> Unit,
    errorShakingState: ShackingState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { CustomSnackbarHost(snackbarHostState = snackbarHostState) },
        topBar = {
            AppTopBar(
                title = stringResource(Res.string.reset_top_title),
                modifier = Modifier.padding(top = 24.dp)
            )
        },
    ) { paddingValues ->
        ResetPasswordContent(
            state = state,
            onIntent = onIntent,
            errorShakingState = errorShakingState,
            modifier = Modifier
                .padding(paddingValues)
        )
    }
}

@Composable
internal fun ResetPasswordContent(
    state: ResetPasswordState,
    onIntent: (ResetPasswordIntent) -> Unit,
    errorShakingState: ShackingState,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val phoneMask = remember { MaskVisualTransformation("+7 (###) ### ##-##") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        AppValidatedTextField(
            value = state.phone,
            onValueChange = { onIntent(ResetPasswordIntent.EnterPhone(it)) },
            isValid = state.isPhoneValid,
            label = stringResource(Res.string.phone_label),
            placeholder = stringResource(Res.string.phone_placeholder),
            modifier = Modifier.padding(vertical = 11.dp),
            isError = state.phoneError != null,
            errorMessage = state.phoneError?.asString(),
            visualTransformation = phoneMask,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            enabled = !state.isLoading
        )

        AppPasswordField(
            value = state.password,
            onValueChange = { onIntent(ResetPasswordIntent.EnterPassword(it)) },
            modifier = Modifier.padding(vertical = 11.dp),
            isError = state.passwordError != null,
            errorMessage = state.passwordError?.asString(),
            enabled = !state.isLoading,
            label = stringResource(Res.string.password_label),
            placeholder = stringResource(Res.string.password_placeholder)
        )

        AppPasswordField(
            value = state.repeatPassword,
            onValueChange = { onIntent(ResetPasswordIntent.EnterRepeatPassword(it)) },
            modifier = Modifier.padding(vertical = 11.dp),
            isError = state.repeatPasswordError != null,
            errorMessage = state.repeatPasswordError?.asString(),
            enabled = !state.isLoading,
            label = stringResource(Res.string.repeat_password_label),
            placeholder = stringResource(Res.string.password_placeholder)
        )

        AppPrimaryButton(
            text = stringResource(Res.string.reset_password_button_text),
            onClick = {
                focusManager.clearFocus()
                onIntent(ResetPasswordIntent.Submit)
            },
            enabled = !state.isLoading,
            isLoading = state.isLoading,
            modifier = Modifier.padding(top = 48.dp, bottom = 24.dp),
            shakingState = errorShakingState
        )

        AppTextButton(
            textGray = stringResource(Res.string.reset_password_button_text_return),
            textOrange = stringResource(Res.string.reset_password_button_text_login),
            onClick = { onIntent(ResetPasswordIntent.ClickLogin) },
            enabled = !state.isLoading
        )
    }
}

@Preview(name = "Default State")
@Composable
private fun ResetPasswordScaffoldPreview() {
    ZavGarThemePreview {
        ResetPasswordScaffold(
            state = ResetPasswordState(
                phone = "",
                password = "",
                repeatPassword = "",
                isLoading = false,
                isPhoneValid = false,
                phoneError = null,
                passwordError = null,
                repeatPasswordError = null
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onIntent = { },
            errorShakingState = rememberShackingState()
        )
    }
}

@Preview(name = "Loading State")
@Composable
private fun ResetPasswordScaffoldLoadingPreview() {
    ZavGarThemePreview {
        ResetPasswordScaffold(
            state = ResetPasswordState(
                phone = "9831082464",
                password = "password123",
                repeatPassword = "password123",
                isLoading = true,
                isPhoneValid = true,
                phoneError = null,
                passwordError = null,
                repeatPasswordError = null
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onIntent = { },
            errorShakingState = rememberShackingState()
        )
    }
}

@Preview(name = "Error State")
@Composable
private fun ResetPasswordScaffoldErrorPreview() {
    ZavGarThemePreview {
        ResetPasswordScaffold(
            state = ResetPasswordState(
                phone = "9831082464",
                password = "123",
                repeatPassword = "1234",
                isLoading = false,
                isPhoneValid = false,
                phoneError = UiText.Resource(Res.string.error_blank_phone),
                passwordError = UiText.Resource(Res.string.error_short_password),
                repeatPasswordError = UiText.Resource(Res.string.error_passwords_not_match)
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onIntent = { },
            errorShakingState = rememberShackingState()
        )
    }
}
