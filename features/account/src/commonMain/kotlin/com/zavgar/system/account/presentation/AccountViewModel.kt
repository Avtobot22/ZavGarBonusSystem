package com.zavgar.system.account.presentation

import com.zavgar.system.account.mapper.toDeleteResult
import com.zavgar.system.account.mapper.toProfileGetResult
import com.zavgar.system.account.mapper.toProfileUpdateResult
import com.zavgar.system.account.model.DeleteResult
import com.zavgar.system.account.model.ProfileGetResult
import com.zavgar.system.account.model.ProfileUpdateResult
import com.zavgar.system.analytics.AnalyticsEvent
import com.zavgar.system.analytics.AnalyticsTracker
import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.core.presentation.loading.ScreenLoadExecutionResult
import com.zavgar.system.core.presentation.loading.ScreenLoadPolicy
import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.SnackBarType
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.core.presentation.util.asUiText
import com.zavgar.system.core.presentation.util.toDisplayString
import com.zavgar.system.domain.userinfo.model.UpdateProfileRequest
import com.zavgar.system.domain.userinfo.usecase.DeleteUserProfileUseCase
import com.zavgar.system.domain.userinfo.usecase.GetUserProfileUseCase
import com.zavgar.system.domain.userinfo.usecase.UpdateUserProfileUseCase
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_unknown_error
import com.zavgar.system.resources.profile_update_success
import com.zavgar.system.utils.validation.ValidateBirthDateUseCase
import com.zavgar.system.utils.validation.ValidateNameUseCase
import com.zavgar.system.utils.validation.ValidationResult
import com.zavgar.system.utils.validation.toPresentation
import kotlinx.coroutines.Job
import kotlinx.datetime.LocalDate

class AccountViewModel(
    private val getProfileUseCase: GetUserProfileUseCase,
    private val updateProfileUseCase: UpdateUserProfileUseCase,
    private val deleteProfileUseCase: DeleteUserProfileUseCase,
    private val validateNameUseCase: ValidateNameUseCase,
    private val validateBirthDateUseCase: ValidateBirthDateUseCase,
    private val analyticsTracker: AnalyticsTracker,
) : BaseViewModel<AccountState, AccountIntent, AccountEvent>(AccountState()) {

    private var loadJob: Job? = null
    private val loadPolicy = ScreenLoadPolicy()

    override fun handleIntent(intent: AccountIntent) {
        when (intent) {
            is AccountIntent.ScreenEntered -> loadProfile(force = false)
            is AccountIntent.EnterName -> handleEnterName(intent.name)
            is AccountIntent.EnterBirthDate -> handleEnterBirthDate(intent.birthDate)
            is AccountIntent.ClickBack -> handleClickBack()
            is AccountIntent.ClickDelete -> handleClickDelete()
            is AccountIntent.DismissDeleteAccountDialog -> handleDismissDeleteAccountDialog()
            is AccountIntent.ConfirmDeleteAccount -> handleConfirmDeleteAccount()
            is AccountIntent.OpenDatePicker -> handleOpenDatePicker()
            is AccountIntent.CloseDatePicker -> handleCloseDatePicker()
            is AccountIntent.DismissDatePicker -> handleDismissDatePicker()
            is AccountIntent.Submit -> handleSubmit()
            is AccountIntent.Retry -> handleRetry()
        }
    }

    private fun loadProfile(force: Boolean) {
        val canStart = if (force) {
            loadPolicy.canStartForcedLoad(loadJob)
        } else {
            loadPolicy.canStartAutomaticLoad(loadJob)
        }
        if (!canStart) return

        loadJob = launchTry {
            if (currentState.screenState !is AccountState.ScreenState.Content) {
                setState { copy(screenState = AccountState.ScreenState.Loading) }
            }

            when (val execution = loadPolicy.executeWithTimeout { getProfileUseCase() }) {
                is ScreenLoadExecutionResult.Completed -> {
                    when (val result = execution.value.toProfileGetResult()) {
                        is ProfileGetResult.Success -> {
                            loadPolicy.markSuccessfulLoad()
                            setState {
                                copy(
                                    screenState = AccountState.ScreenState.Content,
                                    name = result.profile.name,
                                    phone = result.profile.phone,
                                    birthDate = result.profile.birthDate,
                                    birthDateText = result.profile.birthDate.toDisplayString(),
                                )
                            }
                        }

                        is ProfileGetResult.Error -> handleLoadFailure(result.message)
                    }
                }

                ScreenLoadExecutionResult.TimedOut -> handleLoadFailure(unknownErrorMessage())
            }
        } catch {
            handleLoadFailure(unknownErrorMessage())
        }
    }

    private fun handleRetry() {
        loadProfile(force = true)
    }

    private fun handleLoadFailure(message: SnackBarMessage) {
        if (currentState.screenState !is AccountState.ScreenState.Content) {
            setState { copy(screenState = AccountState.ScreenState.Error) }
        }
        setEvent { AccountEvent.ShowSnackbar(message) }
    }

    private fun handleEnterName(name: String) = setState {
        copy(
            name = name,
            nameError = null,
        )
    }

    private fun handleEnterBirthDate(birthDate: LocalDate) = setState {
        copy(
            birthDate = birthDate,
            birthDateText = birthDate.toDisplayString(),
            birthDateError = null,
            isDatePickerOpen = false,
        )
    }

    private fun handleOpenDatePicker() = setState { copy(isDatePickerOpen = true) }

    private fun handleCloseDatePicker() = setState { copy(isDatePickerOpen = false) }

    private fun handleDismissDatePicker() = setState { copy(isDatePickerOpen = false) }

    private fun handleClickBack() = setEvent { AccountEvent.NavigateBack }

    private fun handleClickDelete() = setState { copy(confirmDeleteDialog = true) }

    private fun handleDismissDeleteAccountDialog() = setState { copy(confirmDeleteDialog = false) }

    private fun handleConfirmDeleteAccount() {
        if (currentState.isSubmitting) return

        launchTry {
            setState { copy(screenState = AccountState.ScreenState.Submitting) }

            val appResult = deleteProfileUseCase()

            when (val result = appResult.toDeleteResult()) {
                is DeleteResult.Success -> {
                    analyticsTracker.log(AnalyticsEvent.DeleteAccount)
                    analyticsTracker.clearUser()
                    setEvent { AccountEvent.NavigateToLogin }
                }

                is DeleteResult.Error -> {
                    setState { copy(screenState = AccountState.ScreenState.Content) }
                    setEvent { AccountEvent.ShowSnackbar(result.message) }
                }
            }
        } catch {
            setState { copy(screenState = AccountState.ScreenState.Content) }
            setEvent {
                AccountEvent.ShowSnackbar(
                    SnackBarMessage.error(UiText.Resource(Res.string.error_unknown_error)),
                )
            }
        }
    }

    private fun handleSubmit() {
        if (currentState.isSubmitting) return

        val state = currentState

        val nameResult = validateNameUseCase(state.name).toPresentation { it.asUiText() }
        val birthDateResult = validateBirthDateUseCase(state.birthDate).toPresentation { it.asUiText() }

        val hasErrors = listOf(nameResult, birthDateResult).any { it is ValidationResult.Invalid }

        setState {
            copy(
                nameError = nameResult.errorOrNull(),
                birthDateError = birthDateResult.errorOrNull(),
            )
        }

        if (hasErrors) return

        performUpdate(
            UpdateProfileRequest(
                name = state.name,
                birthDate = requireNotNull(state.birthDate),
            ),
        )
    }

    private fun performUpdate(profileRequest: UpdateProfileRequest) {
        launchTry {
            setState { copy(screenState = AccountState.ScreenState.Submitting) }

            val appResult = updateProfileUseCase(profileRequest)
            setState { copy(screenState = AccountState.ScreenState.Content) }

            when (val result = appResult.toProfileUpdateResult()) {
                is ProfileUpdateResult.Success -> {
                    loadPolicy.markSuccessfulLoad()
                    setEvent {
                        AccountEvent.ShowSnackbar(
                            SnackBarMessage(
                                message = UiText.Resource(Res.string.profile_update_success),
                                type = SnackBarType.SUCCESS,
                            ),
                        )
                    }
                }

                is ProfileUpdateResult.Error -> setEvent {
                    AccountEvent.ShowSnackbar(result.message)
                }
            }
        } catch {
            setState { copy(screenState = AccountState.ScreenState.Content) }
            setEvent {
                AccountEvent.ShowSnackbar(
                    SnackBarMessage.error(UiText.Resource(Res.string.error_unknown_error)),
                )
            }
        }
    }

    private fun ValidationResult<UiText>.errorOrNull(): UiText? {
        return (this as? ValidationResult.Invalid)?.error
    }

    private fun unknownErrorMessage(): SnackBarMessage =
        SnackBarMessage.error(UiText.Resource(Res.string.error_unknown_error))
}
