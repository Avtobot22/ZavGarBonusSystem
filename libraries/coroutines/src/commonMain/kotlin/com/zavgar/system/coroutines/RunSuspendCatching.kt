package com.zavgar.system.coroutines

import kotlinx.coroutines.CancellationException

// Deliberate catch-all (Result wrapper); CancellationException is rethrown to keep structured concurrency.
@Suppress("TooGenericExceptionCaught")
inline fun <R> runSuspendCatching(block: () -> R): Result<R> = try {
    Result.success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Throwable) {
    Result.failure(e)
}
