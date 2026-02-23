package com.zavgar.system.history.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun LoadingHistoryList(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
    ) {
        LoadingDateHeaderItem()
        repeat(3) {
            LoadingTransactionItem()
        }
        LoadingDateHeaderItem()
        repeat(2) {
            LoadingTransactionItem()
        }
        LoadingDateHeaderItem()
        repeat(2) {
            LoadingTransactionItem()
        }
    }
}