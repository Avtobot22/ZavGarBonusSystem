package com.zavgar.system.designsystem.components.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import com.zavgar.system.designsystem.theme.*

/**
 * Кнопка "назад" из прототипа: квадрат 42×42dp с card-фоном, скруглением 13dp и лёгкой тенью.
 */
@Composable
fun ZavGarBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = "Назад",
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(13.dp)
    Box(
        modifier = modifier
            .size(42.dp)
            .shadow(elevation = 6.dp, shape = shape, clip = false)
            .clip(shape)
            .background(colors.card)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = contentDescription,
            tint = colors.foreground,
        )
    }
}
