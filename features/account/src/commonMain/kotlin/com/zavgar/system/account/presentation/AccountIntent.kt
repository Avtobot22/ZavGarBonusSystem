package com.zavgar.system.account.presentation

import kotlinx.datetime.LocalDate

// TODO нужно же добавить диалог с подверждением удаления?
sealed interface AccountIntent {

    data class EnterName(val name: String) : AccountIntent

    data class EnterBirthDate(val birthDate: LocalDate) : AccountIntent

    data class EnterOldPassword(val oldPassword: String) : AccountIntent

    data class EnterNewPassword(val newPassword: String) : AccountIntent

    data object OpenDatePicker : AccountIntent

    data object CloseDatePicker : AccountIntent

    data object DismissDatePicker : AccountIntent

    data object OpenPasswordDialog : AccountIntent

    data object ClosePasswordDialog : AccountIntent

    data object DismissPasswordDialog : AccountIntent

    data object ClickBack : AccountIntent

    data object ClickDelete : AccountIntent

    data object DismissDeleteAccountDialog : AccountIntent

    data object ConfirmDeleteAccount : AccountIntent

    data object Submit : AccountIntent

    data object Retry : AccountIntent

}