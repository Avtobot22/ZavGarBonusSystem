package com.zavgar.system.registration.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
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
import com.zavgar.system.designsystem.components.datepicker.AppDatePicker
import com.zavgar.system.designsystem.components.scaffold.ZavGarAuthScaffold
import com.zavgar.system.designsystem.components.snackbar.CustomSnackbarHost
import com.zavgar.system.designsystem.components.snackbar.showCustomSnackbar
import com.zavgar.system.designsystem.components.textfield.AppDatePickerField
import com.zavgar.system.designsystem.components.textfield.AppTextField
import com.zavgar.system.designsystem.components.textfield.AppValidatedTextField
import com.zavgar.system.designsystem.modifiers.ShakingState
import com.zavgar.system.designsystem.modifiers.rememberShakingState
import com.zavgar.system.designsystem.theme.ZavGarThemePreview
import com.zavgar.system.designsystem.theme.accent
import com.zavgar.system.designsystem.theme.foreground
import com.zavgar.system.designsystem.theme.foregroundSecondary
import com.zavgar.system.registration.presentation.RegisterEvent
import com.zavgar.system.registration.presentation.RegisterIntent
import com.zavgar.system.registration.presentation.RegisterState
import com.zavgar.system.registration.presentation.RegisterViewModel
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.birth_date_label
import com.zavgar.system.resources.birth_date_placeholder
import com.zavgar.system.resources.login_button_text
import com.zavgar.system.resources.login_sms_hint
import com.zavgar.system.resources.phone_label
import com.zavgar.system.resources.phone_placeholder
import com.zavgar.system.resources.register_name_label
import com.zavgar.system.resources.register_name_placeholder
import com.zavgar.system.resources.register_top_title
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToConfirm: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    RegisterLoader(
        onNavigateToLogin = onNavigateToLogin,
        onNavigateToConfirm = onNavigateToConfirm,
        modifier = modifier
    )
}

@Composable
internal fun RegisterLoader(
    onNavigateToLogin: () -> Unit,
    onNavigateToConfirm: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val errorShakingState = rememberShakingState(power = ShakingState.ShakePower.Low)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    viewModel.event.ObserveAsEvents { event ->
        when (event) {
            is RegisterEvent.NavigateToLogin -> onNavigateToLogin()
            is RegisterEvent.NavigateToConfirm -> onNavigateToConfirm(event.phone)
            is RegisterEvent.ShowSnackbar -> {
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

    RegisterScaffold(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::handleIntent,
        errorShakingState = errorShakingState,
        modifier = modifier
    )
}

@Composable
internal fun RegisterScaffold(
    state: RegisterState,
    snackbarHostState: SnackbarHostState,
    onIntent: (RegisterIntent) -> Unit,
    errorShakingState: ShakingState,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val colors = MaterialTheme.colorScheme

    AppDatePicker(
        initialDate = state.birthDate,
        isOpen = state.isDatePickerOpen,
        onDismiss = { onIntent(RegisterIntent.DismissDatePicker) },
        onConfirm = { onIntent(RegisterIntent.CloseDatePicker(it)) }
    )

    ZavGarAuthScaffold(
        modifier = modifier,
        snackbarHost = { CustomSnackbarHost(snackbarHostState = snackbarHostState) },
        headerBottomPadding = 36.dp,
        sheetContentPadding = PaddingValues(horizontal = 28.dp, vertical = 22.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.padding(bottom = 18.dp),
        ) {
            ZavGarBackButton(onClick = { onIntent(RegisterIntent.ClickLogin) })
            Text(
                text = stringResource(Res.string.register_top_title),
                color = colors.foreground,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        RegisterForm(state, onIntent)

        Spacer(Modifier.height(16.dp))

        SmsHintDivider()

        AppPrimaryButton(
            text = stringResource(Res.string.login_button_text),
            onClick = {
                focusManager.clearFocus()
                onIntent(RegisterIntent.Submit)
            },
            enabled = state.isRegisterButtonEnabled,
            isLoading = state.screenState is RegisterState.ScreenState.Submitting,
            modifier = Modifier.padding(top = 16.dp),
            shakingState = errorShakingState
        )

        Spacer(Modifier.height(12.dp))

        RegisterPrivacyConsent(
            privacyPolicyUrl = state.privacyPolicyUrl,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun RegisterForm(
    state: RegisterState,
    onIntent: (RegisterIntent) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        AppTextField(
            value = state.name,
            onValueChange = { onIntent(RegisterIntent.EnterName(it)) },
            label = stringResource(Res.string.register_name_label),
            placeholder = stringResource(Res.string.register_name_placeholder),
            isError = state.nameError != null,
            errorMessage = state.nameError?.asString(),
            enabled = state.screenState is RegisterState.ScreenState.Idle
        )

        AppDatePickerField(
            value = state.birthDateText,
            onClick = { onIntent(RegisterIntent.OpenDatePicker) },
            label = stringResource(Res.string.birth_date_label),
            placeholder = stringResource(Res.string.birth_date_placeholder),
            isError = state.birthDateError != null,
            errorMessage = state.birthDateError?.asString(),
            enabled = state.screenState is RegisterState.ScreenState.Idle,
        )

        AppValidatedTextField(
            value = state.phone,
            onValueChange = { onIntent(RegisterIntent.EnterPhone(it)) },
            isValid = state.isPhoneValid,
            label = stringResource(Res.string.phone_label),
            placeholder = stringResource(Res.string.phone_placeholder),
            isError = state.phoneError != null,
            errorMessage = state.phoneError?.asString(),
            visualTransformation = MaskVisualTransformation.DEFAULT_PHONE_MASK,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            enabled = state.screenState is RegisterState.ScreenState.Idle
        )
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

@Preview(name = "Light Mode", showBackground = true)
@Composable
private fun RegisterScreenPreview() {
    val snackbarHostState = remember { SnackbarHostState() }
    ZavGarThemePreview {
        RegisterScaffold(
            state = RegisterState(
                name = "Иван",
                phone = "9991234567",
                isPhoneValid = true,
                privacyPolicyUrl = "https://zavgar.ru",
            ),
            snackbarHostState = snackbarHostState,
            onIntent = {},
            errorShakingState = rememberShakingState()
        )
    }
}

@Preview(name = "Error State", showBackground = true)
@Composable
private fun RegisterScreenErrorPreview() {
    val snackbarHostState = remember { SnackbarHostState() }
    ZavGarThemePreview {
        RegisterScaffold(
            state = RegisterState(
                nameError = UiText.DynamicString("Имя не может быть пустым"),
                phoneError = UiText.DynamicString("Неверный формат телефона"),
                birthDateError = UiText.DynamicString("Выберите дату рождения"),
                privacyPolicyUrl = "https://zavgar.ru",
            ),
            snackbarHostState = snackbarHostState,
            onIntent = {},
            errorShakingState = rememberShakingState()
        )
    }
}
