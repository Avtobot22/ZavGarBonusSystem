package com.zavgar.system.onboarding.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Loyalty
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.ui.graphics.vector.ImageVector
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.onboarding_slide_1_description
import com.zavgar.system.resources.onboarding_slide_1_title
import com.zavgar.system.resources.onboarding_slide_2_description
import com.zavgar.system.resources.onboarding_slide_2_title
import org.jetbrains.compose.resources.StringResource

data class OnboardingPage(
    val icon: ImageVector,
    val titleRes: StringResource,
    val descriptionRes: StringResource,
)

val onboardingPages: List<OnboardingPage> = listOf(
    OnboardingPage(
        icon = Icons.Filled.Loyalty,
        titleRes = Res.string.onboarding_slide_1_title,
        descriptionRes = Res.string.onboarding_slide_1_description,
    ),
    OnboardingPage(
        icon = Icons.Filled.QrCode2,
        titleRes = Res.string.onboarding_slide_2_title,
        descriptionRes = Res.string.onboarding_slide_2_description,
    ),
)
