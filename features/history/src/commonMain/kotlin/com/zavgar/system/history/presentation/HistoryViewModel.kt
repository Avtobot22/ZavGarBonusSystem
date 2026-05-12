package com.zavgar.system.history.presentation

import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.SnackBarType
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.core.presentation.util.toDisplayString
import com.zavgar.system.domain.operations.usecase.GetOperationsUseCase
import com.zavgar.system.history.mapper.toPresentation
import com.zavgar.system.history.mapper.toTransactionsResult
import com.zavgar.system.history.model.DatePickerType
import com.zavgar.system.domain.operations.model.TransactionsRequest
import com.zavgar.system.history.model.TransactionsResult
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_invalid_date_range
import com.zavgar.system.resources.error_unknown_error
import kotlinx.datetime.LocalDate

class HistoryViewModel(
    private val getOperationsUseCase: GetOperationsUseCase,
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

        if (isFirstPage && !canLoadFirstPage(state, isRefreshing)) return
        if (isRefreshing && state.isRefreshing) return
        if (!isFirstPage && !canLoadNextPage(state)) return

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

        launchTry {

            val state = currentState
            val request = TransactionsRequest(
                periodStart = state.periodStart,
                periodEnd = state.periodEnd,
                cursor = if (isFirstPage) null else state.nextCursor,
            )

            val historyItems = if (!isFirstPage) state.history else emptyList()

            val appResult = getOperationsUseCase(request)
            setState { copy(isRefreshing = false, isLoadingNextPage = false) }

            when (val result = appResult.toTransactionsResult { it.toPresentation(historyItems) }) {
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
            }
        } catch {
            setState { copy(isRefreshing = false, isLoadingNextPage = false) }
            if (isFirstPage && !isRefreshing && currentState.history.isEmpty()) {
                setState { copy(screenState = HistoryState.ScreenState.Error) }
            } else {
                setState { copy(screenState = HistoryState.ScreenState.Content) }
            }
            setEvent {
                HistoryEvent.ShowSnackbar(
                    SnackBarMessage.error(UiText.Resource(Res.string.error_unknown_error))
                )
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

    private fun canLoadFirstPage(state: HistoryState, isRefreshing: Boolean): Boolean {
        val isAlreadyLoading = state.screenState is HistoryState.ScreenState.Loading ||
                state.screenState is HistoryState.ScreenState.Reloading
        return !(isAlreadyLoading && !isRefreshing)
    }

    private fun canLoadNextPage(state: HistoryState): Boolean =
        !state.isLoadingNextPage && state.hasMore && state.nextCursor != null
}