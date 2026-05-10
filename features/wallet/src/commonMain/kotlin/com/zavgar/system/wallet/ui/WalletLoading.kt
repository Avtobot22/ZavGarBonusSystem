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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.zavgar.system.designsystem.modifiers.shimmerAnimation
import com.zavgar.system.designsystem.theme.border
import com.zavgar.system.designsystem.theme.card
import com.zavgar.system.designsystem.theme.foreground
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.home_top_title_wallet
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun WalletLoading(modifier: Modifier) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxSize()
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(colors.card)
                .padding(horizontal = 18.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .shimmerAnimation(RoundedCornerShape(16.dp)),
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
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .width(140.dp)
                        .shimmerAnimation(RoundedCornerShape(8.dp)),
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shimmerAnimation(RoundedCornerShape(14.dp)),
        )

        Spacer(Modifier.height(20.dp))

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
                    .shimmerAnimation(RoundedCornerShape(13.dp)),
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .height(11.dp)
                        .width(160.dp)
                        .shimmerAnimation(RoundedCornerShape(4.dp)),
                )
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .height(22.dp)
                        .width(110.dp)
                        .shimmerAnimation(RoundedCornerShape(6.dp)),
                )
            }
        }
    }
}
