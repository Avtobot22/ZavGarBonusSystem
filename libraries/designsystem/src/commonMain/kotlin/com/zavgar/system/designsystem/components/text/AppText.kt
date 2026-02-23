package com.zavgar.system.designsystem.components.text

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zavgar.system.designsystem.theme.ZavGarThemePreview

@Composable
fun AppTextMain(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = MaterialTheme.colorScheme.onBackground,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        text = text,
        modifier = modifier,
        style = style,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun AppTextSecondary(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        text = text,
        modifier = modifier,
        style = style,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis
    )
}

@Preview(showBackground = true, name = "Light Mode", widthDp = 320)
@Composable
fun AppTextPreview() {

    ZavGarThemePreview {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Иерархия заголовков
                Column {
                    AppTextMain(
                        text = "Заголовок экрана",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    AppTextSecondary(
                        text = "Подзаголовок или описание",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                HorizontalDivider()

                // 2. Пример для списка (как в Истории)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        AppTextMain(
                            text = "Магазин 'Пятерочка'",
                            style = MaterialTheme.typography.titleMedium
                        )
                        AppTextSecondary(
                            text = "10 ноя, 14:30",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    AppTextMain(
                        text = "- 1 250 ₽",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                HorizontalDivider()

                // 3. Проверка длинного текста (Ellipsis)
                Column {
                    AppTextMain(text = "Пример длинного текста:")
                    AppTextSecondary(
                        text = "Это очень длинное описание, которое должно обрезаться в конце, если не влезает в одну строку...",
                        maxLines = 1
                    )
                }
            }
        }
    }
}