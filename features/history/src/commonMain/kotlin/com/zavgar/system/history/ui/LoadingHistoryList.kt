package com.zavgar.system.history.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zavgar.system.designsystem.theme.border

@Composable
internal fun LoadingHistoryList(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        repeat(3) {
            LoadingHistorySection(colors)
        }
    }
}

@Composable
private fun LoadingHistorySection(colors: ColorScheme) {
    LoadingDateHeaderItem()
    repeat(3) {
        LoadingTransactionItem()
        HorizontalDivider(
            color = colors.border,
            thickness = 0.5.dp,
        )
    }
}
