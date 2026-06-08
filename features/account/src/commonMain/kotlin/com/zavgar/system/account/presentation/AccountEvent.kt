package com.zavgar.system.account.presentation

import com.zavgar.system.core.presentation.util.SnackBarMessage

sealed interface AccountEvent {

    /**
     * Аккаунт удалён: показываем success-снэкбар на текущем экране и только
     * после того, как пользователь успел его увидеть, уходим на логин.
     *
     * Раздельные [ShowSnackbar] + навигация здесь не годятся: целевой экран
     * снэкбара исчезает мгновенно при навигации, и сообщение не успевает
     * отобразиться (гонка).
     */
    data class DeleteAccountSuccess(val message: SnackBarMessage) : AccountEvent

    data object NavigateBack : AccountEvent

    data class ShowSnackbar(val message: SnackBarMessage) : AccountEvent
}
