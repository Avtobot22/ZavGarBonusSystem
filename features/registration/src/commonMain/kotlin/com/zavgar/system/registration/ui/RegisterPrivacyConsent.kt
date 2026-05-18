package com.zavgar.system.registration.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.zavgar.system.designsystem.screen.Screen
import com.zavgar.system.designsystem.theme.ZavGarThemePreview
import com.zavgar.system.designsystem.theme.accent
import com.zavgar.system.designsystem.theme.foregroundSecondary
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.register_privacy_consent_link
import com.zavgar.system.resources.register_privacy_consent_text
import org.jetbrains.compose.resources.stringResource

/**
 * Текст согласия с политикой конфиденциальности под кнопкой регистрации.
 *
 * Часть «политика конфиденциальности» — кликабельная ссылка: по нажатию
 * системный обработчик открывает [privacyPolicyUrl] во внешнем браузере.
 */
@Composable
internal fun RegisterPrivacyConsent(
    privacyPolicyUrl: String,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme

    val annotatedText = buildAnnotatedString {
        withStyle(SpanStyle(color = colors.foregroundSecondary)) {
            append(stringResource(Res.string.register_privacy_consent_text))
            append(" ")
        }
        withLink(
            LinkAnnotation.Url(
                url = privacyPolicyUrl,
                styles = TextLinkStyles(
                    style = SpanStyle(
                        color = colors.accent,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = TextDecoration.Underline,
                    ),
                ),
            ),
        ) {
            append(stringResource(Res.string.register_privacy_consent_link))
        }
    }

    Text(
        text = annotatedText,
        fontSize = 12.sp,
        textAlign = TextAlign.Center,
        modifier = modifier,
    )
}

@Preview
@Composable
private fun RegisterPrivacyConsentPreview() {
    ZavGarThemePreview {
        Screen {
            RegisterPrivacyConsent(privacyPolicyUrl = "https://zavgar.ru")
        }
    }
}
