package com.zavgar.system.designsystem.components.header

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zavgar.system.designsystem.components.logo.AppLogo
import com.zavgar.system.designsystem.theme.accent

/**
 * Оранжевая шапка для экранов авторизации (Auth/Register/Forgot/Confirm).
 */
@Composable
fun ZavGarOrangeHeader(
    modifier: Modifier = Modifier,
    topPadding: Dp = 44.dp,
    bottomPadding: Dp = 44.dp,
) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.accent)
            .padding(top = topPadding, bottom = bottomPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        AppLogo()
    }
}
