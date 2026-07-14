package com.zavgar.system.core.presentation.loading

import kotlinx.coroutines.Job
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

class ScreenLoadPolicy(
    private val clock: Clock = Clock.System,
    private val freshFor: Duration = DEFAULT_FRESH_FOR,
    private val timeout: Duration = DEFAULT_TIMEOUT,
) {

    private var lastSuccessfulLoadAt: Instant? = null

    init {
        require(freshFor >= Duration.ZERO) { "freshFor must not be negative" }
        require(timeout.isPositive()) { "timeout must be positive" }
    }

    fun canStartAutomaticLoad(activeJob: Job?): Boolean =
        activeJob?.isActive != true && !hasFreshData()

    fun canStartForcedLoad(activeJob: Job?): Boolean = activeJob?.isActive != true

    fun markSuccessfulLoad() {
        lastSuccessfulLoadAt = clock.now()
    }

    suspend fun <T> executeWithTimeout(block: suspend () -> T): ScreenLoadExecutionResult<T> =
        withTimeoutOrNull(timeout) {
            ScreenLoadExecutionResult.Completed(block())
        } ?: ScreenLoadExecutionResult.TimedOut

    private fun hasFreshData(): Boolean {
        val loadedAt = lastSuccessfulLoadAt ?: return false
        return clock.now() - loadedAt < freshFor
    }

    companion object {
        val DEFAULT_FRESH_FOR: Duration = 30.seconds
        val DEFAULT_TIMEOUT: Duration = 15.seconds
    }
}

sealed interface ScreenLoadExecutionResult<out T> {

    data class Completed<T>(val value: T) : ScreenLoadExecutionResult<T>

    data object TimedOut : ScreenLoadExecutionResult<Nothing>
}
