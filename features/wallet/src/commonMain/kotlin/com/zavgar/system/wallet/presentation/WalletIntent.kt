package com.zavgar.system.wallet.presentation

sealed interface WalletIntent {

    data object RefreshBalance : WalletIntent

    data object Retry : WalletIntent

    data object PullToRefresh : WalletIntent

}