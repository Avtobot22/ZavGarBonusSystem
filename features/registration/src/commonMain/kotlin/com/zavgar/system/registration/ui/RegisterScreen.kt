package com.zavgar.system.registration.ui

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
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
import com.zavgar.system.designsystem.components.datepicker.AppDatePicker
import com.zavgar.system.designsystem.components.snackbar.CustomSnackbarHost
import com.zavgar.system.designsystem.components.snackbar.showCustomSnackbar
import com.zavgar.system.designsystem.components.textfield.AppDatePickerField
import com.zavgar.system.designsystem.components.textfield.AppPasswordField
import com.zavgar.system.designsystem.components.textfield.AppTextField
import com.zavgar.system.designsystem.components.textfield.AppValidatedTextField
import com.zavgar.system.designsystem.components.topbar.AppTopBar
import com.zavgar.system.designsystem.modifiers.ShakingState
import com.zavgar.system.designsystem.modifiers.rememberShakingState
import com.zavgar.system.designsystem.theme.ZavGarThemePreview
import com.zavgar.system.registration.presentation.RegisterEvent
import com.zavgar.system.registration.presentation.RegisterIntent
import com.zavgar.system.registration.presentation.RegisterState
import com.zavgar.system.registration.presentation.RegisterViewModel
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.birth_date_label
import com.zavgar.system.resources.birth_date_placeholder
import com.zavgar.system.resources.password_label
import com.zavgar.system.resources.password_placeholder
import com.zavgar.system.resources.phone_label
import com.zavgar.system.resources.phone_placeholder
import com.zavgar.system.resources.register_button_text
import com.zavgar.system.resources.register_button_text_has_account
import com.zavgar.system.resources.register_button_text_login
import com.zavgar.system.resources.register_name_label
import com.zavgar.system.resources.register_name_placeholder
import com.zavgar.system.resources.register_top_title
import com.zavgar.system.resources.repeat_password_label
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

private val PhoneMask = MaskVisualTransformation("+7 (###) ### ##-##")

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
    Scaffold(
        modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { CustomSnackbarHost(snackbarHostState = snackbarHostState) },
        topBar = {
            AppTopBar(
                title = stringResource(Res.string.register_top_title),
                modifier = Modifier.padding(top = 24.dp)
            )
        },
    ) { paddingValues ->
        RegisterContent(
            state = state,
            onIntent = onIntent,
            errorShakingState = errorShakingState,
            modifier = Modifier
                .padding(paddingValues)

        )

    }
}

@Composable
internal fun RegisterContent(
    state: RegisterState,
    onIntent: (RegisterIntent) -> Unit,
    errorShakingState: ShakingState,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    AppDatePicker(
        initialDate = state.birthDate,
        isOpen = state.isDatePickerOpen,
        onDismiss = { onIntent(RegisterIntent.DismissDatePicker) },
        onConfirm = { onIntent(RegisterIntent.CloseDatePicker(it)) }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        RegisterForm(state, onIntent, PhoneMask)

        RegisterButton(focusManager, onIntent, state, errorShakingState)

        HasAccountTextButton(onIntent, state)
    }
}

@Composable
private fun HasAccountTextButton(
    onIntent: (RegisterIntent) -> Unit,
    state: RegisterState
) {
    AppTextButton(
        textGray = stringResource(Res.string.register_button_text_has_account),
        textOrange = stringResource(Res.string.register_button_text_login),
        onClick = { onIntent(RegisterIntent.ClickLogin) },
        enabled = state.screenState is RegisterState.ScreenState.Idle
    )
}

@Composable
private fun RegisterButton(
    focusManager: FocusManager,
    onIntent: (RegisterIntent) -> Unit,
    state: RegisterState,
    errorShakingState: ShakingState
) {
    AppPrimaryButton(
        text = stringResource(Res.string.register_button_text),
        onClick = {
            focusManager.clearFocus()
            onIntent(RegisterIntent.Submit)
        },
        enabled = state.isRegisterButtonEnabled,
        isLoading = state.screenState is RegisterState.ScreenState.Submitting,
        modifier = Modifier.padding(top = 36.dp, bottom = 11.dp),
        shakingState = errorShakingState
    )
}

@Composable
private fun RegisterForm(
    state: RegisterState,
    onIntent: (RegisterIntent) -> Unit,
    phoneMask: MaskVisualTransformation
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(11.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
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
            visualTransformation = phoneMask,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            enabled = state.screenState is RegisterState.ScreenState.Idle
        )

        AppPasswordField(
            value = state.password,
            onValueChange = { onIntent(RegisterIntent.EnterPassword(it)) },
            isError = state.passwordError != null,
            errorMessage = state.passwordError?.asString(),
            enabled = state.screenState is RegisterState.ScreenState.Idle,
            label = stringResource(Res.string.password_label),
            placeholder = stringResource(Res.string.password_placeholder)
        )

        AppPasswordField(
            value = state.repeatPassword,
            onValueChange = { onIntent(RegisterIntent.EnterRepeatPassword(it)) },
            isError = state.repeatPasswordError != null,
            errorMessage = state.repeatPasswordError?.asString(),
            enabled = state.screenState is RegisterState.ScreenState.Idle,
            label = stringResource(Res.string.repeat_password_label),
            placeholder = stringResource(Res.string.password_placeholder)
        )

        RegisterCheckBox(
            checked = state.isTermsAccepted,
            onCheckedChange = { onIntent(RegisterIntent.AcceptTerms(it)) },
        )
    }
}

// --- ПРЕВЬЮ ---
@Preview(name = "Light Mode - Full Screen", showBackground = true)
@Composable
private fun RegisterScreenPreview() {
    val previewState = RegisterState(
        name = "Иван",
        nameError = null,
        phone = "9991234567",
        isPhoneValid = true,
        phoneError = null,
        password = "",
        passwordError = null,
        repeatPassword = "",
        repeatPasswordError = null,
        birthDate = null,
        birthDateError = null,
        isDatePickerOpen = false,
    )

    val snackbarHostState = remember { SnackbarHostState() }

    ZavGarThemePreview {
        RegisterScaffold(
            state = previewState,
            snackbarHostState = snackbarHostState,
            onIntent = {},
            errorShakingState = rememberShakingState()
        )
    }
}

@Preview(name = "Loading State - Full Screen", showBackground = true)
@Composable
private fun RegisterScreenLoadingPreview() {
    val loadingState = RegisterState(
        name = "Иван Петров",
        nameError = null,
        phone = "9991234567",
        isPhoneValid = true,
        phoneError = null,
        password = "password123",
        passwordError = null,
        repeatPassword = "password123",
        repeatPasswordError = null,
        birthDate = null,
        birthDateError = null,
        isDatePickerOpen = false,
        screenState = RegisterState.ScreenState.Submitting
    )

    val snackbarHostState = remember { SnackbarHostState() }

    ZavGarThemePreview {
        RegisterScaffold(
            state = loadingState,
            snackbarHostState = snackbarHostState,
            onIntent = {},
            errorShakingState = rememberShakingState()
        )
    }
}

@Preview(name = "Error State - Full Screen", showBackground = true)
@Composable
private fun RegisterScreenErrorPreview() {
    val errorState = RegisterState(
        name = "",
        nameError = UiText.DynamicString("Имя не может быть пустым"),
        phone = "123",
        isPhoneValid = false,
        phoneError = UiText.DynamicString("Неверный формат телефона"),
        password = "123",
        passwordError = UiText.DynamicString("Слишком короткий пароль"),
        repeatPassword = "456",
        repeatPasswordError = UiText.DynamicString("Пароли не совпадают"),
        birthDate = null,
        birthDateError = UiText.DynamicString("Выберите дату рождения"),
        isDatePickerOpen = false,
    )

    val snackbarHostState = remember { SnackbarHostState() }

    ZavGarThemePreview {
        RegisterScaffold(
            state = errorState,
            snackbarHostState = snackbarHostState,
            onIntent = {},
            errorShakingState = rememberShakingState()
        )
    }
}
