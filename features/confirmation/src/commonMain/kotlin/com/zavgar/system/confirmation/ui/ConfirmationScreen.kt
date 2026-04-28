package com.zavgar.system.confirmation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zavgar.system.confirmation.presentation.ConfirmationEvent
import com.zavgar.system.confirmation.presentation.ConfirmationIntent
import com.zavgar.system.confirmation.presentation.ConfirmationState
import com.zavgar.system.confirmation.presentation.ConfirmationViewModel
import com.zavgar.system.core.presentation.ObserveAsEvents
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.designsystem.components.button.AppPrimaryButton
import com.zavgar.system.designsystem.components.snackbar.CustomSnackbarHost
import com.zavgar.system.designsystem.components.snackbar.showCustomSnackbar
import com.zavgar.system.designsystem.components.text.AppTextSecondary
import com.zavgar.system.designsystem.components.textfield.OtpTextField
import com.zavgar.system.designsystem.components.topbar.AppTopBar
import com.zavgar.system.designsystem.modifiers.ShakingState
import com.zavgar.system.designsystem.modifiers.rememberShakingState
import com.zavgar.system.designsystem.theme.ZavGarThemePreview
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.confirmation_button_text
import com.zavgar.system.resources.confirmation_default_time
import com.zavgar.system.resources.confirmation_resend_code
import com.zavgar.system.resources.confirmation_text
import com.zavgar.system.resources.confirmation_top_title
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun ConfirmationScreen(
    phone: String,
    isRegistration: Boolean,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ConfirmationLoader(
        phone = phone,
        isRegistration = isRegistration,
        onNavigateToLogin = onNavigateToLogin,
        modifier = modifier
    )
}

@Composable
internal fun ConfirmationLoader(
    phone: String,
    isRegistration: Boolean,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ConfirmationViewModel = koinInject()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val errorShakingState = rememberShakingState(power = ShakingState.ShakePower.Low)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(phone, isRegistration) {
        viewModel.handleIntent(ConfirmationIntent.Initialize(phone, isRegistration))
    }

    ObserveAsEvents(viewModel.event) { event ->
        when (event) {
            is ConfirmationEvent.NavigateToLogin -> onNavigateToLogin()
            is ConfirmationEvent.ShowSnackbar -> {
                launch {
                    errorShakingState.shake()
                }
                launch {
                    snackbarHostState.showCustomSnackbar(
                        type = event.message.type,
                        message = event.message.message.suspendAsString(),
                        withDismissAction = true,
                    )
                }
            }
        }
    }

    ConfirmationScaffold(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::handleIntent,
        errorShakingState = errorShakingState,
        modifier = modifier
    )
}

@Composable
internal fun ConfirmationScaffold(
    state: ConfirmationState,
    snackbarHostState: SnackbarHostState,
    onIntent: (ConfirmationIntent) -> Unit,
    errorShakingState: ShakingState,
    modifier: Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { CustomSnackbarHost(snackbarHostState = snackbarHostState) },
        topBar = {
            AppTopBar(
                title = stringResource(Res.string.confirmation_top_title),
                modifier = Modifier.padding(top = 24.dp)
            )
        }
    ) { paddingValues ->
        ConfirmationContent(
            state = state,
            onIntent = onIntent,
            errorShakingState = errorShakingState,
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
        )
    }
}

@Composable
internal fun ConfirmationContent(
    state: ConfirmationState,
    onIntent: (ConfirmationIntent) -> Unit,
    errorShakingState: ShakingState,
    modifier: Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        ConfirmDescriptionText()

        OtpTextField(
            value = state.code,
            onValueChange = { onIntent(ConfirmationIntent.EnterCode(it)) },
            length = 6,
            isError = state.codeError != null,
            errorMessage = state.codeError?.asString(),
            modifier = Modifier.padding(vertical = 44.dp),
            enabled = state.screenState is ConfirmationState.ScreenState.Idle,
        )

        ResendConfirmationCodeButton(onIntent, state)

        ConfirmButton(onIntent, state, errorShakingState)
    }
}

@Composable
private fun ConfirmDescriptionText() {
    AppTextSecondary(
        text = stringResource(Res.string.confirmation_text),
        style = MaterialTheme.typography.bodyLarge,
    )
}

@Composable
private fun ConfirmButton(
    onIntent: (ConfirmationIntent) -> Unit,
    state: ConfirmationState,
    errorShakingState: ShakingState
) {
    AppPrimaryButton(
        text = stringResource(Res.string.confirmation_button_text),
        onClick = { onIntent(ConfirmationIntent.Submit) },
        modifier = Modifier.padding(top = 35.dp),
        enabled = state.isConfirmButtonEnabled,
        isLoading = state.screenState is ConfirmationState.ScreenState.Submitting,
        shakingState = errorShakingState
    )
}

@Composable
private fun ResendConfirmationCodeButton(
    onIntent: (ConfirmationIntent) -> Unit,
    state: ConfirmationState
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.clickable { onIntent(ConfirmationIntent.ClickResend) }
    ) {
        if (state.timerSeconds > 0 || state.screenState is ConfirmationState.ScreenState.Submitting) {
            Text(
                text = stringResource(Res.string.confirmation_resend_code),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = state.timerSeconds.toString().padStart(2, '0'),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Text(
                text = stringResource(Res.string.confirmation_resend_code),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(Res.string.confirmation_default_time),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(name = "Normal State", showBackground = true)
@Composable
fun ConfirmationScreenNormalPreview() {
    ZavGarThemePreview {
        ConfirmationScaffold(
            state = ConfirmationState(
                phone = "+7 999 123 45 67",
                isRegistration = true,
                code = "123",
                timerSeconds = 45
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onIntent = { },
            modifier = Modifier,
            errorShakingState = rememberShakingState()
        )
    }
}

@Preview(name = "Error State", showBackground = true)
@Composable
fun ConfirmationScreenErrorPreview() {
    ZavGarThemePreview {
        ConfirmationScaffold(
            state = ConfirmationState(
                phone = "+7 999 123 45 67",
                isRegistration = false,
                code = "1234",
                codeError = UiText.DynamicString("Неверный код"),
                timerSeconds = 0
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onIntent = { },
            modifier = Modifier,
            errorShakingState = rememberShakingState()
        )
    }
}

@Preview(name = "Loading State", showBackground = true)
@Composable
fun ConfirmationScreenLoadingPreview() {
    ZavGarThemePreview {
        ConfirmationScaffold(
            state = ConfirmationState(
                phone = "+7 999 123 45 67",
                isRegistration = true,
                code = "123456",
                screenState = ConfirmationState.ScreenState.Submitting,
                timerSeconds = 30
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onIntent = { },
            modifier = Modifier,
            errorShakingState = rememberShakingState()
        )
    }
}