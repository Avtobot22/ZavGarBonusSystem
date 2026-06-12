package com.zavgar.system.account.presentation

import com.zavgar.system.core.presentation.util.UiText
import kotlinx.datetime.LocalDate

data class AccountState(
    val screenState: ScreenState = ScreenState.Initial,

    val name: String = "",
    val phone: String = "",
    val birthDate: LocalDate? = null,
    val birthDateText: String = "",

    val nameError: UiText? = null,
    val birthDateError: UiText? = null,

    val isDatePickerOpen: Boolean = false,

    val confirmDeleteDialog: Boolean = false,
) {

    /** Идёт сетевая операция (обновление профиля / удаление аккаунта). */
    val isSubmitting: Boolean
        get() = screenState is ScreenState.Submitting

    sealed interface ScreenState {
        data object Initial : ScreenState
        data object Loading : ScreenState
        data object Content : ScreenState

        /** Контент уже показан, но идёт сетевая операция (submit/delete) — блокируем форму. */
        data object Submitting : ScreenState
        data object Error : ScreenState
    }
}
