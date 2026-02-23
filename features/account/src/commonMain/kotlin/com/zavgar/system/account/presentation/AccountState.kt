package com.zavgar.system.account.presentation

import com.zavgar.system.core.presentation.util.UiText
import kotlinx.datetime.LocalDate

data class AccountState(

    val name: String = "",
    val birthDate: LocalDate? = null,
    val birthDateText: String = "",

    val oldPassword: String = "",
    val newPassword: String = "",

    val nameError: UiText? = null,
    val birthDateError: UiText? = null,
    val oldPasswordError: UiText? = null,
    val newPasswordError: UiText? = null,

    val isDatePickerOpen: Boolean = false,

    val isPasswordDialogOpen: Boolean = false,
    val isPasswordDialogLoading: Boolean = false,

    val confirmDeleteDialog: Boolean = false,

    val isLoading: Boolean = false
)