package com.zavgar.system.wallet.presentation

import com.zavgar.system.core.presentation.util.SnackBarMessage

interface WalletEvent {

    data object NavigateToLogin : WalletEvent

    data class ShowSnackbar(val message: SnackBarMessage) : WalletEvent
}