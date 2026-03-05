package com.zavgar.system.history.presentation

import androidx.lifecycle.viewModelScope
import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.SnackBarType
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.core.presentation.util.toDisplayString
import com.zavgar.system.domain.usecase.DeleteSessionUseCase
import com.zavgar.system.domain.usecase.GetOperationsUseCase
import com.zavgar.system.history.mapper.toDomain
import com.zavgar.system.history.mapper.toPresentation
import com.zavgar.system.history.mapper.toTransactionsResult
import com.zavgar.system.history.model.DatePickerType
import com.zavgar.system.history.model.TransactionsRequest
import com.zavgar.system.history.model.TransactionsResult
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_invalid_date_range
import com.zavgar.system.resources.error_unknown_error
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class HistoryViewModel(
    private val getOperationsUseCase: GetOperationsUseCase,
    private val deleteSessionUseCase: DeleteSessionUseCase
) : BaseViewModel<HistoryState, HistoryIntent, HistoryEvent>(HistoryState()) {

    init {
        initData()
    }

    override fun handleIntent(intent: HistoryIntent) {
        when (intent) {
            is HistoryIntent.OpenDatePicker -> handleOpenDatePicker(intent.type)
            is HistoryIntent.CloseDatePicker -> handleCloseDatePicker(intent.type, intent.date)
            is HistoryIntent.DismissDatePicker -> handleDismissDatePicker()
            is HistoryIntent.Refresh -> handleRefresh()
            is HistoryIntent.LoadNextPage -> handleLoadNextPage()
            is HistoryIntent.Retry -> handleRetry()
        }
    }

    private fun handleCloseDatePicker(type: DatePickerType, date: LocalDate) {
        val currentStart = currentState.periodStart
        val currentEnd = currentState.periodEnd

        val newStart = if (type == DatePickerType.START) date else currentStart
        val newEnd = if (type == DatePickerType.END) date else currentEnd

        if (newStart > newEnd) {
            setEvent {
                HistoryEvent.ShowSnackbar(
                    SnackBarMessage(
                        message = UiText.Resource(Res.string.error_invalid_date_range),
                        type = SnackBarType.WARNING
                    )
                )
            }
            setState { copy(datePickerOpen = null) }
            return
        }

        when (type) {
            DatePickerType.START -> setState {
                copy(
                    periodStart = date,
                    periodStartText = date.toDisplayString()
                )
            }

            DatePickerType.END -> setState {
                copy(
                    periodEnd = date,
                    periodEndText = date.toDisplayString()
                )
            }
        }

        setState { copy(datePickerOpen = null) }

        loadData(isRefreshing = false, isFirstPage = true)
    }

    private fun loadData(isRefreshing: Boolean = false, isFirstPage: Boolean) {
        val state = currentState

        val isLoading = state.screenState is HistoryState.ScreenState.Loading
                || state.screenState is HistoryState.ScreenState.Reloading

        if (isFirstPage && !isRefreshing && isLoading) return

        if (isRefreshing && state.isRefreshing) return

        if (!isFirstPage && (state.isLoadingNextPage || !state.hasMore || state.nextCursor == null)) return

        setState {
            copy(
                screenState = if (isFirstPage && !isRefreshing) {
                    if (screenState is HistoryState.ScreenState.Content) {
                        HistoryState.ScreenState.Reloading
                    } else {
                        HistoryState.ScreenState.Loading
                    }
                } else screenState,
                isRefreshing = isRefreshing,
                isLoadingNextPage = !isFirstPage,
                history = if (isFirstPage && !isRefreshing) emptyList() else history
            )
        }

        viewModelScope.launch {

            val state = currentState
            val request = TransactionsRequest(
                periodStart = state.periodStart,
                periodEnd = state.periodEnd,
                cursor = if (isFirstPage) null else state.nextCursor,
            )

            val historyItems = if (!isFirstPage) state.history else emptyList()

            val result =
                getOperationsUseCase(request.toDomain()).toTransactionsResult { it.toPresentation(historyItems) }

            setState { copy(isRefreshing = false, isLoadingNextPage = false) }

            when (result) {
                is TransactionsResult.Success -> setState {
                    copy(
                        screenState = HistoryState.ScreenState.Content,
                        history = result.history.transactions,
                        nextCursor = result.history.nextCursor,
                        hasMore = result.history.hasMore,
                    )
                }

                is TransactionsResult.Error -> {
                    if (isFirstPage && !isRefreshing && currentState.history.isEmpty()) {
                        setState { copy(screenState = HistoryState.ScreenState.Error) }
                        setEvent { HistoryEvent.ShowSnackbar(result.message) }
                    } else {
                        setState { copy(screenState = HistoryState.ScreenState.Content) }
                        setEvent { HistoryEvent.ShowSnackbar(result.message) }
                    }
                }

                is TransactionsResult.TokenExpired -> handleLogout()
            }
        }

    }

    private fun handleOpenDatePicker(type: DatePickerType) = setState {
        copy(datePickerOpen = type)
    }

    private fun handleDismissDatePicker() = setState {
        copy(datePickerOpen = null)
    }

    private fun handleRefresh() {
        loadData(isRefreshing = true, isFirstPage = true)
    }

    private fun handleRetry() {
        loadData(isRefreshing = false, isFirstPage = true)
    }

    private fun initData() {
        loadData(isRefreshing = false, isFirstPage = true)
    }

    private fun handleLoadNextPage() {
        loadData(isRefreshing = false, isFirstPage = false)
    }

    private fun handleLogout() {
        viewModelScope.launch {
            val result = deleteSessionUseCase()
            result.getOrElse { exception ->
                setEvent {
                    HistoryEvent.ShowSnackbar(
                        SnackBarMessage(
                            message = exception.message?.let {
                                UiText.DynamicString(it)
                            } ?: UiText.Resource(Res.string.error_unknown_error),
                            type = SnackBarType.ERROR
                        )
                    )
                }
            }
        }

        setEvent { HistoryEvent.Logout }
    }
}