package com.zavgar.system.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.zavgar.system.resources.Manrope_Bold
import com.zavgar.system.resources.Manrope_Light
import com.zavgar.system.resources.Manrope_Medium
import com.zavgar.system.resources.Manrope_Regular
import com.zavgar.system.resources.Manrope_SemiBold
import com.zavgar.system.resources.Res
import org.jetbrains.compose.resources.Font

@Composable
fun manropeFamily(): FontFamily = FontFamily(
    Font(resource = Res.font.Manrope_Regular, weight = FontWeight.Normal),
    Font(resource = Res.font.Manrope_SemiBold, weight = FontWeight.SemiBold),
    Font(resource = Res.font.Manrope_Bold, weight = FontWeight.Bold),
    Font(resource = Res.font.Manrope_Medium, weight = FontWeight.Medium),
    Font(resource = Res.font.Manrope_Light, weight = FontWeight.Light),
)

@Suppress("LongMethod")
@Composable
fun zavGarTypography(): Typography {
    val manropeFontFamily: FontFamily = manropeFamily()

    return remember(manropeFontFamily) {
        Typography(
            displayLarge = TextStyle(
                fontFamily = manropeFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 57.sp,
                lineHeight = 64.sp,
                letterSpacing = (-0.25).sp,
            ),
            displayMedium = TextStyle(
                fontFamily = manropeFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 45.sp,
                lineHeight = 52.sp,
                letterSpacing = 0.sp,
            ),
            displaySmall = TextStyle(
                fontFamily = manropeFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 36.sp,
                lineHeight = 44.sp,
                letterSpacing = 0.sp,
            ),
            headlineLarge = TextStyle(
                fontFamily = manropeFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 32.sp,
                lineHeight = 40.sp,
                letterSpacing = 0.sp,
            ),
            headlineMedium = TextStyle(
                fontFamily = manropeFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 28.sp,
                lineHeight = 36.sp,
                letterSpacing = 0.sp,
            ),
            headlineSmall = TextStyle(
                fontFamily = manropeFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 24.sp,
                lineHeight = 32.sp,
                letterSpacing = 0.sp,
            ),
            titleLarge = TextStyle(
                fontFamily = manropeFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                letterSpacing = 0.sp,
            ),
            titleMedium = TextStyle(
                fontFamily = manropeFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.1.sp,
            ),
            titleSmall = TextStyle(
                fontFamily = manropeFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp,
            ),
            labelLarge = TextStyle(
                fontFamily = manropeFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp,
            ),
            bodyLarge = TextStyle(
                fontFamily = manropeFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.25.sp,
            ),
            bodyMedium = TextStyle(
                fontFamily = manropeFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.25.sp,
            ),
            bodySmall = TextStyle(
                fontFamily = manropeFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.3.sp,
            ),
            labelMedium = TextStyle(
                fontFamily = manropeFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.5.sp,
            ),
            labelSmall = TextStyle(
                fontFamily = manropeFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.5.sp,
            ),
        )
    }
}
