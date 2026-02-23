package com.zavgar.system.designsystem.components.topbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zavgar.system.designsystem.components.icon.AppCircleIcon
import com.zavgar.system.designsystem.components.text.AppTextMain
import com.zavgar.system.designsystem.theme.ZavGarThemePreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        AppTextMain(
            text = title,
            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier
        )
    }
}

@Composable
fun AppProfilTopBar(
    title: String,
    onBackClick: () -> Unit,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier
                .size(48.dp)
                .clickable { onBackClick() }
        )

        AppTextMain(
            text = title,
            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        )

        AppCircleIcon(
            imageVector = Icons.Default.DeleteOutline,
            modifier = Modifier
                .size(48.dp)
                .clickable { onActionClick() }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun AppTopBarPreview() {
    ZavGarThemePreview {
        Column {
            AppTopBar(title = "Входящие")

            AppTopBar(
                title = "Профиль",
            )

            AppTopBar(
                title = "Детали",
            )

            AppProfilTopBar(
                title = "Аккаунт",
                onBackClick = {},
                onActionClick = {}
            )
        }
    }
}