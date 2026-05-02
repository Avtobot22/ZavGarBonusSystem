package com.zavgar.system.resetpassword.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import com.zavgar.system.core.presentation.ObserveAsEvents
import com.zavgar.system.core.presentation.compose.MaskVisualTransformation
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.designsystem.components.button.AppPrimaryButton
import com.zavgar.system.designsystem.components.button.ZavGarBackButton
import com.zavgar.system.designsystem.components.scaffold.ZavGarAuthScaffold
import com.zavgar.system.designsystem.components.snackbar.CustomSnackbarHost
import com.zavgar.system.designsystem.components.snackbar.showCustomSnackbar
import com.zavgar.system.designsystem.components.textfield.AppPasswordField
import com.zavgar.system.designsystem.components.textfield.AppValidatedTextField
import com.zavgar.system.designsystem.modifiers.ShakingState
import com.zavgar.system.designsystem.modifiers.rememberShakingState
import com.zavgar.system.designsystem.theme.LocalZavGarColors
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
import com.zavgar.system.resources.reset_password_description
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
    val errorShakingState = rememberShakingState(power = ShakingState.ShakePower.Low)
    val snackbarhostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    viewModel.event.ObserveAsEvents { event ->
        when (event) {
            is ResetPasswordEvent.NavigateToConfirm -> onNavigateToConfirm(event.phone)
            is ResetPasswordEvent.NavigateToLogin -> onNavigateToLogin()
            is ResetPasswordEvent.ShowSnackbar -> {
                scope.launch { errorShakingState.shake() }
                scope.launch {
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
    errorShakingState: ShakingState,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val colors = LocalZavGarColors.current

    ZavGarAuthScaffold(
        modifier = modifier,
        snackbarHost = { CustomSnackbarHost(snackbarHostState = snackbarHostState) },
        headerBottomPadding = 36.dp,
        sheetContentPadding = PaddingValues(horizontal = 28.dp, vertical = 22.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.padding(bottom = 12.dp),
        ) {
            ZavGarBackButton(onClick = { onIntent(ResetPasswordIntent.ClickLogin) })
            Text(
                text = stringResource(Res.string.reset_top_title),
                color = colors.foreground,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Text(
            text = stringResource(Res.string.reset_password_description),
            color = colors.foregroundSecondary,
            fontSize = 15.sp,
            modifier = Modifier.padding(bottom = 22.dp),
        )

        ResetAccountForm(state, onIntent)

        AppPrimaryButton(
            text = stringResource(Res.string.reset_password_button_text),
            onClick = {
                focusManager.clearFocus()
                onIntent(ResetPasswordIntent.Submit)
            },
            enabled = state.isResetPasswordButtonEnabled,
            isLoading = state.screenState is ResetPasswordState.ScreenState.Submitting,
            modifier = Modifier.padding(top = 22.dp),
            shakingState = errorShakingState
        )
    }
}

@Composable
private fun ResetAccountForm(
    state: ResetPasswordState,
    onIntent: (ResetPasswordIntent) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        AppValidatedTextField(
            value = state.phone,
            onValueChange = { onIntent(ResetPasswordIntent.EnterPhone(it)) },
            isValid = state.isPhoneValid,
            label = stringResource(Res.string.phone_label),
            placeholder = stringResource(Res.string.phone_placeholder),
            isError = state.phoneError != null,
            errorMessage = state.phoneError?.asString(),
            visualTransformation = MaskVisualTransformation.DEFAULT_PHONE_MASK,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            enabled = state.screenState is ResetPasswordState.ScreenState.Idle
        )

        AppPasswordField(
            value = state.password,
            onValueChange = { onIntent(ResetPasswordIntent.EnterPassword(it)) },
            isError = state.passwordError != null,
            errorMessage = state.passwordError?.asString(),
            enabled = state.screenState is ResetPasswordState.ScreenState.Idle,
            label = stringResource(Res.string.password_label),
            placeholder = stringResource(Res.string.password_placeholder)
        )

        AppPasswordField(
            value = state.repeatPassword,
            onValueChange = { onIntent(ResetPasswordIntent.EnterRepeatPassword(it)) },
            isError = state.repeatPasswordError != null,
            errorMessage = state.repeatPasswordError?.asString(),
            enabled = state.screenState is ResetPasswordState.ScreenState.Idle,
            label = stringResource(Res.string.repeat_password_label),
            placeholder = stringResource(Res.string.password_placeholder)
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
                isPhoneValid = false,
                phoneError = null,
                passwordError = null,
                repeatPasswordError = null
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onIntent = { },
            errorShakingState = rememberShakingState()
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
                isPhoneValid = false,
                phoneError = UiText.Resource(Res.string.error_blank_phone),
                passwordError = UiText.Resource(Res.string.error_short_password),
                repeatPasswordError = UiText.Resource(Res.string.error_passwords_not_match)
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onIntent = { },
            errorShakingState = rememberShakingState()
        )
    }
}
