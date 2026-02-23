package com.zavgar.system.designsystem.components.logo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zavgar.system.designsystem.theme.ZavGarThemePreview
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.colored_logo
import com.zavgar.system.resources.zavgar_logo
import org.jetbrains.compose.resources.painterResource


@Composable
fun AppLogo(
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(Res.drawable.zavgar_logo),
        contentDescription = null,
        modifier = modifier.padding(8.dp)
    )
}

@Composable
fun AppLogoColored(
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(Res.drawable.colored_logo),
        contentDescription = null,
        modifier = modifier.padding(8.dp)
    )
}

@Preview
@Composable
private fun AppLogoPreview() {
    ZavGarThemePreview {
        Column {
            AppLogo(
                modifier = Modifier
            )

            AppLogoColored(
                modifier = Modifier
            )
        }
    }
}
