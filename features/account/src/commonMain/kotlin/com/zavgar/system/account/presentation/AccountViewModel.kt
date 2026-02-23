package com.zavgar.system.account.presentation

import androidx.lifecycle.viewModelScope
import com.zavgar.system.account.mapper.toChangePasswordResult
import com.zavgar.system.account.mapper.toDeleteResult
import com.zavgar.system.account.mapper.toDomain
import com.zavgar.system.account.mapper.toProfileGetResult
import com.zavgar.system.account.mapper.toProfileUpdateResult
import com.zavgar.system.account.model.ChangePasswordRequest
import com.zavgar.system.account.model.ChangePasswordResult
import com.zavgar.system.account.model.DeleteResult
import com.zavgar.system.account.model.ProfileGetResult
import com.zavgar.system.account.model.ProfileRequest
import com.zavgar.system.account.model.ProfileUpdateResult
import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.core.presentation.util.toDisplayString
import com.zavgar.system.domain.usecase.ChangePasswordUseCase
import com.zavgar.system.domain.usecase.DeleteProfileUseCase
import com.zavgar.system.domain.usecase.DeleteSessionUseCase
import com.zavgar.system.domain.usecase.GetProfileUseCase
import com.zavgar.system.domain.usecase.UpdateProfileUseCase
import com.zavgar.system.domain.usecase.validation.ValidateBirthDateUseCase
import com.zavgar.system.domain.usecase.validation.ValidateNameUseCase
import com.zavgar.system.domain.usecase.validation.ValidatePasswordUseCase
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_unknown_error
import com.zavgar.system.resources.password_update_error
import com.zavgar.system.resources.password_update_success
import com.zavgar.system.resources.profile_delete_success
import com.zavgar.system.resources.profile_update_success
import com.zavgar.system.sharedValidation.ValidationResult
import com.zavgar.system.sharedValidation.asUiText
import com.zavgar.system.sharedValidation.toPresentation
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class AccountViewModel(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val deleteProfileUseCase: DeleteProfileUseCase,
    private val deleteSessionUseCase: DeleteSessionUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
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
            is AccountIntent.EnterOldPassword -> handleEnterOldPassword(intent.oldPassword)
            is AccountIntent.EnterNewPassword -> handleEnterNewPassword(intent.newPassword)
            is AccountIntent.ClickBack -> handleClickBack()
            is AccountIntent.ClickDelete -> handleClickDelete()
            is AccountIntent.DismissDeleteAccountDialog -> handleDismissDeleteAccountDialog()
            is AccountIntent.ConfirmDeleteAccount -> handleConfirmDeleteAccount()
            is AccountIntent.OpenDatePicker -> handleOpenDatePicker()
            is AccountIntent.CloseDatePicker -> handleCloseDatePicker()
            is AccountIntent.DismissDatePicker -> handleDismissDatePicker()
            is AccountIntent.OperPasswordDialog -> handleOpenPasswordDialog()
            is AccountIntent.ClosePasswordDialog -> handleClosePasswordDialog()
            is AccountIntent.DismissPasswordDialog -> handleDismissPasswordDialog()
            is AccountIntent.Submit -> handleSubmit()
        }
    }

    private fun initProfile() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }

            when (val result = getProfileUseCase().toProfileGetResult()) {
                is ProfileGetResult.Success ->
                    setState {
                        copy(
                            name = result.profileResponse.name,
                            birthDate = result.profileResponse.birthDate,
                            birthDateText = result.profileResponse.birthDate.toDisplayString()
                        )
                    }

                is ProfileGetResult.Error -> setEvent { AccountEvent.ShowSnackbar(result.message) }
                is ProfileGetResult.TokenExpired -> handleLogout()
            }

            setState { copy(isLoading = false) }
        }
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

    private fun handleEnterOldPassword(password: String) = setState {
        copy(
            oldPassword = password,
            oldPasswordError = null
        )
    }

    private fun handleEnterNewPassword(repeatPassword: String) = setState {
        copy(
            newPassword = repeatPassword,
            newPasswordError = null
        )
    }

    private fun handleOpenDatePicker() = setState { copy(isDatePickerOpen = true) }

    private fun handleCloseDatePicker() = setState { copy(isDatePickerOpen = false) }

    private fun handleDismissDatePicker() = setState { copy(isDatePickerOpen = false) }

    private fun handleOpenPasswordDialog() = setState {
        copy(
            isPasswordDialogOpen = true,
            oldPassword = "",
            newPassword = "",
            oldPasswordError = null,
            newPasswordError = null,
            isPasswordDialogLoading = false
        )
    }

    private fun handleClosePasswordDialog() {
        if (currentState.isPasswordDialogLoading) return
        val state = currentState

        val passwordResult = validatePasswordUseCase(state.oldPassword).toPresentation { it.asUiText() }
        val repeatPasswordResult = validatePasswordUseCase(state.newPassword).toPresentation { it.asUiText() }

        setState {
            copy(
                oldPasswordError = passwordResult.errorOrNull(),
                newPasswordError = repeatPasswordResult.errorOrNull()
            )
        }

        if (passwordResult is ValidationResult.Error || repeatPasswordResult is ValidationResult.Error) return

        viewModelScope.launch {
            setState { copy(isPasswordDialogLoading = true) }

            val result = changePasswordUseCase(
                ChangePasswordRequest(
                    state.oldPassword,
                    state.newPassword
                ).toDomain()
            ).toChangePasswordResult()

            setState { copy(isPasswordDialogLoading = false) }

            when (result) {
                is ChangePasswordResult.Success -> {
                    setEvent { AccountEvent.ShowSnackbar(UiText.Resource(Res.string.password_update_success)) }
                    setState { copy(isPasswordDialogOpen = false) }
                }

                is ChangePasswordResult.Error -> setEvent { AccountEvent.ShowSnackbar(UiText.Resource(Res.string.password_update_error)) }
                is ChangePasswordResult.TokenExpired -> handleLogout()
            }
        }
    }

    private fun handleDismissPasswordDialog() = setState { copy(isPasswordDialogOpen = false) }

    private fun handleClickBack() = setEvent { AccountEvent.NavigateBack }

    private fun handleClickDelete() = setState { copy(confirmDeleteDialog = true) }

    private fun handleDismissDeleteAccountDialog() = setState { copy(confirmDeleteDialog = false) }

    private fun handleConfirmDeleteAccount() {
        if (currentState.isLoading) return

        viewModelScope.launch {
            setState { copy(isLoading = true) }

            when (val result = deleteProfileUseCase().toDeleteResult()) {
                is DeleteResult.Success -> {
                    setEvent { AccountEvent.ShowSnackbar(UiText.Resource(Res.string.profile_delete_success)) }
                    setEvent { AccountEvent.NavigateToLogin }
                }

                is DeleteResult.Error -> setEvent { AccountEvent.ShowSnackbar(result.message) }
                is DeleteResult.TokenExpired -> handleLogout()
            }

            setState { copy(isLoading = false) }


        }

        setEvent { AccountEvent.NavigateToLogin }
    }

    private fun handleSubmit() {
        if (currentState.isLoading) return

        val state = currentState

        val nameResult = validateNameUseCase(state.name).toPresentation { it.asUiText() }
        val birthDateResult =
            validateBirthDateUseCase(state.birthDate).toPresentation { it.asUiText() }

        val isFormValid = formValidation(
            nameResult,
            birthDateResult,
        )

        if (!isFormValid) return

        val birthDate = state.birthDate ?: return

        performUpdate(
            ProfileRequest(
                name = state.name,
                birthDate = birthDate
            )
        )

    }

    private fun performUpdate(profileRequest: ProfileRequest) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }

            val result = updateProfileUseCase(profileRequest.toDomain()).toProfileUpdateResult()

            setState { copy(isLoading = false) }

            when (result) {
                is ProfileUpdateResult.Success -> setEvent { AccountEvent.ShowSnackbar(UiText.Resource(Res.string.profile_update_success)) }
                is ProfileUpdateResult.Error -> setEvent { AccountEvent.ShowSnackbar(result.message) }
                is ProfileUpdateResult.TokenExpired -> setEvent { AccountEvent.NavigateToLogin }
            }
        }
    }

    private fun handleLogout() {
        viewModelScope.launch {
            val result = deleteSessionUseCase()
            result.getOrElse { exception ->
                setEvent {
                    AccountEvent.ShowSnackbar(exception.message?.let {
                        UiText.DynamicString(it)
                    } ?: UiText.Resource(Res.string.error_unknown_error))
                }
            }
        }

        setEvent { AccountEvent.NavigateToLogin }
    }

    private fun formValidation(
        nameResult: ValidationResult<UiText>,
        birthDateResult: ValidationResult<UiText>,
    ): Boolean {
        val hasErrors = listOf(
            nameResult,
            birthDateResult
        ).any { it is ValidationResult.Error }

        setState {
            copy(
                nameError = nameResult.errorOrNull(),
                birthDateError = birthDateResult.errorOrNull()
            )
        }

        return !hasErrors
    }

    private fun ValidationResult<UiText>.errorOrNull(): UiText? {
        return (this as? ValidationResult.Error)?.error
    }
}