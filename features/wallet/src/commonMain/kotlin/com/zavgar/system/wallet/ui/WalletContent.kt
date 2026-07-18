package com.zavgar.system.wallet.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zavgar.system.designsystem.components.button.AppPrimaryButton
import com.zavgar.system.designsystem.theme.accent
import com.zavgar.system.designsystem.theme.border
import com.zavgar.system.designsystem.theme.card
import com.zavgar.system.designsystem.theme.danger
import com.zavgar.system.designsystem.theme.dangerContainer
import com.zavgar.system.designsystem.theme.foreground
import com.zavgar.system.designsystem.theme.foregroundSecondary
import com.zavgar.system.designsystem.theme.onAccent
import com.zavgar.system.designsystem.theme.success
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.home_top_title_wallet
import com.zavgar.system.resources.refresh_points
import com.zavgar.system.resources.wallet_balance_unit
import com.zavgar.system.resources.wallet_monthly_earned_empty
import com.zavgar.system.resources.wallet_monthly_earned_title
import com.zavgar.system.resources.wallet_monthly_earned_value
import com.zavgar.system.resources.wallet_refresh_cooldown
import com.zavgar.system.resources.wallet_stale_banner
import com.zavgar.system.wallet.presentation.WalletIntent
import com.zavgar.system.wallet.presentation.WalletState
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Instant

@Composable
internal fun WalletContent(
    state: WalletState,
    screenState: WalletState.ScreenState.Content,
    onIntent: (WalletIntent) -> Unit,
    onQrClick: () -> Unit,
    modifier: Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(10.dp))

        Text(
            text = stringResource(Res.string.home_top_title_wallet),
            color = colors.foreground,
            fontSize = 38.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 40.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp),
        )

        QrCard(
            card = state.phone,
            balance = screenState.balance,
            onQrClick = onQrClick,
        )

        if (screenState.isStale && screenState.lastUpdatedMillis != null) {
            Spacer(Modifier.height(12.dp))
            StaleBanner(updatedAtMillis = screenState.lastUpdatedMillis)
        }

        Spacer(Modifier.height(16.dp))

        val onCooldown = screenState.timerSeconds > 0
        AppPrimaryButton(
            text = if (onCooldown) {
                stringResource(Res.string.wallet_refresh_cooldown, formatCooldown(screenState.timerSeconds))
            } else {
                stringResource(Res.string.refresh_points)
            },
            onClick = { onIntent(WalletIntent.RefreshBalance) },
            isLoading = screenState.isRefreshing,
            enabled = !screenState.isRefreshing && !onCooldown,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = colors.onAccent,
                )
            },
        )

        Spacer(Modifier.height(20.dp))

        MonthlyEarned(earned = state.monthlyEarned)
    }
}

@Composable
private fun QrCard(
    card: String,
    balance: Int,
    onQrClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(colors.card)
            .padding(horizontal = 18.dp, vertical = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        WalletQrCode(
            card = card,
            onClick = onQrClick,
            modifier = Modifier.size(220.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(colors.border),
        )
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = formatBalance(balance),
                color = colors.accent,
                fontSize = 46.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 46.sp,
            )
            Text(
                text = stringResource(Res.string.wallet_balance_unit),
                color = colors.foregroundSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 6.dp),
            )
        }
    }
}

@Composable
private fun MonthlyEarned(earned: Int?) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(colors.card)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(colors.success.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = colors.success,
                modifier = Modifier.size(22.dp),
            )
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(Res.string.wallet_monthly_earned_title),
                color = colors.foregroundSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.6.sp,
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = if (earned != null) {
                    stringResource(Res.string.wallet_monthly_earned_value, earned)
                } else {
                    stringResource(Res.string.wallet_monthly_earned_empty)
                },
                color = colors.success,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun StaleBanner(updatedAtMillis: Long) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.dangerContainer)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = colors.danger,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = stringResource(Res.string.wallet_stale_banner, formatTime(updatedAtMillis)),
            color = colors.danger,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 18.sp,
        )
    }
}

private const val SECONDS_PER_MINUTE = 60
private const val THOUSANDS_GROUP_SIZE = 3

/** Форматирует оставшиеся секунды кулдауна как m:ss. */
private fun formatCooldown(totalSeconds: Int): String {
    val minutes = totalSeconds / SECONDS_PER_MINUTE
    val seconds = totalSeconds % SECONDS_PER_MINUTE
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}

/** Форматирует epoch millis как HH:MM в локальной таймзоне. */
private fun formatTime(epochMillis: Long): String {
    val dateTime = Instant.fromEpochMilliseconds(epochMillis)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    val hour = dateTime.hour.toString().padStart(2, '0')
    val minute = dateTime.minute.toString().padStart(2, '0')
    return "$hour:$minute"
}

private fun formatBalance(value: Int): String {
    val s = value.toString()
    val sb = StringBuilder()
    var counter = 0
    for (i in s.indices.reversed()) {
        sb.append(s[i])
        counter++
        if (counter == THOUSANDS_GROUP_SIZE && i != 0) {
            sb.append(' ')
            counter = 0
        }
    }
    return sb.reverse().toString()
}
