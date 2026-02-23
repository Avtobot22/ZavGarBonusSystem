package com.zavgar.system.designsystem.components.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zavgar.system.designsystem.components.text.AppTextMain
import com.zavgar.system.designsystem.theme.ZavGarThemePreview

@Composable
fun AppMenuButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDestructive: Boolean = false,
    showChevron: Boolean = true
) {
    val contentColor = if (isDestructive) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = contentColor
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp
        ),
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 40.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier
                    .align(Alignment.CenterStart)
            )

            AppTextMain(
                text = text,
                color = contentColor,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.align(Alignment.Center)
            )

            if (showChevron && !isDestructive) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileMenuPreview() {
    ZavGarThemePreview {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppMenuButton(
                text = "Учетная запись",
                icon = Icons.Default.Person,
                onClick = {}
            )

            AppMenuButton(
                text = "О приложении",
                icon = Icons.Default.Info,
                onClick = {}
            )

            AppMenuButton(
                text = "Выйти",
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                onClick = {},
                isDestructive = true
            )
        }
    }
}