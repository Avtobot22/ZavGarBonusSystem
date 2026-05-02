package com.zavgar.system.navigationapi.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.zavgar.system.navigationapi.destination.Destination
import com.zavgar.system.navigationapi.marker.BottomBarVisible


/**
 * Controller to manage the navigation back stack.
 *
 * @param T the type of [Destination] managed in the back stack
 * @param startDestination the starting destination
 */
class NavBackStack<T : Destination>(startDestination: T) {

    private val topLevelStacks: LinkedHashMap<T, SnapshotStateList<T>> = linkedMapOf()

    var topLevelKey: T? by mutableStateOf(null)
        private set

    val backStack: SnapshotStateList<T> = mutableStateListOf(startDestination)

    // Отдельный стек для non-TopLevel экранов (Auth flow, Splash и т.д.)
    private val rootStack: SnapshotStateList<T> = mutableStateListOf(startDestination)

    /**
     * Restores internal state from saved data.
     * Called by [Saver] during state restoration (e.g. after configuration change).
     */
    internal fun restore(
        savedRootStack: List<T>,
        savedTopLevelStacks: List<Pair<T, List<T>>>,
        savedTopLevelKey: T?,
    ) {
        rootStack.clear()
        rootStack.addAll(savedRootStack)

        topLevelStacks.clear()
        savedTopLevelStacks.forEach { (key, stack) ->
            topLevelStacks[key] = mutableStateListOf<T>().apply { addAll(stack) }
        }

        topLevelKey = savedTopLevelKey

        updateBackStack()
    }

    companion object {

        /**
         * Creates a [Saver] for [NavBackStack] that preserves navigation state across
         * configuration changes and process death.
         *
         * All [Destination] objects must implement `CommonParcelable` (which they already do)
         * to be saveable in Android's Bundle.
         *
         * The saver stores:
         * - rootStack: list of non-TopLevel destinations
         * - topLevelStacks: ordered list of (key, stack) pairs preserving tab order
         * - topLevelKey: currently active TopLevel tab (or null)
         *
         * @param startDestination the fallback start destination used to create a fresh NavBackStack
         */
        fun <T : Destination> saver(startDestination: T): Saver<NavBackStack<T>, Any> =
            listSaver(
                save = { navBackStack ->
                    buildList<Any?> {
                        // [0] rootStack as List<T>
                        add(ArrayList(navBackStack.rootStack.toList()))
                        // [1] topLevelStacks keys as List<T>
                        add(ArrayList(navBackStack.topLevelStacks.keys.toList()))
                        // [2] topLevelStacks values as List<List<T>>
                        add(ArrayList(navBackStack.topLevelStacks.values.map { ArrayList(it.toList()) }))
                        // [3] topLevelKey (nullable)
                        add(navBackStack.topLevelKey)
                    }
                },
                restore = { saved ->
                    @Suppress("UNCHECKED_CAST")
                    val rootStackList = saved[0] as List<T>

                    @Suppress("UNCHECKED_CAST")
                    val topLevelKeys = saved[1] as List<T>

                    @Suppress("UNCHECKED_CAST")
                    val topLevelValues = saved[2] as List<List<T>>

                    @Suppress("UNCHECKED_CAST")
                    val savedTopLevelKey = saved[3] as T?

                    val topLevelPairs = topLevelKeys.zip(topLevelValues)

                    NavBackStack(startDestination).apply {
                        restore(rootStackList, topLevelPairs, savedTopLevelKey)
                    }
                },
            )
    }

    var isBottomBarVisible: Boolean by mutableStateOf(false)
        private set

    private fun updateBackStack() {
        backStack.apply {
            clear()
            addAll(rootStack)
            addAll(topLevelStacks.flatMap { it.value })
        }
        backStack.lastOrNull()?.let { updateBottomAppBarVisibility(it) }
    }

    fun addTopLevel(destination: T) {
        // Очищаем root стек при переходе к TopLevel навигации
        rootStack.clear()

        if (topLevelStacks[destination] == null) {
            topLevelStacks[destination] = mutableStateListOf(destination)
        } else {
            topLevelStacks.apply {
                remove(destination)?.let { put(destination, it) }
            }
        }
        topLevelKey = destination
        isBottomBarVisible = true
        updateBackStack()
    }

    fun add(destination: T) {
        if (topLevelKey != null) {
            topLevelStacks[topLevelKey]?.add(destination)
        } else {
            rootStack.add(destination)
        }
        updateBackStack()
    }

    fun removeLast() {
        if (topLevelKey != null && (topLevelStacks[topLevelKey]?.size ?: 0) > 1) {
            topLevelStacks[topLevelKey]?.removeLastOrNull()
        } else if (topLevelStacks.size > 1) {
            val removedKey = topLevelKey
            topLevelStacks.remove(removedKey)
            topLevelKey = topLevelStacks.keys.lastOrNull()
        } else if (rootStack.size > 1) {
            rootStack.removeLastOrNull()
        }

        updateBackStack()
    }

    fun clearAndNavigate(destination: T) {
        rootStack.clear()
        topLevelStacks.clear()
        topLevelKey = null
        rootStack.add(destination)
        updateBackStack()
    }

    /**
     * Replaces the current destination with a new one.
     * Useful for Splash screen to replace itself with Login or Home.
     */
    fun replaceTop(destination: T) {
        if (topLevelKey != null) {
            topLevelStacks[topLevelKey]?.apply {
                removeLastOrNull()
                add(destination)
            }
        } else {
            rootStack.apply {
                removeLastOrNull()
                add(destination)
            }
        }
        updateBackStack()
    }

    /**
     * Clears root stack and navigates to a TopLevel destination.
     * Useful for transitioning from Auth flow to Main app.
     */
    fun clearAndNavigateToTopLevel(destination: T) {
        rootStack.clear()
        topLevelStacks.clear()
        topLevelKey = null
        addTopLevel(destination)
    }

    /**
     * Clears the stack for a specific TopLevel destination back to root.
     * Used when user taps on already selected tab.
     */
    fun clearTopLevel(destination: T) {
        topLevelStacks[destination]?.apply {
            clear()
            add(destination)
        }
        updateBackStack()
    }

    private fun updateBottomAppBarVisibility(destination: T) {
        isBottomBarVisible = destination is BottomBarVisible
    }
}