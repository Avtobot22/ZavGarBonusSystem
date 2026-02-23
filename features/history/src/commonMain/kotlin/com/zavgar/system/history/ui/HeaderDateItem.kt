package com.zavgar.system.history.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zavgar.system.designsystem.components.text.AppTextMain
import com.zavgar.system.designsystem.modifiers.shimmerAnimation
import com.zavgar.system.history.model.HistoryItem

@Composable
internal fun DateHeaderItem(item: HistoryItem.DateHeader) {
    AppTextMain(
        text = item.date,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
    )
}

@Composable
internal fun LoadingDateHeaderItem(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
            .width(140.dp)
            .height(18.dp)
            .clip(MaterialTheme.shapes.small)
            .shimmerAnimation()
    )
}