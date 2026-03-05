package com.zavgar.system.wallet.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zavgar.system.designsystem.components.button.AppOutlinedButton
import com.zavgar.system.designsystem.components.qrcode.AppQrCode
import com.zavgar.system.resources.Res
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
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AppQrCode(
            card = state.phone,
            points = screenState.balance,
        )

        AppOutlinedButton(
            onClick = { onIntent(WalletIntent.RefreshBalance) },
            enabled = !screenState.isRefreshing,
            isLoading = screenState.isRefreshing,
            modifier = Modifier.fillMaxWidth(0.7f),
            contentPadding = PaddingValues(vertical = 10.dp, horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier
                )

                Spacer(Modifier.width(5.dp))

                Text(
                    stringResource(Res.string.refresh_points),
                    modifier = Modifier
                )
            }
        }
    }
}