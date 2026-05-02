package com.zavgar.system.designsystem.components.logo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zavgar.system.designsystem.theme.LocalZavGarColors
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.zavgar_header_title
import com.zavgar.system.resources.zavgar_logo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    val colors = LocalZavGarColors.current
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(Res.drawable.zavgar_logo),
            contentDescription = "ZavGar",
            colorFilter = ColorFilter.tint(colors.onAccent),
            modifier = Modifier.height(80.dp).padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(Res.string.zavgar_header_title),
            color = colors.onAccent.copy(alpha = 0.75f),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
        )
    }
}