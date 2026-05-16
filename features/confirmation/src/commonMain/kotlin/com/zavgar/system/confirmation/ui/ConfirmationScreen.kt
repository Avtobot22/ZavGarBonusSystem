package com.zavgar.system.confirmation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zavgar.system.confirmation.presentation.ConfirmationEvent
import com.zavgar.system.confirmation.presentation.ConfirmationIntent
import com.zavgar.system.confirmation.presentation.ConfirmationState
import com.zavgar.system.confirmation.presentation.ConfirmationViewModel
import com.zavgar.system.core.presentation.ObserveAsEvents
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.designsystem.components.button.AppPrimaryButton
import com.zavgar.system.designsystem.components.button.ZavGarBackButton
import com.zavgar.system.designsystem.components.scaffold.ZavGarAuthScaffold
import com.zavgar.system.designsystem.components.snackbar.CustomSnackbarHost
import com.zavgar.system.designsystem.components.snackbar.showCustomSnackbar
import com.zavgar.system.confirmation.ui.components.OtpTextField
import com.zavgar.system.utils.validation.CODE_LENGTH
import com.zavgar.system.designsystem.modifiers.ShakingState
import com.zavgar.system.designsystem.modifiers.rememberShakingState
import androidx.compose.material3.MaterialTheme
import com.zavgar.system.designsystem.theme.*
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
    onNavigateToWallet: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ConfirmationLoader(
        phone = phone,
        isRegistration = isRegistration,
        onNavigateToLogin = onNavigateToLogin,
        onNavigateToWallet = onNavigateToWallet,
        modifier = modifier
    )
}

@Composable
internal fun ConfirmationLoader(
    phone: String,
    isRegistration: Boolean,
    onNavigateToLogin: () -> Unit,
    onNavigateToWallet: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ConfirmationViewModel = koinInject()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val errorShakingState = rememberShakingState(power = ShakingState.ShakePower.Low)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(phone, isRegistration) {
        viewModel.handleIntent(ConfirmationIntent.Initialize(phone, isRegistration))
    }

    viewModel.event.ObserveAsEvents { event ->
        when (event) {
            is ConfirmationEvent.NavigateToLogin -> onNavigateToLogin()
            is ConfirmationEvent.NavigateToWallet -> onNavigateToWallet()
            is ConfirmationEvent.ShowSnackbar -> {
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

    ConfirmationScaffold(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::handleIntent,
        errorShakingState = errorShakingState,
        onBackClick = onNavigateToLogin,
        modifier = modifier
    )
}

@Composable
internal fun ConfirmationScaffold(
    state: ConfirmationState,
    snackbarHostState: SnackbarHostState,
    onIntent: (ConfirmationIntent) -> Unit,
    errorShakingState: ShakingState,
    onBackClick: () -> Unit,
    modifier: Modifier
) {
    val colors = MaterialTheme.colorScheme

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
            ZavGarBackButton(onClick = onBackClick)
            Text(
                text = stringResource(Res.string.confirmation_top_title),
                color = colors.foreground,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        ConfirmDescriptionText(phone = state.phone)

        Spacer(Modifier.height(28.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            OtpTextField(
                value = state.code,
                onValueChange = { onIntent(ConfirmationIntent.EnterCode(it)) },
                length = CODE_LENGTH,
                isError = state.codeError != null,
                errorMessage = state.codeError?.asString(),
                enabled = state.screenState is ConfirmationState.ScreenState.Idle,
            )
        }

        Spacer(Modifier.height(32.dp))

        AppPrimaryButton(
            text = stringResource(Res.string.confirmation_button_text),
            onClick = { onIntent(ConfirmationIntent.Submit) },
            enabled = state.isConfirmButtonEnabled,
            isLoading = state.screenState is ConfirmationState.ScreenState.Submitting,
            shakingState = errorShakingState
        )

        Spacer(Modifier.height(20.dp))

        ResendConfirmationCodeRow(onIntent = onIntent, state = state)
    }
}

@Composable
private fun ConfirmDescriptionText(phone: String) {
    val colors = MaterialTheme.colorScheme
    val description = stringResource(Res.string.confirmation_text)
    val annotated: AnnotatedString = buildAnnotatedString {
        append(description)
        if (phone.isNotBlank()) {
            append(' ')
            withStyle(SpanStyle(color = colors.accent, fontWeight = FontWeight.Bold)) {
                append(formatPhoneNomer(phone))
            }
        }
    }
    Text(
        text = annotated,
        color = colors.foregroundSecondary,
        fontSize = 15.sp,
    )
}

@Composable
private fun ResendConfirmationCodeRow(
    onIntent: (ConfirmationIntent) -> Unit,
    state: ConfirmationState
) {
    val colors = MaterialTheme.colorScheme
    val canResend = state.timerSeconds <= 0 &&
            state.screenState !is ConfirmationState.ScreenState.Submitting

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.clickable(enabled = canResend) {
                onIntent(ConfirmationIntent.ClickResend)
            }
        ) {
            Text(
                text = stringResource(Res.string.confirmation_resend_code),
                color = if (canResend) colors.accent else colors.foregroundSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
            if (state.timerSeconds > 0 ||
                state.screenState is ConfirmationState.ScreenState.Submitting
            ) {
                Text(
                    text = "00:" + state.timerSeconds.toString().padStart(2, '0'),
                    color = colors.foregroundSecondary,
                    fontSize = 14.sp,
                )
            } else {
                Text(
                    text = "00:" + stringResource(Res.string.confirmation_default_time),
                    color = colors.foregroundSecondary,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

private fun formatPhoneNomer(phone: String): String {
    val digits = phone.filter { it.isDigit() }
    return if (digits.length == 10) {
        digits.replace(
            regex = Regex("(\\d{3})(\\d{3})(\\d{2})(\\d{2})"),
            replacement = "+7 ($1) $2 $3-$4"
        )
    } else {
        phone
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
            errorShakingState = rememberShakingState(),
            onBackClick = { },
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
            errorShakingState = rememberShakingState(),
            onBackClick = { },
        )
    }
}
