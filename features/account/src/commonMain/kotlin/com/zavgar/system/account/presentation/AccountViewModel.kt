package com.zavgar.system.account.presentation

import com.zavgar.system.account.mapper.toDeleteResult
import com.zavgar.system.account.mapper.toProfileGetResult
import com.zavgar.system.account.mapper.toProfileUpdateResult
import com.zavgar.system.account.model.DeleteResult
import com.zavgar.system.account.model.ProfileGetResult
import com.zavgar.system.account.model.ProfileUpdateResult
import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.SnackBarType
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.core.presentation.util.toDisplayString
import com.zavgar.system.domain.userinfo.model.UpdateProfileRequest
import com.zavgar.system.domain.userinfo.usecase.DeleteUserProfileUseCase
import com.zavgar.system.domain.userinfo.usecase.GetUserProfileUseCase
import com.zavgar.system.domain.userinfo.usecase.UpdateUserProfileUseCase
import com.zavgar.system.utils.validation.ValidateBirthDateUseCase
import com.zavgar.system.utils.validation.ValidateNameUseCase
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_unknown_error
import com.zavgar.system.resources.profile_delete_success
import com.zavgar.system.resources.profile_update_success
import com.zavgar.system.utils.validation.ValidationResult
import com.zavgar.system.utils.validation.asUiText
import com.zavgar.system.utils.validation.toPresentation
import kotlinx.datetime.LocalDate

class AccountViewModel(
    private val getProfileUseCase: GetUserProfileUseCase,
    private val updateProfileUseCase: UpdateUserProfileUseCase,
    private val deleteProfileUseCase: DeleteUserProfileUseCase,
    private val validateNameUseCase: ValidateNameUseCase,
    private val validateBirthDateUseCase: ValidateBirthDateUseCase
) : BaseViewModel<AccountState, AccountIntent, AccountEvent>(AccountState()) {

    init {
        initProfile()
    }

    override fun handleIntent(intent: AccountIntent) {
        when (intent) {
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

    private fun initProfile() {
        launchTry {
            setState { copy(screenState = AccountState.ScreenState.Loading, isLoading = true) }

            val appResult = getProfileUseCase()
            setState { copy(isLoading = false) }

            when (val result = appResult.toProfileGetResult()) {
                is ProfileGetResult.Success ->
                    setState {
                        copy(
                            screenState = AccountState.ScreenState.Content,
                            name = result.profile.name,
                            phone = result.profile.phone,
                            birthDate = result.profile.birthDate,
                            birthDateText = result.profile.birthDate.toDisplayString()
                        )
                    }

                is ProfileGetResult.Error -> {
                    setState { copy(screenState = AccountState.ScreenState.Error) }
                    setEvent { AccountEvent.ShowSnackbar(result.message) }
                }
            }
        } catch {
            setState { copy(isLoading = false, screenState = AccountState.ScreenState.Error) }
            setEvent {
                AccountEvent.ShowSnackbar(
                    SnackBarMessage.error(UiText.Resource(Res.string.error_unknown_error))
                )
            }
        }
    }

    private fun handleRetry() {
        initProfile()
    }

    private fun handleEnterName(name: String) = setState {
        copy(
            name = name,
            nameError = null
        )
    }

    private fun handleEnterBirthDate(birthDate: LocalDate) = setState {
        copy(
            birthDate = birthDate,
            birthDateText = birthDate.toDisplayString(),
            birthDateError = null,
            isDatePickerOpen = false
        )
    }

    private fun handleOpenDatePicker() = setState { copy(isDatePickerOpen = true) }

    private fun handleCloseDatePicker() = setState { copy(isDatePickerOpen = false) }

    private fun handleDismissDatePicker() = setState { copy(isDatePickerOpen = false) }

    private fun handleClickBack() = setEvent { AccountEvent.NavigateBack }

    private fun handleClickDelete() = setState { copy(confirmDeleteDialog = true) }

    private fun handleDismissDeleteAccountDialog() = setState { copy(confirmDeleteDialog = false) }

    private fun handleConfirmDeleteAccount() {
        if (currentState.isLoading) return

        launchTry {
            setState { copy(isLoading = true) }

            val appResult = deleteProfileUseCase()
            setState { copy(isLoading = false) }

            when (val result = appResult.toDeleteResult()) {
                is DeleteResult.Success -> {
                    setEvent {
                        AccountEvent.ShowSnackbar(
                            SnackBarMessage(
                                message = UiText.Resource(Res.string.profile_delete_success),
                                type = SnackBarType.SUCCESS
                            )
                        )
                    }
                    setEvent { AccountEvent.NavigateToLogin }
                }

                is DeleteResult.Error -> setEvent { AccountEvent.ShowSnackbar(result.message) }
            }
        } catch {
            setState { copy(isLoading = false) }
            setEvent {
                AccountEvent.ShowSnackbar(
                    SnackBarMessage.error(UiText.Resource(Res.string.error_unknown_error))
                )
            }
        }
    }

    private fun handleSubmit() {
        if (currentState.isLoading) return

        val state = currentState

        val nameResult = validateNameUseCase(state.name).toPresentation { it.asUiText() }
        val birthDateResult = validateBirthDateUseCase(state.birthDate).toPresentation { it.asUiText() }

        val hasErrors = listOf(nameResult, birthDateResult).any { it is ValidationResult.Invalid }

        setState {
            copy(
                nameError = nameResult.errorOrNull(),
                birthDateError = birthDateResult.errorOrNull()
            )
        }

        if (hasErrors) return

        performUpdate(
            UpdateProfileRequest(
                name = state.name,
                birthDate = requireNotNull(state.birthDate)
            )
        )
    }

    private fun performUpdate(profileRequest: UpdateProfileRequest) {
        launchTry {
            setState { copy(isLoading = true) }

            val appResult = updateProfileUseCase(profileRequest)
            setState { copy(isLoading = false) }

            when (val result = appResult.toProfileUpdateResult()) {
                is ProfileUpdateResult.Success -> setEvent {
                    AccountEvent.ShowSnackbar(
                        SnackBarMessage(
                            message = UiText.Resource(Res.string.profile_update_success),
                            type = SnackBarType.SUCCESS
                        )
                    )
                }

                is ProfileUpdateResult.Error -> setEvent {
                    AccountEvent.ShowSnackbar(result.message)
                }
            }
        } catch {
            setState { copy(isLoading = false) }
            setEvent {
                AccountEvent.ShowSnackbar(
                    SnackBarMessage.error(UiText.Resource(Res.string.error_unknown_error))
                )
            }
        }
    }

    private fun ValidationResult<UiText>.errorOrNull(): UiText? {
        return (this as? ValidationResult.Invalid)?.error
    }
}
