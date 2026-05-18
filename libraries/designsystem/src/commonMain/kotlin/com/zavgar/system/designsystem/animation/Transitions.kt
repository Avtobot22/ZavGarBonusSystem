package com.zavgar.system.designsystem.animation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith

private const val ANIMATION_DURATION_SHORT = 250

/**
 * Переход с затуханием контента для смены состояния внутри экрана
 * (используется в [com.zavgar.system.designsystem.components.content.AnimatedState]).
 *
 * Навигационные переходы между экранами вынесены в навигационный модуль:
 * `com.zavgar.system.navigationapi.transition.NavTransitions`.
 */
val FadeTransition: ContentTransform = fadeIn(
    animationSpec = tween(
        durationMillis = ANIMATION_DURATION_SHORT,
        easing = LinearEasing,
    ),
) togetherWith fadeOut(
    animationSpec = tween(
        durationMillis = ANIMATION_DURATION_SHORT,
        easing = LinearEasing,
    ),
)
