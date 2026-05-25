package com.zavgar.system.history.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zavgar.system.designsystem.modifiers.shimmerAnimation
import com.zavgar.system.designsystem.theme.foregroundSecondary
import com.zavgar.system.history.model.HistoryItem

@Composable
internal fun DateHeaderItem(item: HistoryItem.DateHeader) {
    val colors = MaterialTheme.colorScheme
    Text(
        text = item.date.uppercase(),
        color = colors.foregroundSecondary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.7.sp,
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 6.dp),
    )
}

@Composable
internal fun LoadingDateHeaderItem(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 6.dp)
            .width(80.dp)
            .height(14.dp)
            .clip(MaterialTheme.shapes.small)
            .shimmerAnimation(),
    )
}
