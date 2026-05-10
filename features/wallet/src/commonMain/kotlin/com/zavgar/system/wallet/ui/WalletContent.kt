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
import androidx.compose.material3.Icon
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
import com.zavgar.system.designsystem.components.qrcode.ZavGarQrImage
import androidx.compose.material3.MaterialTheme
import com.zavgar.system.designsystem.theme.*
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.home_top_title_wallet
import com.zavgar.system.resources.refresh_points
import com.zavgar.system.wallet.presentation.WalletIntent
import com.zavgar.system.wallet.presentation.WalletState
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun WalletContent(
    state: WalletState,
    screenState: WalletState.ScreenState.Content,
    onIntent: (WalletIntent) -> Unit,
    modifier: Modifier
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

        QrCard(card = state.phone, balance = screenState.balance)

        Spacer(Modifier.height(16.dp))

        AppPrimaryButton(
            text = stringResource(Res.string.refresh_points),
            onClick = { onIntent(WalletIntent.RefreshBalance) },
            isLoading = screenState.isRefreshing,
            enabled = !screenState.isRefreshing,
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
private fun QrCard(card: String, balance: Int) {
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
        ZavGarQrImage(
            card = card,
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
                text = "баллов",
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
                text = "ЗАРАБОТАНО ЗА МЕСЯЦ",
                color = colors.foregroundSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.6.sp,
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = if (earned != null) "+$earned баллов" else "—",
                color = colors.success,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

private fun formatBalance(value: Int): String {
    val s = value.toString()
    val sb = StringBuilder()
    var counter = 0
    for (i in s.indices.reversed()) {
        sb.append(s[i])
        counter++
        if (counter == 3 && i != 0) {
            sb.append(' ')
            counter = 0
        }
    }
    return sb.reverse().toString()
}

