package com.zavgar.system.history.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.zavgar.system.designsystem.components.icon.AppCircleIcon
import com.zavgar.system.designsystem.components.text.AppTextMain
import com.zavgar.system.designsystem.components.text.AppTextSecondary
import com.zavgar.system.designsystem.modifiers.shimmerAnimation
import com.zavgar.system.history.model.HistoryItem

@Composable
internal fun TransactionItem(item: HistoryItem.TransactionItem) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppCircleIcon(
                imageVector = Icons.Outlined.AddShoppingCart,
                modifier = Modifier.size(28.dp),
                tint = if (item.isIncome) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                backgroundColor = if (item.isIncome) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceContainer
                },
                iconScale = 0.7f
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                AppTextMain(
                    text = item.store,
                    style = MaterialTheme.typography.bodyLarge
                )
                AppTextSecondary(
                    text = item.time,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            AppTextMain(
                text = item.amount,
                style = MaterialTheme.typography.bodyLarge,
                color = if (item.isIncome) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
internal fun LoadingTransactionItem(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .shimmerAnimation(CircleShape)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(16.dp)
                        .clip(MaterialTheme.shapes.small)
                        .shimmerAnimation()
                )
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(12.dp)
                        .clip(MaterialTheme.shapes.small)
                        .shimmerAnimation()
                )
            }

            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(16.dp)
                    .clip(MaterialTheme.shapes.small)
                    .shimmerAnimation()
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    }
}