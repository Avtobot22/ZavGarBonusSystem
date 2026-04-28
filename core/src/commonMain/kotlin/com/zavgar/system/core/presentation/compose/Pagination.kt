package com.zavgar.system.core.presentation.compose

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

/**
 * Triggers [onLoadMore] when the user scrolls near the end of the list.
 *
 * @param lazyListState State of the [LazyColumn] used to detect the last visible item.
 * @param itemsCount Total number of items in the list.
 * @param onLoadMore Callback invoked when more data should be loaded.
 * @param loadThreshold Number of items from the end at which loading is triggered.
 */
@Composable
fun Pagination(
    lazyListState: LazyListState,
    itemsCount: Int,
    loadThreshold: Int = 5,
    onLoadMore: () -> Unit,
) {
    val shouldLoadMore by remember(loadThreshold) {
        derivedStateOf {
            val lastIndex = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            itemsCount > 0 && lastIndex >= itemsCount - loadThreshold
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) onLoadMore()
    }
}
