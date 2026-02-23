package com.zavgar.system.wallet.presentation

data class WalletState(
    val screenState: ScreenState = ScreenState.Loading,
    val phone: String = "",
    val balance: Int = 0,
    val timerSeconds: Int = 0,
    val isRefreshing: Boolean = false
) {

    enum class ScreenState {
        Loading,
        Content
    }
}