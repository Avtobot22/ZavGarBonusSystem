package com.zavgar.system.account.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zavgar.system.account.presentation.AccountEvent
import com.zavgar.system.account.presentation.AccountIntent
import com.zavgar.system.account.presentation.AccountState
import com.zavgar.system.account.presentation.AccountViewModel
import com.zavgar.system.account.ui.components.ChangePasswordDialog
import com.zavgar.system.account.ui.components.DeleteAccountDialog
import com.zavgar.system.core.presentation.ObserveAsEvents
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.designsystem.components.button.AppOutlinedButton
import com.zavgar.system.designsystem.components.content.AnimatedState
import com.zavgar.system.designsystem.components.content.AppProgressIndicator
import com.zavgar.system.designsystem.components.datepicker.AppDatePicker
import com.zavgar.system.designsystem.components.snackbar.CustomSnackbarHost
import com.zavgar.system.designsystem.components.snackbar.showCustomSnackbar
import com.zavgar.system.designsystem.components.textfield.AppClickablePasswordField
import com.zavgar.system.designsystem.components.textfield.AppDatePickerField
import com.zavgar.system.designsystem.components.textfield.AppTextField
import com.zavgar.system.designsystem.components.topbar.AppProfilTopBar
import com.zavgar.system.designsystem.modifiers.ShakingState
import com.zavgar.system.designsystem.modifiers.rememberShakingState
import com.zavgar.system.designsystem.screen.ErrorScreen
import com.zavgar.system.designsystem.screen.Screen
import com.zavgar.system.designsystem.theme.ZavGarThemePreview
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.account_confirm
import com.zavgar.system.resources.account_password_label
import com.zavgar.system.resources.account_password_pattern
import com.zavgar.system.resources.account_top_title
import com.zavgar.system.resources.birth_date_label
import com.zavgar.system.resources.birth_date_placeholder
import com.zavgar.system.resources.register_name_label
import com.zavgar.system.resources.register_name_placeholder
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun AccountScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    AccountLoader(
        onNavigateToLogin = onNavigateToLogin,
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@Composable
internal fun AccountLoader(
    onNavigateToLogin: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AccountViewModel = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val errorShakingState = rememberShakingState()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.event) { event ->
        when (event) {
            is AccountEvent.NavigateToLogin -> onNavigateToLogin()
            is AccountEvent.NavigateBack -> onNavigateBack()
            is AccountEvent.ShowSnackbar -> {
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

    AccountScaffold(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::handleIntent,
        errorShakingState = errorShakingState,
        modifier = modifier
    )
}

@Composable
internal fun AccountScaffold(
    state: AccountState,
    snackbarHostState: SnackbarHostState,
    onIntent: (AccountIntent) -> Unit,
    errorShakingState: ShakingState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = Color.Transparent,
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { CustomSnackbarHost(snackbarHostState = snackbarHostState) },
        topBar = {
            AppProfilTopBar(
                title = stringResource(Res.string.account_top_title),
                onBackClick = { onIntent(AccountIntent.ClickBack) },
                onActionClick = { onIntent(AccountIntent.ClickDelete) },
                modifier = Modifier.padding(top = 24.dp)
            )
        },
    ) { paddingValues ->
        AnimatedState(targetState = state) { state ->
            when (state.screenState) {
                AccountState.ScreenState.Error -> ErrorScreen(
                    onRetry = { onIntent(AccountIntent.Retry) },
                    modifier = Modifier.padding(paddingValues),
                    shakingState = errorShakingState
                )

                AccountState.ScreenState.Initial,
                AccountState.ScreenState.Loading -> AccountLoading(
                    modifier = Modifier.padding(paddingValues)
                )

                AccountState.ScreenState.Content -> AccountContent(
                    state = state,
                    onIntent = onIntent,
                    modifier = Modifier
                        .padding(paddingValues)
                )
            }
        }
    }
}

@Composable
internal fun AccountContent(state: AccountState, onIntent: (AccountIntent) -> Unit, modifier: Modifier) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    ChangePasswordDialog(state = state, onIntent = onIntent)
    DeleteAccountDialog(state = state, onIntent = onIntent)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(11.dp, Alignment.CenterVertically)
    ) {
        AccountForm(state, onIntent)

        AppOutlinedButton(
            text = stringResource(Res.string.account_confirm),
            onClick = {
                focusManager.clearFocus()
                onIntent(AccountIntent.Submit)
            },
            enabled = !state.isLoading,
            isLoading = state.isLoading,
            modifier = Modifier.padding(top = 60.dp)
        )
    }
}

@Composable
private fun AccountForm(
    state: AccountState,
    onIntent: (AccountIntent) -> Unit
) {
    AppTextField(
        value = state.name,
        onValueChange = { onIntent(AccountIntent.EnterName(it)) },
        label = stringResource(Res.string.register_name_label),
        placeholder = stringResource(Res.string.register_name_placeholder),
        isError = state.nameError != null,
        errorMessage = state.nameError?.asString(),
        enabled = !state.isLoading
    )

    AppDatePickerField(
        value = state.birthDateText,
        onClick = { onIntent(AccountIntent.OpenDatePicker) },
        label = stringResource(Res.string.birth_date_label),
        placeholder = stringResource(Res.string.birth_date_placeholder),
        isError = state.birthDateError != null,
        errorMessage = state.birthDateError?.asString(),
        enabled = !state.isLoading,
    )

    AppDatePicker(
        initialDate = state.birthDate,
        isOpen = state.isDatePickerOpen,
        onDismiss = { onIntent(AccountIntent.DismissDatePicker) },
        onConfirm = { onIntent(AccountIntent.EnterBirthDate(it)) }
    )

    AppClickablePasswordField(
        value = stringResource(Res.string.account_password_pattern),
        onClick = { onIntent(AccountIntent.OpenPasswordDialog) },
        label = stringResource(Res.string.account_password_label),
        enabled = !state.isLoading,
    )
}

@Composable
internal fun AccountLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AppProgressIndicator()
    }
}

@Preview
@Composable
fun AccountScreenPreview() {
    val mockState = AccountState(
        screenState = AccountState.ScreenState.Content,
        name = "Иван Петров",
        birthDate = LocalDate(1990, 5, 15),
        birthDateText = "15.05.1990",
        nameError = null,
        birthDateError = null,
        isLoading = false,
        isDatePickerOpen = false
    )
    ZavGarThemePreview {
        Screen {
            AccountScaffold(
                state = mockState,
                snackbarHostState = remember { SnackbarHostState() },
                onIntent = {},
                modifier = Modifier,
                errorShakingState = rememberShakingState()
            )
        }
    }
}

@Preview
@Composable
fun AccountScreenLoadingPreview() {
    val mockState = AccountState(
        screenState = AccountState.ScreenState.Loading,
        name = "Иван Петров",
        birthDate = LocalDate(1990, 5, 15),
        birthDateText = "15.05.1990",
        nameError = null,
        birthDateError = null,
        isLoading = true,
        isDatePickerOpen = false
    )
    ZavGarThemePreview {
        Screen {
            AccountScaffold(
                state = mockState,
                snackbarHostState = remember { SnackbarHostState() },
                onIntent = {},
                modifier = Modifier,
                errorShakingState = rememberShakingState()
            )
        }
    }
}

@Preview
@Composable
fun AccountScreenWithErrorsPreview() {
    val mockState = AccountState(
        screenState = AccountState.ScreenState.Content,
        name = "",
        birthDate = null,
        birthDateText = "",
        nameError = UiText.Resource(Res.string.register_name_placeholder),
        birthDateError = UiText.Resource(Res.string.birth_date_placeholder),
        isLoading = false,
        isDatePickerOpen = false
    )
    ZavGarThemePreview {
        Screen {
            AccountScaffold(
                state = mockState,
                snackbarHostState = remember { SnackbarHostState() },
                onIntent = {},
                modifier = Modifier,
                errorShakingState = rememberShakingState()
            )
        }
    }
}
