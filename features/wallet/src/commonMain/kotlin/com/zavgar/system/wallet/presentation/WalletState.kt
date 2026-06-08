package com.zavgar.system.wallet.presentation

data class WalletState(
    val screenState: ScreenState = ScreenState.Initial,
    val phone: String = "",
    val monthlyEarned: Int? = null,
) {

    sealed interface ScreenState {
        data object Initial : ScreenState
        data object Loading : ScreenState

        data class Content(
            val balance: Int = 0,
            val timerSeconds: Int = 0,
            val isRefreshing: Boolean = false,
            // Время последнего успешного обновления (epoch millis) для баннера устаревших данных
            val lastUpdatedMillis: Long? = null,
            // true, когда показываем кэш, а свежий запрос упал
            val isStale: Boolean = false,
        ) : ScreenState

        data object Error : ScreenState

        data object Offline : ScreenState
    }
}
