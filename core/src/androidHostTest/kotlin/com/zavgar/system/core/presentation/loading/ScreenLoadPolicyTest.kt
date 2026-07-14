@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.zavgar.system.core.presentation.loading

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

class ScreenLoadPolicyTest {

    private val clock = FakeClock(Instant.parse("2026-07-14T12:00:00Z"))

    @Test
    fun `automatic load starts when data has never been loaded`() {
        val policy = ScreenLoadPolicy(clock = clock)

        assertTrue(policy.canStartAutomaticLoad(activeJob = null))
    }

    @Test
    fun `successful load remains fresh for thirty seconds`() {
        val policy = ScreenLoadPolicy(clock = clock)

        policy.markSuccessfulLoad()
        clock.advanceBy(29.seconds)

        assertFalse(policy.canStartAutomaticLoad(activeJob = null))

        clock.advanceBy(1.seconds)

        assertTrue(policy.canStartAutomaticLoad(activeJob = null))
    }

    @Test
    fun `active job prevents automatic load even when data is stale`() {
        val policy = ScreenLoadPolicy(clock = clock)
        val activeJob = Job()

        assertFalse(policy.canStartAutomaticLoad(activeJob))

        activeJob.complete()

        assertTrue(policy.canStartAutomaticLoad(activeJob))
    }

    @Test
    fun `forced load bypasses freshness but not an active job`() {
        val policy = ScreenLoadPolicy(clock = clock)
        val activeJob = Job()
        policy.markSuccessfulLoad()

        assertTrue(policy.canStartForcedLoad(activeJob = null))
        assertFalse(policy.canStartForcedLoad(activeJob))
    }

    @Test
    fun `nullable block result is distinct from timeout`() = runTest {
        val policy = ScreenLoadPolicy(clock = clock)

        val result = policy.executeWithTimeout<String?> { null }

        assertEquals(ScreenLoadExecutionResult.Completed(null), result)
    }

    @Test
    fun `execution times out after fifteen seconds of virtual time`() = runTest {
        val policy = ScreenLoadPolicy(clock = clock)

        val result = policy.executeWithTimeout {
            delay(30.seconds)
            "late value"
        }

        assertIs<ScreenLoadExecutionResult.TimedOut>(result)
        assertEquals(15_000L, currentTime)
    }

    @Test
    fun `outer coroutine timeout is not converted into policy timeout`() = runTest {
        val policy = ScreenLoadPolicy(clock = clock)

        assertFailsWith<kotlinx.coroutines.TimeoutCancellationException> {
            withTimeout(1.seconds) {
                policy.executeWithTimeout {
                    delay(30.seconds)
                }
            }
        }
        assertEquals(1_000L, currentTime)
    }

    private class FakeClock(
        private var currentInstant: Instant,
    ) : Clock {

        override fun now(): Instant = currentInstant

        fun advanceBy(duration: Duration) {
            currentInstant += duration
        }
    }
}
