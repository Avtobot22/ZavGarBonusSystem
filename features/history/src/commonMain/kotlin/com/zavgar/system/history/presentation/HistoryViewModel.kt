package com.zavgar.system.history.presentation

import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.SnackBarType
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.core.presentation.util.toDisplayString
import com.zavgar.system.domain.operations.model.TransactionsRequest
import com.zavgar.system.domain.operations.usecase.GetOperationsUseCase
import com.zavgar.system.firebase.analytics.AnalyticsEvent
import com.zavgar.system.firebase.analytics.AnalyticsTracker
import com.zavgar.system.history.mapper.toPresentation
import com.zavgar.system.history.mapper.toTransactionsResult
import com.zavgar.system.history.model.DatePickerType
import com.zavgar.system.history.model.History
import com.zavgar.system.history.model.TransactionsResult
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_invalid_date_range
import com.zavgar.system.resources.error_unknown_error
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

class HistoryViewModel(
    private val getOperationsUseCase: GetOperationsUseCase,
    private val analyticsTracker: AnalyticsTracker,
    clock: Clock = Clock.System,
) : BaseViewModel<HistoryState, HistoryIntent, HistoryEvent>(initialState(clock)) {

    private enum class LoadMode { FIRST_PAGE, REFRESH, NEXT_PAGE }

    private var loadedPages: Int = 0

    init {
        loadData(LoadMode.FIRST_PAGE)
    }

    override fun handleIntent(intent: HistoryIntent) {
        when (intent) {
            is HistoryIntent.OpenDatePicker -> handleOpenDatePicker(intent.type)
            is HistoryIntent.CloseDatePicker -> handleCloseDatePicker(intent.type, intent.date)
            is HistoryIntent.DismissDatePicker -> handleDismissDatePicker()
            is HistoryIntent.Refresh -> loadData(LoadMode.REFRESH)
            is HistoryIntent.LoadNextPage -> loadData(LoadMode.NEXT_PAGE)
            is HistoryIntent.Retry -> loadData(LoadMode.FIRST_PAGE)
        }
    }

    private fun handleOpenDatePicker(type: DatePickerType) = setState {
        copy(datePickerOpen = type)
    }

    private fun handleDismissDatePicker() = setState {
        copy(datePickerOpen = null)
    }

    private fun handleCloseDatePicker(type: DatePickerType, date: LocalDate) {
        val newStart = if (type == DatePickerType.START) date else currentState.periodStart
        val newEnd = if (type == DatePickerType.END) date else currentState.periodEnd

        if (newStart > newEnd) {
            setState { copy(datePickerOpen = null) }
            setEvent {
                HistoryEvent.ShowSnackbar(
                    SnackBarMessage(
                        message = UiText.Resource(Res.string.error_invalid_date_range),
                        type = SnackBarType.WARNING,
                    )
                )
            }
            return
        }

        setState {
            when (type) {
                DatePickerType.START -> copy(
                    periodStart = date,
                    periodStartText = date.toDisplayString(),
                    datePickerOpen = null,
                )

                DatePickerType.END -> copy(
                    periodEnd = date,
                    periodEndText = date.toDisplayString(),
                    datePickerOpen = null,
                )
            }
        }

        loadData(LoadMode.FIRST_PAGE)
    }

    private fun loadData(mode: LoadMode) {
        if (!canLoad(mode)) return
        applyLoadingState(mode)
        fetchPage(mode)
    }

    private fun canLoad(mode: LoadMode): Boolean = with(currentState) {
        when (mode) {
            LoadMode.FIRST_PAGE ->
                screenState !is HistoryState.ScreenState.Loading &&
                    screenState !is HistoryState.ScreenState.Reloading

            LoadMode.REFRESH -> !isRefreshing

            LoadMode.NEXT_PAGE ->
                !isLoadingNextPage && history.hasMore && history.nextCursor != null
        }
    }

    private fun applyLoadingState(mode: LoadMode) = setState {
        when (mode) {
            LoadMode.FIRST_PAGE -> copy(
                screenState = if (screenState is HistoryState.ScreenState.Content) {
                    HistoryState.ScreenState.Reloading
                } else {
                    HistoryState.ScreenState.Loading
                },
                history = History.EMPTY,
                isRefreshing = false,
                isLoadingNextPage = false,
            )

            LoadMode.REFRESH -> copy(
                isRefreshing = true,
                isLoadingNextPage = false,
            )

            LoadMode.NEXT_PAGE -> copy(isLoadingNextPage = true)
        }
    }

    private fun fetchPage(mode: LoadMode) {
        launchTry {
            val result = getOperationsUseCase(buildRequest(mode)).toTransactionsResult()
            applyResult(mode, result)
        } catch {
            applyError(mode, SnackBarMessage.error(UiText.Resource(Res.string.error_unknown_error)))
        }
    }

    private fun buildRequest(mode: LoadMode): TransactionsRequest = with(currentState) {
        TransactionsRequest(
            periodStart = periodStart,
            periodEnd = periodEnd,
            cursor = if (mode == LoadMode.NEXT_PAGE) history.nextCursor else null,
        )
    }

    private fun applyResult(mode: LoadMode, result: TransactionsResult) {
        when (result) {
            is TransactionsResult.Success -> {
                setState {
                    val baseItems =
                        if (mode == LoadMode.NEXT_PAGE) history.transactions else emptyList()
                    copy(
                        screenState = HistoryState.ScreenState.Content,
                        history = result.page.toPresentation(baseItems),
                        isRefreshing = false,
                        isLoadingNextPage = false,
                    )
                }
                if (mode == LoadMode.NEXT_PAGE) {
                    loadedPages++
                    analyticsTracker.log(AnalyticsEvent.HistoryLoadMore(page = loadedPages))
                } else {
                    loadedPages = 0
                    analyticsTracker.log(
                        AnalyticsEvent.HistoryViewed(itemsCount = currentState.history.transactions.size)
                    )
                }
            }

            is TransactionsResult.Error -> applyError(mode, result.message)
        }
    }

    private fun applyError(mode: LoadMode, message: SnackBarMessage) {
        setState {
            val showErrorScreen =
                mode == LoadMode.FIRST_PAGE && history.transactions.isEmpty()
            copy(
                screenState = if (showErrorScreen) {
                    HistoryState.ScreenState.Error
                } else {
                    HistoryState.ScreenState.Content
                },
                isRefreshing = false,
                isLoadingNextPage = false,
            )
        }
        setEvent { HistoryEvent.ShowSnackbar(message) }
    }

    private companion object {
        fun initialState(clock: Clock): HistoryState {
            val today = clock.todayIn(TimeZone.currentSystemDefault())
            val periodStart = LocalDate(year = today.year, month = today.month, day = 1)
            return HistoryState(
                periodStart = periodStart,
                periodEnd = today,
            )
        }
    }
}
