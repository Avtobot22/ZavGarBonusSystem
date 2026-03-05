package com.zavgar.system.registration.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.zavgar.system.designsystem.screen.Screen
import com.zavgar.system.designsystem.theme.ZavGarThemePreview
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.check_box_link_text
import com.zavgar.system.resources.check_box_static_text
import org.jetbrains.compose.resources.stringResource

private const val PERSONAL_INFO_URL = "https://zavgar.ru/n/v/275/soglasie-na-obrabotku-personalnyh-dannyh-polzovatela"
@Composable
internal fun RegisterCheckBox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = CheckboxColors(
        checkedCheckmarkColor = MaterialTheme.colorScheme.primary,
        uncheckedBorderColor = MaterialTheme.colorScheme.secondary,
        uncheckedCheckmarkColor = Color.Transparent,
        checkedBoxColor = Color.Transparent,
        uncheckedBoxColor = Color.Transparent,
        checkedBorderColor = MaterialTheme.colorScheme.primary,
        disabledBorderColor = Color.Transparent,
        disabledCheckedBoxColor = Color.Transparent,
        disabledIndeterminateBorderColor = Color.Transparent,
        disabledIndeterminateBoxColor = Color.Transparent,
        disabledUncheckedBorderColor = Color.Transparent,
        disabledUncheckedBoxColor = Color.Transparent,
        disabledCheckmarkColor = Color.Transparent
    )

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = true,
            colors = colors,
        )

        LinkText(
            fullText = stringResource(Res.string.check_box_static_text),
            linkText = stringResource(Res.string.check_box_link_text),
        )
    }
}

@Composable
private fun LinkText(
    fullText: String,
    linkText: String,
) {
    val annotatedString = buildAnnotatedString {
        append(fullText)

        val start = fullText.indexOf(linkText)
        val end = start + linkText.length

        addStyle(
            style = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant),
            start = 0,
            end = fullText.length - linkText.length,
        )

        addLink(
            url = LinkAnnotation.Url(
                url = PERSONAL_INFO_URL,
            ),
            start = start,
            end = end,
        )

        addStyle(
            style = SpanStyle(
                fontSize = 14.sp,
                letterSpacing = 0.sp,
                fontStyle = FontStyle.Normal,
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight(weight = 400),
                color = MaterialTheme.colorScheme.primary,
            ),
            start = fullText.length - linkText.length,
            end = fullText.length
        )
    }

    Text(text = annotatedString)
}

@Preview
@Composable
internal fun RegisterCheckBoxPreview() {
    ZavGarThemePreview {
        Screen {
            RegisterCheckBox(
                checked = true,
                onCheckedChange = { }
            )
        }
    }
}