package com.zavgar.system.navigationapi.controller

import androidx.compose.runtime.saveable.SaverScope
import com.zavgar.system.navigationapi.destination.AuthDestination
import com.zavgar.system.navigationapi.destination.Destination
import com.zavgar.system.navigationapi.destination.OnboardingDestination
import com.zavgar.system.navigationapi.destination.SettingsDestination
import com.zavgar.system.navigationapi.destination.SplashDestination
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for [NavBackStack].
 *
 * These tests use **real** production [Destination]s. The sealed [Destination] hierarchy lives in
 * `commonMain`, which is a different compilation unit than this `androidHostTest` source set, so
 * declaring local subclasses of the sealed interface is prohibited ("Extending sealed classes or
 * interfaces from a different module is prohibited"). Instead we pick resource-free destinations
 * that can be instantiated safely inside a plain JVM host test:
 *
 * - [AuthDestination.Login] / [AuthDestination.Register] — plain root destinations.
 * - [AuthDestination.Confirmation] — a data class, used to obtain several distinct "child" entries.
 * - [SettingsDestination.Profile] — the only resource-free [BottomBarVisible] destination, used
 *   wherever bottom-bar visibility must be asserted.
 * - [OnboardingDestination] / [SplashDestination] — extra resource-free destinations used as
 *   additional "tabs".
 *
 * [HomeDestination.Wallet/Settings/History] are deliberately avoided: their object initializers
 * resolve Compose `Res.drawable.*` / `Res.string.*` resources, which are unavailable in a pure JVM
 * host test.
 *
 * Key API fact that makes multi-stack testable without [TopLevel] markers: `NavBackStack<T>` and
 * `addTopLevel(destination: T)` accept any [Destination], not only the [TopLevel] marker. Stack
 * logic does not depend on the marker — `addTopLevel` sets `isBottomBarVisible = true` directly,
 * while `updateBackStack()` re-derives it from `last is BottomBarVisible`. Therefore we use
 * [SettingsDestination.Profile] (a [BottomBarVisible] destination) as the "active" tab whenever a
 * test asserts the bottom bar is visible.
 *
 * Assertions are made through the public surface ([NavBackStack.backStack],
 * [NavBackStack.topLevelKey], [NavBackStack.isBottomBarVisible], [NavBackStack.saver]).
 */
class NavBackStackTest {

    // region real destination fixtures

    /** Plain (non top-level) root destinations. */
    private val root: Destination = AuthDestination.Login
    private val rootSecond: Destination = AuthDestination.Register

    /**
     * "Tabs" pushed via [NavBackStack.addTopLevel]. [SettingsDestination.Profile] is
     * [BottomBarVisible] so it keeps the bottom bar visible after `addTopLevel`/`updateBackStack`.
     */
    private val tabA: Destination = SettingsDestination.Profile
    private val tabB: Destination = OnboardingDestination
    private val tabC: Destination = SplashDestination

    /** Plain child destinations pushed on top of a tab stack (distinct data-class instances). */
    private val childA1: Destination = AuthDestination.Confirmation(phone = "A1", isRegistration = false)
    private val childA2: Destination = AuthDestination.Confirmation(phone = "A2", isRegistration = true)
    private val childB1: Destination = AuthDestination.Confirmation(phone = "B1", isRegistration = false)

    /** A destination requesting the bottom bar to stay visible. */
    private val bottomBarVisibleScreen: Destination = SettingsDestination.Profile

    // endregion

    private fun newStack(start: Destination = root): NavBackStack<Destination> =
        NavBackStack(start)

    private fun NavBackStack<Destination>.snapshot(): List<Destination> = backStack.toList()

    // region initial state

    @Test
    fun `initial back stack contains only the start destination`() {
        val stack = newStack(root)

        assertEquals(listOf(root), stack.snapshot())
    }

    @Test
    fun `initial top level key is null`() {
        assertNull(newStack().topLevelKey)
    }

    @Test
    fun `bottom bar is hidden initially`() {
        assertFalse(newStack().isBottomBarVisible)
    }

    // endregion

    // region root stack add / removeLast

    @Test
    fun `add pushes onto root stack when no top level is active`() {
        val stack = newStack(root)

        stack.add(rootSecond)

        assertEquals(listOf(root, rootSecond), stack.snapshot())
        assertNull(stack.topLevelKey)
    }

    @Test
    fun `add increases stack size and preserves order`() {
        val stack = newStack(root)

        stack.add(rootSecond)
        stack.add(childA1)

        assertEquals(listOf(root, rootSecond, childA1), stack.snapshot())
    }

    @Test
    fun `removeLast pops the top of the root stack`() {
        val stack = newStack(root)
        stack.add(rootSecond)

        stack.removeLast()

        assertEquals(listOf(root), stack.snapshot())
    }

    @Test
    fun `removeLast keeps the last remaining root destination`() {
        val stack = newStack(root)

        stack.removeLast()

        assertEquals(listOf(root), stack.snapshot())
    }

    @Test
    fun `removeLast on an already single root stack is idempotent`() {
        val stack = newStack(root)

        repeat(3) { stack.removeLast() }

        assertEquals(listOf(root), stack.snapshot())
    }

    // endregion

    // region addTopLevel (bottom navigation tabs)

    @Test
    fun `addTopLevel clears root stack and activates the tab`() {
        val stack = newStack(root)
        stack.add(rootSecond)

        stack.addTopLevel(tabA)

        assertEquals(tabA, stack.topLevelKey)
        assertEquals(listOf(tabA), stack.snapshot())
        assertTrue(stack.isBottomBarVisible)
    }

    @Test
    fun `addTopLevel keeps a separate stack per tab`() {
        val stack = newStack(root)

        stack.addTopLevel(tabA)
        stack.add(childA1)
        stack.addTopLevel(tabB)
        stack.add(childB1)

        // Both tab stacks are retained and flattened in insertion order.
        assertEquals(
            listOf(tabA, childA1, tabB, childB1),
            stack.snapshot(),
        )
        assertEquals(tabB, stack.topLevelKey)
    }

    @Test
    fun `re-selecting an existing tab restores its own sub-stack and moves it to the end`() {
        val stack = newStack(root)

        stack.addTopLevel(tabA)
        stack.add(childA1)
        stack.add(childA2)
        stack.addTopLevel(tabB)

        // Returning to A must not reset A's children, and A is re-inserted last.
        stack.addTopLevel(tabA)

        assertEquals(tabA, stack.topLevelKey)
        assertEquals(
            listOf(tabB, tabA, childA1, childA2),
            stack.snapshot(),
        )
    }

    @Test
    fun `re-selecting the current tab does not duplicate its destinations`() {
        val stack = newStack(root)
        stack.addTopLevel(tabA)
        stack.add(childA1)

        stack.addTopLevel(tabA)

        assertEquals(listOf(tabA, childA1), stack.snapshot())
        assertEquals(tabA, stack.topLevelKey)
    }

    @Test
    fun `add pushes onto the active tab stack when a top level is active`() {
        val stack = newStack(root)
        stack.addTopLevel(tabA)

        stack.add(childA1)

        assertEquals(listOf(tabA, childA1), stack.snapshot())
        assertEquals(tabA, stack.topLevelKey)
    }

    // endregion

    // region removeLast across tabs

    @Test
    fun `removeLast pops within the active tab before touching the tab itself`() {
        val stack = newStack(root)
        stack.addTopLevel(tabA)
        stack.add(childA1)

        stack.removeLast()

        assertEquals(listOf(tabA), stack.snapshot())
        assertEquals(tabA, stack.topLevelKey)
    }

    @Test
    fun `removeLast on a single-entry tab removes the tab and activates the previous one`() {
        val stack = newStack(root)
        stack.addTopLevel(tabA)
        stack.addTopLevel(tabB)

        // tabB has only its root entry, so removing falls through to dropping the tab.
        stack.removeLast()

        assertEquals(tabA, stack.topLevelKey)
        assertEquals(listOf(tabA), stack.snapshot())
    }

    @Test
    fun `removeLast keeps the last remaining tab`() {
        val stack = newStack(root)
        stack.addTopLevel(tabA)

        stack.removeLast()

        // Only one tab and it has a single entry: nothing is removed.
        assertEquals(tabA, stack.topLevelKey)
        assertEquals(listOf(tabA), stack.snapshot())
    }

    @Test
    fun `removeLast unwinds nested children then tabs in reverse insertion order`() {
        val stack = newStack(root)
        stack.addTopLevel(tabA)
        stack.addTopLevel(tabB)
        stack.add(childB1)

        // 1. pop child of B
        stack.removeLast()
        assertEquals(listOf(tabA, tabB), stack.snapshot())
        assertEquals(tabB, stack.topLevelKey)

        // 2. B has only its root left -> drop tab B, activate A
        stack.removeLast()
        assertEquals(listOf(tabA), stack.snapshot())
        assertEquals(tabA, stack.topLevelKey)

        // 3. only A left with a single entry -> no further change
        stack.removeLast()
        assertEquals(listOf(tabA), stack.snapshot())
        assertEquals(tabA, stack.topLevelKey)
    }

    // endregion

    // region clearTopLevel

    @Test
    fun `clearTopLevel resets a tab sub-stack back to its root`() {
        val stack = newStack(root)
        stack.addTopLevel(tabA)
        stack.add(childA1)
        stack.add(childA2)

        stack.clearTopLevel(tabA)

        assertEquals(listOf(tabA), stack.snapshot())
        assertEquals(tabA, stack.topLevelKey)
    }

    @Test
    fun `clearTopLevel only affects the targeted tab`() {
        val stack = newStack(root)
        stack.addTopLevel(tabA)
        stack.add(childA1)
        stack.addTopLevel(tabB)
        stack.add(childB1)

        stack.clearTopLevel(tabA)

        assertEquals(
            listOf(tabA, tabB, childB1),
            stack.snapshot(),
        )
        assertEquals(tabB, stack.topLevelKey)
    }

    @Test
    fun `clearTopLevel on an unknown tab is a no-op`() {
        val stack = newStack(root)
        stack.addTopLevel(tabA)
        stack.add(childA1)

        stack.clearTopLevel(tabC)

        assertEquals(listOf(tabA, childA1), stack.snapshot())
        assertEquals(tabA, stack.topLevelKey)
    }

    // endregion

    // region replaceTop

    @Test
    fun `replaceTop swaps the current root destination`() {
        val stack = newStack(root)

        stack.replaceTop(rootSecond)

        assertEquals(listOf(rootSecond), stack.snapshot())
        assertNull(stack.topLevelKey)
    }

    @Test
    fun `replaceTop on a deeper root stack only swaps the top`() {
        val stack = newStack(root)
        stack.add(rootSecond)

        stack.replaceTop(childA1)

        assertEquals(listOf(root, childA1), stack.snapshot())
    }

    @Test
    fun `replaceTop swaps the top of the active tab stack`() {
        val stack = newStack(root)
        stack.addTopLevel(tabA)
        stack.add(childA1)

        stack.replaceTop(childA2)

        assertEquals(listOf(tabA, childA2), stack.snapshot())
        assertEquals(tabA, stack.topLevelKey)
    }

    // endregion

    // region clearAndNavigate / clearAndNavigateToTopLevel

    @Test
    fun `clearAndNavigate resets everything to a single root destination`() {
        val stack = newStack(root)
        stack.addTopLevel(tabA)
        stack.add(childA1)
        stack.addTopLevel(tabB)

        stack.clearAndNavigate(rootSecond)

        assertEquals(listOf(rootSecond), stack.snapshot())
        assertNull(stack.topLevelKey)
    }

    @Test
    fun `clearAndNavigateToTopLevel drops auth flow and starts a fresh tab`() {
        val stack = newStack(root)
        stack.add(rootSecond)

        stack.clearAndNavigateToTopLevel(tabA)

        assertEquals(listOf(tabA), stack.snapshot())
        assertEquals(tabA, stack.topLevelKey)
        assertTrue(stack.isBottomBarVisible)
    }

    @Test
    fun `clearAndNavigateToTopLevel discards previously opened tabs`() {
        val stack = newStack(root)
        stack.addTopLevel(tabA)
        stack.add(childA1)
        stack.addTopLevel(tabB)

        stack.clearAndNavigateToTopLevel(tabC)

        assertEquals(listOf(tabC), stack.snapshot())
        assertEquals(tabC, stack.topLevelKey)
    }

    // endregion

    // region bottom bar visibility

    @Test
    fun `bottom bar stays hidden on a plain root destination`() {
        val stack = newStack(root)
        stack.add(rootSecond)

        assertFalse(stack.isBottomBarVisible)
    }

    @Test
    fun `bottom bar becomes visible for a BottomBarVisible destination on the root stack`() {
        val stack = newStack(root)

        stack.add(bottomBarVisibleScreen)

        assertTrue(stack.isBottomBarVisible)
    }

    @Test
    fun `bottom bar visibility follows the current destination after pop`() {
        val stack = newStack(root)
        stack.add(bottomBarVisibleScreen)
        assertTrue(stack.isBottomBarVisible)

        stack.removeLast()

        assertFalse(stack.isBottomBarVisible)
    }

    @Test
    fun `pushing a plain destination on top of a tab hides the bottom bar`() {
        val stack = newStack(root)
        stack.addTopLevel(tabA)
        assertTrue(stack.isBottomBarVisible)

        // childA1 is a plain Destination (not BottomBarVisible).
        stack.add(childA1)

        assertFalse(stack.isBottomBarVisible)
    }

    // endregion

    // region saver round-trip

    private fun saveAndRestore(stack: NavBackStack<Destination>): NavBackStack<Destination> {
        val saver = NavBackStack.saver<Destination>(root)
        // `Saver.save` is a member with a `SaverScope` extension receiver, so it needs both the
        // saver as the dispatch receiver (`with(saver)`) and a `SaverScope` as the receiver of the
        // call. `SaverScope { true }` is the `canBeSaved` lambda (always true in tests).
        val saved = with(saver) { SaverScope { true }.save(stack) }
        requireNotNull(saved) { "Saver returned null" }
        return requireNotNull(saver.restore(saved)) { "Saver restored null" }
    }

    @Test
    fun `saver round-trips a fresh root stack`() {
        val original = newStack(root)

        val restored = saveAndRestore(original)

        assertEquals(original.snapshot(), restored.snapshot())
        assertEquals(original.topLevelKey, restored.topLevelKey)
    }

    @Test
    fun `saver round-trips a deep root stack`() {
        val original = newStack(root)
        original.add(rootSecond)
        original.add(childA1)

        val restored = saveAndRestore(original)

        assertEquals(
            listOf(root, rootSecond, childA1),
            restored.snapshot(),
        )
        assertNull(restored.topLevelKey)
    }

    @Test
    fun `saver round-trips multiple tab stacks preserving tab order and children`() {
        val original = newStack(root)
        original.addTopLevel(tabA)
        original.add(childA1)
        original.add(childA2)
        original.addTopLevel(tabB)
        original.add(childB1)

        val restored = saveAndRestore(original)

        assertEquals(original.snapshot(), restored.snapshot())
        assertEquals(
            listOf(tabA, childA1, childA2, tabB, childB1),
            restored.snapshot(),
        )
        assertEquals(tabB, restored.topLevelKey)
    }

    @Test
    fun `saver preserves the active top level key`() {
        val original = newStack(root)
        original.addTopLevel(tabA)
        original.addTopLevel(tabB)
        original.addTopLevel(tabA) // re-activate A

        val restored = saveAndRestore(original)

        assertEquals(tabA, restored.topLevelKey)
        assertEquals(original.snapshot(), restored.snapshot())
    }

    @Test
    fun `restored stack keeps behaving correctly`() {
        val original = newStack(root)
        original.addTopLevel(tabA)
        original.add(childA1)
        original.addTopLevel(tabB)

        val restored = saveAndRestore(original)

        // Continue navigating on the restored instance.
        restored.add(childB1)
        assertEquals(
            listOf(tabA, childA1, tabB, childB1),
            restored.snapshot(),
        )

        restored.removeLast()
        assertEquals(listOf(tabA, childA1, tabB), restored.snapshot())
        assertEquals(tabB, restored.topLevelKey)
    }

    @Test
    fun `saver round-trips bottom bar visibility through the restored destination`() {
        val original = newStack(root)
        original.add(bottomBarVisibleScreen)
        assertTrue(original.isBottomBarVisible)

        val restored = saveAndRestore(original)

        // restore() rebuilds backStack and re-derives bottom bar visibility from the top entry.
        assertTrue(restored.isBottomBarVisible)
        assertEquals(original.snapshot(), restored.snapshot())
    }

    // endregion
}
