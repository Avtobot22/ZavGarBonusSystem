package com.zavgar.system.designsystem.animation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith

private const val ANIMATION_DURATION_SHORT = 250
private const val ANIMATION_DURATION_MEDIUM = 300

/**
 * Переход с затуханием контента
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

/**
 * Transition used to fade in the content.
 */
val FadeInTransition: ContentTransform = fadeIn(
    animationSpec = tween(
        durationMillis = 200,
        easing = LinearEasing,
    ),
) togetherWith fadeOut(
    animationSpec = tween(
        durationMillis = 200,
        easing = LinearEasing,
    ),
)

/**
 * Transition used to fade out the content.
 */
val FadeOutTransition: ContentTransform =
    fadeIn(
        animationSpec = tween(
            durationMillis = 200,
            easing = LinearEasing,
        ),
    ) togetherWith fadeOut(
        animationSpec = tween(
            durationMillis = 200,
            easing = LinearEasing,
        ),
    )

/**
 * Exit: Бар уезжает ВНИЗ и исчезает
 */
val BottomBarExitTransition: ExitTransition = slideOutVertically(
    targetOffsetY = { fullHeight -> fullHeight },
    animationSpec = tween(
        durationMillis = ANIMATION_DURATION_MEDIUM,
        easing = androidx.compose.animation.core.FastOutSlowInEasing
    )
) + fadeOut(
    animationSpec = tween(
        durationMillis = ANIMATION_DURATION_MEDIUM,
        easing = androidx.compose.animation.core.FastOutSlowInEasing
    )
)

/**
 * Enter: Бар выезжает СНИЗУ и появляется
 */
val BottomBarEnterTransition: EnterTransition = slideInVertically(
    initialOffsetY = { fullHeight -> fullHeight },
    animationSpec = tween(
        durationMillis = ANIMATION_DURATION_MEDIUM,
        easing = androidx.compose.animation.core.FastOutSlowInEasing
    )
) + fadeIn(
    animationSpec = tween(
        durationMillis = ANIMATION_DURATION_MEDIUM,
        easing = androidx.compose.animation.core.FastOutSlowInEasing
    )
)